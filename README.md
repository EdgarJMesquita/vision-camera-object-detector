# vision-camera-object-detector

Vision Camera plugin for detecting objects with [MLKit](https://developers.google.com/ml-kit/vision/object-detection).

This package is a plugin for [react-native-vision-camera](https://mrousavy.com/react-native-vision-camera/).

## Installing

### Using npm

```bash
$ npm i vision-camera-object-detector
```

### Using yarn

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

## Requirements

Frame Processors require react-native-reanimated 2.2.0 or higher. Also make sure to add

```js
import 'react-native-reanimated';
```

to the top of your index.js

## Registering the plugin

Add react-native-reanimated plugin in babel.config.js

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

#

## Usage

```tsx
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
```

## Developer notes

Currently react-native-vision-camera plugin made with swift won't work on XCode 14.

Apparently Objective-C works fine.
I'm working on refactoring my code from Swift to Objective-C

## Coming soon

- Option for enabling classifications
- Option for multiple object detects

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
