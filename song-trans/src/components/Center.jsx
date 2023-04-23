import React, { useState, useEffect } from 'react'
import { useSession } from 'next-auth/react';
import { ChevronDownIcon } from '@heroicons/react/outline';
import { shuffle } from 'lodash';
import { spotifyApi } from '../hooks/useSpotify';

const colors = [
  "from-red-500",
  "from-yellow-500",
  "from-green-500",
  "from-blue-500",
  "from-indigo-500",
  "from-purple-500",
  "from-pink-500",
  "from-gray-500",
]


function Center() {
  const {data: session} = useSession();
  const [color, setColor] = React.useState(null);
  useEffect(() => {
    setColor(shuffle(colors).pop());
  })

  // 获取用户的最喜爱曲目
  const [topTracks, setTopTracks] = useState([]);
  // useEffect(() => {
  // spotifyApi.getMyTopTracks({ limit: 10 })
  //   .then((response) => {
  //     setTopTracks(response.body.items);
  //   })
  //   .catch((error) => {
  //     console.log(error);
  //   });
  // }, []);

  return (
    <div className='flex-grow text-white'>
      <header className='absolute top-5 right-8'>
        <div className='flex items-center bg-black space-x-3 opacity-90 hover:opacity-70 cursor-pointer rounded-full p-1 pr-2'>
          <img className="rounded-full w-10 h-10" src={session?.user.image}  alt='image'/>
          <h2>{session?.user.name}</h2>
          <ChevronDownIcon className='h-5 w-5'/>
        </div>
      </header>
      <section className={`flex items-end space-x-7 bg-gradient-to-b to-black ${color} h-80 text-white padding-8`}>
        Hello
        <ul>
        {topTracks.map((track) => (
          <li key={track.id}>{track.name}</li>
        ))}
      </ul>
      </section>
    </div>
  )
}

export default Center
