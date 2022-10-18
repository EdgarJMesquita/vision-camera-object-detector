import CoreML
import MLKitObjectDetection
import MLKitVision
import CoreML

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
    
  @objc
  public static func callback(_ frame: Frame!, withArgs _: [Any]!) -> Any! {
    let image = VisionImage(buffer: frame.buffer)
    image.orientation = .up
      
    do {
          let objectDetects =  try objectDetector.results(in: image)
          if (!objectDetects.isEmpty){
              for objectDetected in objectDetects {
                  var map: [String: Any] = [:]
                  
                  
                  
                 // faceAttributes.append(map)
              }
          }
      } catch _ {
          return nil
      }
    // code goes here
    return []
  }
}
