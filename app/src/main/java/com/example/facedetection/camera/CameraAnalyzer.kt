package com.example.facedetection.camera

import java.time.LocalDateTime
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import androidx.camera.core.processing.SurfaceProcessorNode.In
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.example.facedetection.graphic.GraphicOverlay
import com.example.facedetection.graphic.RectangleOverlay
import com.example.facedetection.utils.FileUtils
import com.google.android.gms.internal.phenotype.zzh.init
import com.google.mlkit.vision.face.FaceLandmark
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class CameraAnalyzer(
    private val overlay: GraphicOverlay<*>
) : BaseCameraAnalyzer<List<Face>>() {

    private var file: FileUtils
    private var intervalSec: Int
    private var prevSec: Int
    // for tracking id
    private var idList: MutableList<Int?> = mutableListOf()

    init {
        file = FileUtils("TrackingID-2.txt")
        intervalSec = 1
        prevSec = 0
    }

    override val graphicOverlay: GraphicOverlay<*>
        get() = overlay

    private val cameraOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        //.setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        //.setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setMinFaceSize(0.10f)
        .enableTracking()
        .build()

    private val detector = FaceDetection.getClient(cameraOptions)

    override fun detectInImage(image: InputImage): Task<List<Face>> {
        return detector.process(image)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: Exception) {
            Log.e(TAG, "stop: $e")
        }
    }

    override fun onSuccess(results: List<Face>, graphicOverlay: GraphicOverlay<*>, rect: Rect) {
        // for log recording
        val dateAndTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh:mm:ss"))
        val curSec = LocalDateTime.now().second

        if (abs(curSec - prevSec) >= 1) {
            graphicOverlay.clear()
            results.forEach {
                val faceGraphic = RectangleOverlay(graphicOverlay, it, rect)
                // for face tracking
                if (it.trackingId != null) {
                    val id = it.trackingId

                    if (id in idList) {
                        file.saveFile(dateAndTime)
                        file.saveFile(", ")
                        file.saveFile(id.toString())
                        file.saveFile("\n")

                        faceGraphic.setColor("RED")
                    } else {
                        idList.add(id)
                    }
                }
                graphicOverlay.add(faceGraphic)
            }
            graphicOverlay.postInvalidate()
            prevSec = curSec
        }
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "onFailure : $e")
    }

    companion object {
        private const val  TAG = "CameraAnalyzer"
    }

}