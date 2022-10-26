package com.visioncameraobjectdetector

import android.annotation.SuppressLint
import android.graphics.Rect
import android.media.Image
import androidx.camera.core.ImageProxy
import com.facebook.react.bridge.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin
import kotlin.math.ceil
import kotlin.Any as Any


class VisionCameraObjectDetectorPlugin internal constructor() :
  FrameProcessorPlugin("detectObjects") {
  private var options: ObjectDetectorOptions = ObjectDetectorOptions.Builder()
    .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
    .build()

  var objectDetector: ObjectDetector = ObjectDetection.getClient(options)

  private fun processBoundingBox(boundingBox: Rect, width: Int, height: Int): WritableMap {
    val bounds: WritableMap = Arguments.createMap()

    val origin = WritableNativeMap()
    val offsetX: Double = (boundingBox.exactCenterX() - ceil(boundingBox.width().toDouble())) / 2.0f
    val offsetY: Double = (boundingBox.exactCenterY() - ceil(boundingBox.height().toDouble())) / 2.0f
    val x: Double = boundingBox.right + offsetX
    val y: Double = boundingBox.top + offsetY

    origin.putDouble("x", boundingBox.centerX() + (boundingBox.centerX() - x))
    origin.putDouble("y", boundingBox.centerY() + (y - boundingBox.centerY()))
    bounds.putMap("origin",origin)

    val size = WritableNativeMap()
    size.putDouble("width", boundingBox.width().toDouble())
    size.putDouble("height", boundingBox.height().toDouble())
    bounds.putMap("size",size)

    val relativeOrigin = WritableNativeMap()
    relativeOrigin.putInt("top",percentage(boundingBox.top,width))
    relativeOrigin.putInt("left",percentage(boundingBox.left,height))
    bounds.putMap("relativeOrigin",relativeOrigin)

    val relativeSize = WritableNativeMap()
    relativeSize.putInt("width",percentage(boundingBox.width(),height))
    relativeSize.putInt("height",percentage(boundingBox.height(),width))
    bounds.putMap("relativeSize",relativeSize)

    return bounds
  }

  private fun percentage(x:Int, y:Int): Int {
    return (x * 100)/y
  }

  private fun processClassification(labels: MutableList<DetectedObject.Label>): WritableNativeArray {
    val array = WritableNativeArray()
    for (label in labels) {
      val map = WritableNativeMap()
      map.putString("text",label.text)
      map.putInt("index",label.index)
      map.putDouble("confidence", label.confidence.toDouble())
      array.pushMap(map)
    }
    return array
  }

  private fun handleArguments(params: Array<Any>){
    if(params.isEmpty()) return
    val arguments = params[0];
    if(arguments is ReadableMap){

      val enableClassification = if (arguments.hasKey("enableClassification")) arguments.getBoolean("enableClassification") else false
      val enableMultipleObjects = if(arguments.hasKey("enableMultipleObjects")) arguments.getBoolean("enableMultipleObjects") else false
      val hasEnableClassificationChanged = options.isClassificationEnabled != enableClassification
      val hasEnableMultipleObjects = options.isMultipleObjectsEnabled != enableMultipleObjects

      if(!hasEnableClassificationChanged and !hasEnableMultipleObjects) return

      val newOptions = ObjectDetectorOptions.Builder()

      if(enableClassification){
        newOptions.enableClassification()
      }

      if(enableMultipleObjects){
        newOptions.enableMultipleObjects()
      }
      newOptions.setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
      options = newOptions.build()
      objectDetector = ObjectDetection.getClient(options)
    }
  }

  override fun callback(imageProxy: ImageProxy, params: Array<Any>): Any? {
    handleArguments(params)
    @SuppressLint("UnsafeOptInUsageError")
    val mediaImage: Image? = imageProxy.image
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

          val bounds: WritableMap = processBoundingBox(detectedObject.boundingBox, image.width, image.height)
          map.putMap("bounds", bounds)

          val imgInfo = WritableNativeMap()
          imgInfo.putInt("width",image.width)
          imgInfo.putInt("height",image.height)
          map.putMap("image",imgInfo)

          detectedObject.trackingId?.let { map.putInt("trackingId", it) }

          map.putArray("labels",processClassification(detectedObject.labels))

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
