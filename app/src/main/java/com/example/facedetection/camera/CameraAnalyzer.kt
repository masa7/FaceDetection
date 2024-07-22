package com.example.facedetection.camera

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

class CameraAnalyzer(
    private val overlay: GraphicOverlay<*>
) : BaseCameraAnalyzer<List<Face>>() {

    private var file: FileUtils
    // for tracking id
    private var idList: MutableList<Int?> = mutableListOf()

    init {
        file = FileUtils("TrackingID-1.txt")
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
        graphicOverlay.clear()
        results.forEach {
            val faceGraphic = RectangleOverlay(graphicOverlay, it, rect)
            // for face tracking
            if(it.trackingId != null){
                val id = it.trackingId

                if(id in idList){
                    faceGraphic.setColor()
                    file.saveFile(id.toString())
                    file.saveFile("\n")
                }else{
                    idList.add(id)
                    //file.saveFile(id.toString())
                    //file.saveFile(", ")
                    //file.saveFile(idList.size.toString())
                    //file.saveFile("\n")
                }
            }
            graphicOverlay.add(faceGraphic)

            // for count down
            //if(!faceGraphic.isDetected){
            //    faceGraphic.countDown()
            //    graphicOverlay.add(faceGraphic)
            //}
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