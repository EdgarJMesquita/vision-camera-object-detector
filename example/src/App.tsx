import * as React from 'react';
import { runOnJS } from 'react-native-reanimated';
import { StyleSheet, View } from 'react-native';
import { Camera } from 'react-native-vision-camera';
import { detectObjects } from 'vision-camera-object-detector';
import {
  useCameraDevices,
  useFrameProcessor,
} from 'react-native-vision-camera';

export default function App() {
  const [hasPermission, setHasPermission] = React.useState(false);
  const [objects, setObjects] = React.useState([]);
  const devices = useCameraDevices();
  const device = devices.front;

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
        objects.map((obj) => <View style={{ position: 'absolute' }} />)}
    </View>
  ) : null;
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    width: '100%',
  },
});
