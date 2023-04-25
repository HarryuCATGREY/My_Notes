import React from 'react'
import { millisToMinutesAndSeconds } from '../../lib/time';

function Song({ order, track}) {
  return (
    <div className='grid grid-cols-2 text-gray-500 hover:bg-gray-900 py-1 px-3.5'>
      <div className='flex items-center space-x-4 '>
        <p>{order + 1}</p>
        <img className='h-10 w-10' src={track.album.images[0].url} alt="album image" />
        <div>
          <p className='w-36 lg:w-64 truncate text-white'>{track.name}</p>
          <p className='w-40'>{track.artists[0].name}</p>
        </div>
      </div>
      
      <div className='flex items-center justify-between ml-auto md:ml-0'>
        <p className='w-40 hidden sm:inline'>{track.album.name}</p>
        <p>{millisToMinutesAndSeconds(track.duration_ms)}</p>
      </div>
    </div>
  )
}

export default Song
