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
import { StyleSheet, View, Text, Button, Dimensions } from 'react-native';
import { Camera } from 'react-native-vision-camera';
import { detectObjects, DetectedObject } from 'vision-camera-object-detector';
import {
  useCameraDevices,
  useFrameProcessor,
} from 'react-native-vision-camera';

const Label = ({ label, trackingId }) => {
  return (
    <Text style={styles.label}>
      {`TrackingId: ${trackingId}`}
      {!!label?.text && `\n${label.text}(index: ${label.index})`}
      {!!label?.confidence &&
        `\n${label.confidence * 100}%(index: ${label.index})`}
    </Text>
  );
};

const Rect = ({ object }) => {
  const label = object.labels[0] ?? null;

  return (
    <View
      style={{
        top: object.bounds.relativeOrigin.top + '%',
        left: object.bounds.relativeOrigin.left + '%',
        width: object.bounds.relativeSize.width + '%',
        height: object.bounds.relativeSize.height + '%',
        borderWidth: 0.5,
        borderColor: 'white',
      }}
    >
      <Label label={label} trackingId={object.trackingId} />
    </View>
  );
};

export default function App() {
  const [hasPermission, setHasPermission] = React.useState(false);
  const [objects, setObjects] = React.useState<DetectedObject[]>([]);
  const devices = useCameraDevices();
  const device = devices.back;
  const [enableClassification, setEnableClassification] = React.useState(false);
  const [enableMultipleObjects, setEnableMultipleObjects] =
    React.useState(false);

  React.useEffect(() => {
    (async () => {
      const status = await Camera.requestCameraPermission();
      setHasPermission(status === 'authorized');
    })();
  }, []);

  const frameProcessor = useFrameProcessor(
    (frame) => {
      'worklet';
      const detectedObjects = detectObjects(frame, {
        enableClassification,
        enableMultipleObjects,
      });
      runOnJS(setObjects)(detectedObjects);
    },
    [enableClassification, enableMultipleObjects]
  );

  return device != null && hasPermission ? (
    <View style={styles.container}>
      <Camera
        style={StyleSheet.absoluteFill}
        device={device}
        isActive={true}
        frameProcessor={frameProcessor}
        frameProcessorFps={25}
      />
      {objects.map((obj, index) => (
        <Rect key={index} object={obj} />
      ))}
      <View style={styles.footer}>
        <Button
          title={`enableClassifications: ${
            enableClassification ? 'yes' : 'no'
          }`}
          onPress={() => setEnableClassification((state) => !state)}
        />
        <Button
          title={`enableMultipleObjects: ${
            enableMultipleObjects ? 'yes' : 'no'
          }`}
          onPress={() => setEnableMultipleObjects((state) => !state)}
        />
      </View>
    </View>
  ) : null;
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    width: '100%',
  },
  label: {
    top: 0,
    left: 0,
    marginTop: -16,
    fontSize: 14,
    color: 'black',
    backgroundColor: 'white',
  },
  footer: {
    position: 'absolute',
    left: 0,
    bottom: 0,
    right: 0,
    justifyContent: 'center',
    alignItems: 'stretch',
    padding: 20,
  },
});
```

## Developer notes

Currently react-native-vision-camera plugin made with swift won't work on XCode 14.

Apparently Objective-C works fine.
I'm working on refactoring my code from Swift to Objective-C

## New features

- Option for enabling classifications(Android)
- Option for enabling multiple object(Android)

## Coming soon

- Option for enabling classifications(iOS)
- Option for enabling multiple object(iOS)

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
