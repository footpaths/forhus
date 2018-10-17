package com.example.pc.huskotlin

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.SparseIntArray
import android.view.Surface
import com.google.android.gms.common.api.GoogleApiClient



class MainActivity :  Activity() {
    private val TAG = MyLocationUsingHelper::class.java!!.getSimpleName()

    private val PLAY_SERVICES_REQUEST = 1000
    private val REQUEST_CHECK_SETTINGS = 2000

    private val mLastLocation: Location? = null

    // Google client to interact with Google API

    private val mGoogleApiClient: GoogleApiClient? = null

    var latitude: Double = 0.toDouble()
    var longitude: Double = 0.toDouble()
    val MULTIPLE_PERMISSIONS = 10 // code you want.
    var permissions = arrayOf<String>(Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenPreference.getInstance(this).saveAppName = "false"
        //setContentView(R.layout.activity_main)
        instance = this
        //val p = packageManager
       // val componentName = ComponentName(this@MainActivity, MainActivity::class.java!!) // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
       // p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)


        val backgroundService = Intent(applicationContext, ScreenOnOffBackgroundService::class.java)
        startService(backgroundService)


        if (checkPermissions()) {

        }


    }
       fun hideIcon (){
        val pp = packageManager
        val componentNamep = ComponentName(this@MainActivity, MainActivity::class.java!!) // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
        pp.setComponentEnabledSetting(componentNamep, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
    }
    private fun checkPermissions(): Boolean {
        var result: Int
        val listPermissionsNeeded: ArrayList<String> = ArrayList()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(this@MainActivity, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MULTIPLE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                } else {
                    var perStr = ""
                    for (per in permissions) {
                        perStr += "\n" + per
                    }
                    // permissions list of don't granted permission
                }
                return
            }
        }
    }

    companion object {


        lateinit var instance: MainActivity
            private set
    }
}
