package com.visioncameraobjectdetector

import android.annotation.SuppressLint
import android.graphics.Rect
import android.media.Image
import androidx.camera.core.ImageProxy
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin
import kotlin.Any as Any


class VisionCameraObjectDetectorPlugin internal constructor() :
  FrameProcessorPlugin("detectObjects") {
  var options: ObjectDetectorOptions = ObjectDetectorOptions.Builder()
    .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
    .build()

  var objectDetector: ObjectDetector = ObjectDetection.getClient(options)

  private fun processBoundingBox(boundingBox: Rect): WritableMap {
    val bounds: WritableMap = Arguments.createMap()
    val offsetX: Double = (boundingBox.exactCenterX() - Math.ceil(boundingBox.width().toDouble())) / 2.0f
    val offsetY: Double = (boundingBox.exactCenterY() - Math.ceil(boundingBox.height().toDouble())) / 2.0f
    val x: Double = boundingBox.right + offsetX
    val y: Double = boundingBox.top + offsetY
    bounds.putDouble("x", boundingBox.centerX() + (boundingBox.centerX() - x))
    bounds.putDouble("y", boundingBox.centerY() + (y - boundingBox.centerY()))
    bounds.putDouble("width", boundingBox.width().toDouble())
    bounds.putDouble("height", boundingBox.height().toDouble())
    bounds.putDouble("left", boundingBox.left.toDouble())
    bounds.putDouble("top", boundingBox.top.toDouble())
    bounds.putDouble("right", boundingBox.right.toDouble())
    bounds.putDouble("bottom", boundingBox.bottom.toDouble())
    return bounds
  }

  override fun callback(imageProxy: ImageProxy, params: Array<Any>): Any? {
    @SuppressLint("UnsafeOptInUsageError")
    val mediaImage: Image? = imageProxy.getImage()
    if (mediaImage != null) {
      val image: InputImage =
        InputImage.fromMediaImage(
          mediaImage,
          imageProxy.imageInfo.rotationDegrees
        )
      val task: Task<List<DetectedObject>> = objectDetector.process(image)
      val array = WritableNativeArray()
      try {
        val detectedObjects: List<DetectedObject> = Tasks.await(task)
        for (detectedObject in detectedObjects) {
          val map: WritableMap = WritableNativeMap()
          val bounds: WritableMap = processBoundingBox(detectedObject.boundingBox)
          map.putMap("bounds", bounds)
          map.putInt("width", image.width)
          map.putInt("height", image.height)

          detectedObject.trackingId?.let { map.putInt("trackingId", it) }

          array.pushMap(map)
        }
        return array
      } catch (e: java.lang.Exception) {
        e.printStackTrace()
      }
    }
    return null
  }
}
