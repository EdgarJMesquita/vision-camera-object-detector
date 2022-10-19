import CoreML
import MLKitObjectDetection
import MLKitVision
import CoreML
import UIKit
import AVFoundation

@objc(VisionCameraObjectDetector)
public class VisionCameraObjectDetector: NSObject, FrameProcessorPluginBase {
 
  static var objectDetectorOptions: ObjectDetectorOptions = {
    let option = ObjectDetectorOptions()
    option.shouldEnableClassification = false
    option.detectorMode = .stream
    option.shouldEnableMultipleObjects = false
    return option
  }()

  static var objectDetector = ObjectDetector.objectDetector(options: objectDetectorOptions)

  private static func processBoundingBox(from object: Object,width:Int, height:Int) -> [String: Any]{
    let boundingBox = object.frame
    var bounds: [String: Any] = [:]
    
    var origin:[String: Any] = [:]
    let offsetX = (boundingBox.midX - ceil(boundingBox.width)) // 2.0
    let offsetY = (boundingBox.midY - ceil(boundingBox.height)) // 2.0
    let x = boundingBox.maxX + offsetX
    let y = boundingBox.minY + offsetY
    origin["x"] = boundingBox.midX + (boundingBox.minX - x)
    origin["y"] = boundingBox.midY + (y - boundingBox.midY)
    bounds["origin"] = origin

    
    var size :[String: Any] = [:]
    size["width"] = boundingBox.width
    size["height"] = boundingBox.height
    bounds["size"] = size

      
    var relativeOrigin :[String: Any] = [:]
    relativeOrigin["top"] = percentage(x: Float(boundingBox.minY),y: Float(width))
    relativeOrigin["left"] = percentage(x: Float(boundingBox.minX),y: Float(height))
    bounds["relativeOrigin"] = relativeOrigin

    var relativeSize:[String: Any] = [:]
    relativeSize["width"] = percentage(x: Float(boundingBox.width),y: Float(height))
    relativeSize["height"] = percentage(x: Float(boundingBox.height),y: Float(width))
    bounds["relativeSize"] = relativeSize
        
    return bounds
  
  }
    
  private static func percentage(x:Float, y:Float)->Float {
      return (x * 100)/y
    }
    
  @objc
  public static func callback(_ frame: Frame!, withArgs _: [Any]!) -> Any! {
    let image = VisionImage(buffer: frame.buffer)
    image.orientation = .up
    
    let imgBuffer = CMSampleBufferGetImageBuffer(frame.buffer)
    let width = CVPixelBufferGetWidth(imgBuffer!)
    let height = CVPixelBufferGetHeight(imgBuffer!)
      
    var array: [Any] = []
      
    do {
          let objects =  try objectDetector.results(in: image)
          if (!objects.isEmpty){
              for object in objects {
                  var map: [String: Any] = [:]
                  map["trackingId"] = object.trackingID
                  
                  map["bounds"] = processBoundingBox(from: object, width: width, height: height)
                  
                  array.append(map)
              }
          }
      } catch _ {
          return nil
      }
    
    return array
  }
}