# vision-camera-object-detector

Vision Camera plugin for detecting objects with MLKit

## Installation

```bash
$ npm i vision-camera-object-detector
```

or

```bash
$ yarn add vision-camera-object-detector
```

### iOS installation

```bash
$ npx pod-install
```

### Android installation

No additional steps

#

### Add react-native-reanimated plugin in babel.config.js

```js
module.exports = {
  //...
  plugins: [
    [
      'react-native-reanimated/plugin',
      {
        globals: ['__detectObjects'], // add this line
      },
    ],
  ],
};
```

## Usage

```js
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
            style={{
              position: 'absolute',
              top: obj.bounds.relativeOrigin.top + '%',
              left: obj.bounds.relativeOrigin.left + '%',
              width: obj.bounds.relativeSize.width + '%',
              height: obj.bounds.relativeSize.height + '%',
              borderWidth: 1,
              borderColor: 'red',
            }}
          ></View>
        ))}
    </View>
  ) : null;
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    width: '100%',
  },
});
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
