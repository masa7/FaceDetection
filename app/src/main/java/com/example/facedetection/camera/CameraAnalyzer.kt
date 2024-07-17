package com.example.facedetection.camera

import android.graphics.Rect
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.example.facedetection.graphic.GraphicOverlay
import com.example.facedetection.graphic.RectangleOverlay

class CameraAnalyzer(
    private val overlay: GraphicOverlay<*>
) : BaseCameraAnalyzer<List<Face>>() {

    override val graphicOverlay: GraphicOverlay<*>
        get() = overlay

    private val cameraOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setMinFaceSize(0.10f)
        .enableTracking()
        .build()

    //https://developers.google.com/android/reference/com/google/mlkit/vision/face/FaceDetectorOptions.Builder

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
        graphicOverlay.clear()
        results.forEach {
            val faceGraphic = RectangleOverlay(graphicOverlay, it, rect)
            graphicOverlay.add(faceGraphic)
        }
        graphicOverlay.postInvalidate()
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "onFailure : $e")
    }

    companion object {
        private const val  TAG = "CameraAnalyzer"
    }
}