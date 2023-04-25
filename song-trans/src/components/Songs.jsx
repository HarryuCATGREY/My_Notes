import React from 'react'
import { useRecoilValue } from 'recoil';
import { playlistState } from '../atoms/playlistAtom';
import Song from './Song';

function Songs() {
  const playlist = useRecoilValue(playlistState);
  return (
    
    <div className='px-4 flex flex-col space-y-1 pb-28 text-white'>
      {playlist?.tracks.items.map((track, i) => (
        <div key={track.track.id} className="text-white flex">     
          <Song key={track.track.id} track={track.track} order={i}/>
        </div>
      ))}
    </div>
  )
}

export default Songs
