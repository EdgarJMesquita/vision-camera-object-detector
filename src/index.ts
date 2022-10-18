// @ts-ignore
// eslint-disable-next-line no-undef
import type { Frame } from 'react-native-vision-camera';

export function detectObjects(frame: Frame) {
  'worklet';
  // @ts-ignore
  // eslint-disable-next-line no-undef
  return __detectObjects(frame);
}