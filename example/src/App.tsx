import * as React from 'react';
import { runOnJS } from 'react-native-reanimated';
import { StyleSheet, View } from 'react-native';
import { Camera } from 'react-native-vision-camera';
import { DetectedObject, detectObjects } from 'vision-camera-object-detector';
import {
  useCameraDevices,
  useFrameProcessor,
} from 'react-native-vision-camera';

export default function App() {
  const [hasPermission, setHasPermission] = React.useState(false);
  const [objects, setObjects] = React.useState<DetectedObject[]>([]);
  const devices = useCameraDevices();
  const device = devices.back;

  React.useEffect(() => {
    (async () => {
      const status = await Camera.requestCameraPermission();
      setHasPermission(status === 'authorized');
    })();
  }, []);

  const frameProcessor = useFrameProcessor((frame) => {
    'worklet';
    const detectedObjects = detectObjects(frame);
    runOnJS(setObjects)(detectedObjects);
  }, []);

  return device != null && hasPermission ? (
    <View style={styles.container}>
      <Camera
        style={StyleSheet.absoluteFill}
        device={device}
        isActive={true}
        frameProcessor={frameProcessor}
        frameProcessorFps={25}
      />
      {!!objects &&
        objects.map((obj) => (
          <View
            key={obj?.trackingId}
            style={[
              styles.rect,
              {
                top: obj.bounds.relativeOrigin.top + '%',
                left: obj.bounds.relativeOrigin.left + '%',
                width: obj.bounds.relativeSize.width + '%',
                height: obj.bounds.relativeSize.height + '%',
              },
            ]}
          />
        ))}
    </View>
  ) : null;
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    width: '100%',
  },
  rect: {
    position: 'absolute',
    borderWidth: 0.5,
    borderColor: 'white',
  },
});
