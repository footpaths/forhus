package com.example.pc.huskotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.support.annotation.RequiresApi
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import com.example.pc.huskotlin.model.Upload
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RecordingActivity :  Activity() {
    private var mScreenDensity: Int = 0
    private var mProjectionManager: MediaProjectionManager? = null
    var mMediaProjection: MediaProjection? = null
    var mVirtualDisplay: VirtualDisplay? = null
     var mMediaRecorder: MediaRecorder? = null
     private var filePath: String? = null
    var filePathDelete: String? = null
    private lateinit var myRef: DatabaseReference
    var mStorage: StorageReference? = null
    var timer: CountDownTimer? = null
    private var mDatabase: DatabaseReference? = null


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStorage = FirebaseStorage.getInstance().getReference(Constants.STORAGE_PATH_UPLOADS)
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS)
        instance = this
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        mScreenDensity = metrics.densityDpi
        mMediaRecorder = MediaRecorder()
        mProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        initRecorder()
        Log.d("RecorderService", "da cau hinh xong")
        shareScreen()
        object : CountDownTimer(2000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                // Toast.makeText(this@ScreenRecordingActivity, "còn : "+ (millisUntilFinished / 1000).toString(), Toast.LENGTH_SHORT).show()
                // count.setText("Time remaining " + (millisUntilFinished / 1000).toString())
            }

            override fun onFinish() {

                moveTaskToBack(true)
                gettime()
            }
        }.start()

    }
    private fun gettime() {

        timer = object : CountDownTimer(300000, 1000) {
       // timer = object : CountDownTimer(30000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                //   Toast.makeText(this@ScreenRecordingActivity, "còn : " + (millisUntilFinished / 1000).toString(), Toast.LENGTH_SHORT).show()
                // count.setText("Time remaining " + (millisUntilFinished / 1000).toString())
                //   println("tu dong tat xong" + (millisUntilFinished / 1000).toString())

            }

            override fun onFinish() {
                try {

                    moveTaskToBack(true)
                    mMediaRecorder!!.stop()
                    mMediaRecorder!!.reset()
                    //stopScreenSharing()
                    //   println("tu dong tat xong")


                    upload()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }

            }
        }.start()
    }

    fun upload() {


        val file = Uri.fromFile(File(filePath))
        var mReference = mStorage!!.child(file.lastPathSegment)

        println(file.lastPathSegment)
        try {

            mReference.putFile(file).addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
                @SuppressLint("VisibleForTests")
                override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot?) {
                    println(taskSnapshot)

                    val upload = Upload(getCurSysDate(), taskSnapshot!!.downloadUrl!!.toString())
                    val uploadId = mDatabase!!.push().key
                    mDatabase!!.child(uploadId!!).setValue(upload)

                    val fdelete = File(filePath)
                    fdelete.delete()
                    Log.d("RecorderService", "da up xong")
                }

            }).addOnFailureListener(object : OnFailureListener {
                override fun onFailure(p0: java.lang.Exception) {
                    println(p0)
                    val fdelete = File(filePath)
                    fdelete.delete()
                    Log.d("RecorderService", "khong up dc")

                }
            })

//            taskSnapshot: UploadTask.TaskSnapshot? -> var url = taskSnapshot!!
//            Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()


        } catch (e: Exception) {
            println(e)
            try {
                val fdelete = File(filePath)
                fdelete.delete()
                Log.d("RecorderService", "vao catch xoa file")
            } catch (e: Exception) {
                println(e)
            }

            // Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }


    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        try {
            Log.d("RecorderService", "da vao result")
            if (requestCode != REQUEST_CODE) {
                //       Log.e(TAG, "Unknown request code: $requestCode")
                return
            }
            if (resultCode != Activity.RESULT_OK) {
                //  Toast.makeText(this, "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show()
                //isRecording = false
                //  actionBtnReload()
                return
            }
            mMediaProjection = mProjectionManager!!.getMediaProjection(resultCode, data)
            mVirtualDisplay = createVirtualDisplay()
            mMediaRecorder!!.start()
            Log.d("RecorderService", "da startttttttttttt")
        }catch (e:Exception){
            e.printStackTrace()
        }



        // actionBtnReload()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private inner class MediaProjectionCallback : MediaProjection.Callback() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onStop() {


                // isRecording = false
                //actionBtnReload()
                mMediaRecorder!!.stop()
                mMediaRecorder!!.reset()


           // stopScreenSharing()
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun stopScreenSharing() {

        if (mVirtualDisplay == null) {
            return
        }
        mVirtualDisplay!!.release()
        destroyMediaProjection()
        //isRecording = false
        // actionBtnReload()
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun destroyMediaProjection() {
        if (mMediaProjection != null) {
             mMediaProjection!!.stop()
            mMediaProjection = null
        }
        //   Log.i(TAG, "MediaProjection Stopped")
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun shareScreen() {
        try {
            Log.d("RecorderService", "bat dau start")
            if (mMediaProjection == null) {
                startActivityForResult(mProjectionManager!!.createScreenCaptureIntent(), REQUEST_CODE)
                return
            }
            mVirtualDisplay = createVirtualDisplay()
            mMediaRecorder!!.start()
            Log.d("RecorderService", "da start")
        }catch (e:Exception){
            println(e)
        }



        //   Log.d(TAG, "media start")
        // isRecording = true
        // actionBtnReload()
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createVirtualDisplay(): VirtualDisplay {
        return mMediaProjection!!.createVirtualDisplay("MainActivity", DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder!!.surface, null, null)
    }

    fun getCurSysDate(): String {
        return SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Date())
    }
    fun stopCountTimer() {
        if (timer != null) {
            timer!!.cancel()
        }
    }
    fun getFilePath(): String? {
        val directory = Environment.getExternalStorageDirectory().toString() + File.separator + "Systems"
        if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()) {
            // Toast.makeText(this, "Failed to get External Storage", Toast.LENGTH_SHORT).show()
            return null
        }
        val folder = File(directory)
        var success = true
        if (!folder.exists()) {
            success = folder.mkdir()
        }

        if (success) {
            val videoName = "info" + getCurSysDate() + ".mp4"
            filePath = directory + File.separator + videoName
        } else {
            // Toast.makeText(this, "Failed to create Recordings directory", Toast.LENGTH_SHORT).show()
            return null
        }
        filePathDelete = filePath
        return filePath
    }

    private fun initRecorder() {
        try {
            mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            mMediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.SURFACE)
            mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) //THREE_GPP
            mMediaRecorder!!.setOutputFile(getFilePath())
            mMediaRecorder!!.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT)
            mMediaRecorder!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            mMediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mMediaRecorder!!.setVideoFrameRate(16) // 30
            //mMediaRecorder!!.setVideoEncodingBitRate(3000000)
            mMediaRecorder!!.setVideoEncodingBitRate(380 * 1000)

            val rotation = windowManager.defaultDisplay.rotation
            val orientation = ORIENTATIONS.get(rotation + 90)
            mMediaRecorder!!.setOrientationHint(orientation)
            mMediaRecorder!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    companion object {

        private val TAG = "MainActivity"
        private val REQUEST_CODE = 1000
        // private val DISPLAY_WIDTH = 720
        //private val DISPLAY_HEIGHT = 1280
        private val DISPLAY_WIDTH = 480 //720
        private val DISPLAY_HEIGHT = 640 //1280
        private val ORIENTATIONS = SparseIntArray()
        private val REQUEST_PERMISSION_KEY = 1

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }

        lateinit var instance: RecordingActivity
            private set
    }

}
