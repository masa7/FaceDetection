package com.example.facedetection.graphic

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.google.mlkit.vision.face.Face
import com.example.facedetection.utils.CameraUtils
import com.google.android.gms.internal.phenotype.zzh.init


// import for count down function
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.facedetection.utils.FileUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
// import for data export
import com.example.facedetection.utils.SingletonContext
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class RectangleOverlay(
    private val graphicOverlay: GraphicOverlay<*>,
    private val face: Face,
    private val rect: Rect
) : GraphicOverlay.Graphic(graphicOverlay) {

    private val boxPaint : Paint = Paint()

    // additional parameters
    // for count down method
    var isDetected = false
    private val countdownTime = 5
    private lateinit var job: Job
    // for data export
    private var fileTracking: FileUtils
    private var fileCountdown: FileUtils

    init {
        // for face detection
        boxPaint.color = Color.GREEN
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = 3.0f
        // for data export
        fileTracking = FileUtils("TrackingID4.txt")
        fileCountdown = FileUtils("Test.txt")
    }

    override fun draw(canvas: Canvas) {
        val rect = CameraUtils.calculateRect(
            graphicOverlay,
            rect.height().toFloat(),
            rect.width().toFloat(),
            face.boundingBox
        )
        canvas.drawRect(rect, boxPaint)
    }

    private fun cancelCountDown() {
        if (::job.isInitialized && job.isActive) {
            job.cancel()
        }
    }

    fun setColor(){
        boxPaint.color = Color.RED
    }
    fun countDown(){
        isDetected = true
        val scope = CoroutineScope(Dispatchers.Default)
        val handler = Handler(Looper.getMainLooper())
        job = scope.launch {
            try {
                handler.post(
                    kotlinx.coroutines.Runnable { boxPaint.color = Color.BLUE }
                )

                repeat(countdownTime) { i ->
                    delay(1000L)
                    val times = i+1
                    fileCountdown.saveFile(times.toString())
                    fileCountdown.saveFile("\n")
                    handler.post(
                        kotlinx.coroutines.Runnable { boxPaint.color = Color.RED }
                    )
                }
            } finally {
                if(job.isActive){
                    // カウントダウン後の処理を記述
                    isDetected = false
                    handler.post(
                        kotlinx.coroutines.Runnable { boxPaint.color = Color.RED }
                    )
                    scope.cancel()
                }else{
                    isDetected = false
                }
            }
        }
    }

}