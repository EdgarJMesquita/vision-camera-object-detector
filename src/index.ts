// @ts-ignore
// eslint-disable-next-line no-undef
import type { Frame } from 'react-native-vision-camera';

/**
 * Represents a detected object
 */
export interface DetectedObject {
  /**
   * Detected object coords and size
   */
  bounds: {
    /**
     * Rect's relative origin in percentage
     */
    relativeOrigin: {
      top: number;
      left: number;
    };
    /**
     * Rect's relative size in percentage
     */
    relativeSize: {
      width: number;
      height: number;
    };
    /**
     * Rect's size based on image pixels
     */
    size: {
      width: number;
      height: number;
    };
    /**
     * Rect's origin based on image pixels
     */
    origin: {
      x: number;
      y: number;
    };
  };
  /**
   * Frame size in pixels
   */
  image: {
    width: number;
    height: number;
  };
  /**
   * Object's identification for tracking purposes
   */
  trackingId: number;
}

export function detectObjects(frame: Frame): Object[] {
  'worklet';
  // @ts-ignore
  // eslint-disable-next-line no-undef
  return __detectObjects(frame);
}
