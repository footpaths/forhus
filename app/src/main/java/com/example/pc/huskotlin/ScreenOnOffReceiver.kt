package com.example.pc.huskotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.firebase.database.*
import java.io.File

class ScreenOnOffReceiver : BroadcastReceiver() {
    var memoContext: Context? = null
    var myRef: DatabaseReference? = null
    var checkFirstOff: Boolean = false
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        var database = FirebaseDatabase.getInstance()
        myRef = database.getReference("message")

        this.memoContext = context

        if (Intent.ACTION_SCREEN_OFF == action) {
            Log.d(SCREEN_TOGGLE_TAG, "Screen is turn off.")
            //  mainActivity.StopRecording();
            var firstOff = ScreenPreference.getInstance(memoContext!!).saveAppName

            if (firstOff.contains("false")) {
                Log.d(TAG, "offf dauuuuuuuuuuuuuuuuu")
                firstOff = "true"
                ScreenPreference.getInstance(memoContext!!).saveAppName = firstOff


            } else {
                Log.d(TAG, "offf nnnnnnnnnnnnnnnnnnnn")
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
                            ScreenPreference.getInstance(context).saveStatus = "false"
                           // RecordingActivity.instance.upload()
                           // RecordingActivity.instance.stopCountTimer()
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                }


            }


        }
        if (Intent.ACTION_SCREEN_ON == action) {
            Log.d(TAG, "Screen is turn on.")
            checkOnOff()

        }
        Log.d(TAG, "StartUpReceiver onReceive() was called")

        if ("android.intent.action.BOOT_COMPLETED" == intent.action) {

            val newIntent = Intent(context.applicationContext, ScreenOnOffBackgroundService::class.java)
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            memoContext!!.startActivity(newIntent)
            Log.d(TAG, "StartUpReceiver onReceive() was called again")

        }


    }

    private fun checkOnOff() {
        myRef!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(String::class.java)
                Log.d(TAG, "Value is: $value")
                if (value!!.contains("true")) {

                    Log.d(TAG, "screen is unlock check off...")
                    var inten = Intent(memoContext, RecordingActivity::class.java)
                    inten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    inten.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    memoContext!!.startActivity(inten)

                } else {
                    try {
                        if (RecordingActivity.instance.timer != null) {
                            RecordingActivity.instance.timer!!.cancel()

                        }

                    } catch (e: Exception) {

                    }

                }
            }
        })

    }

    companion object {
        private val SCREEN_TOGGLE_TAG = "SCREEN_TOGGLE_TAG"

        private val TAG = "RecorderService"
    }


}
