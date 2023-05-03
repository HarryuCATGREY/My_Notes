import React from 'react'
import useSpotify from '../hooks/useSpotify'
import { useSession } from 'next-auth/react';
import { useRecoilState } from 'recoil';
import { currentTrackIdState } from '../atoms/songAtom';
import { isPlayingState } from '../atoms/songAtom';
import { useState } from 'react';
import useSongInfo from '../hooks/useSongInfo';

function Player() {
  const spotifyApi = useSpotify();
  const { data: session, status} = useSession();
  const [currentTrackId, setCurrentTrackId] = useRecoilState(currentTrackIdState);
  const [isPlaying, setIsPlaying] = useRecoilState(isPlayingState);
  const [volume, setVolume] = useState(0.5);
  const songInfo = useSongInfo();
  return (
    <div>
      <div>
        <img className='h-10 w-10' src={songInfo?.album.images?.[0]?.url} alt="album image" />
      </div>
    </div>
  )
}

export default Player
