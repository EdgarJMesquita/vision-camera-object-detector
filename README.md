# vision-camera-object-detector

Vision Camera plugin for detecting objects

## Installation

```sh
npm install vision-camera-object-detector
```

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

import { StyleSheet } from 'react-native';
import {
  useCameraDevices,
  useFrameProcessor,
} from 'react-native-vision-camera';
import { Camera } from 'react-native-vision-camera';
import { detectObjects } from 'vision-camera-object-detector';

export default function App() {
  const [hasPermission, setHasPermission] = React.useState(false);
  const [faces, setFaces] = React.useState();

  const devices = useCameraDevices();
  const device = devices.front;

  React.useEffect(() => {
    console.log(faces);
  }, [faces]);

  React.useEffect(() => {
    (async () => {
      const status = await Camera.requestCameraPermission();
      setHasPermission(status === 'authorized');
    })();
  }, []);

  const frameProcessor = useFrameProcessor((frame) => {
    'worklet';
    const scannedFaces = detectObjects(frame);
    runOnJS(setFaces)(scannedFaces);
  }, []);

  return device != null && hasPermission ? (
    <Camera
      style={StyleSheet.absoluteFill}
      device={device}
      isActive={true}
      onError={console.log}
      frameProcessor={frameProcessor}
      frameProcessorFps={5}
    />
  ) : null;
}
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
