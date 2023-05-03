import { atom } from 'recoil';

export const currentTrackIdState = atom({
  key: 'currentTrackState',
  default: null,
});

export const isPlayingState = atom({
  key: 'isPlayingState',
  default: false,
});