// @ts-ignore
// eslint-disable-next-line no-undef
import type { Frame } from 'react-native-vision-camera';

type Object = {
  bounds: {
    relativeOrigin: {
      top: number;
      left: number;
    };
    relativeSize: {
      width: number;
      height: number;
    };
    size: {
      width: number;
      height: number;
    };
    origin: {
      x: number;
      y: number;
    };
  };
  image: {
    width: number;
    height: number;
  };
  trackingId: number;
};

export function detectObjects(frame: Frame): Object[] {
  'worklet';
  // @ts-ignore
  // eslint-disable-next-line no-undef
  return __detectObjects(frame);
}
