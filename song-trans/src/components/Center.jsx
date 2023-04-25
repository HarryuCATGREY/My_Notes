import React, { useState, useEffect } from 'react'
import { useSession } from 'next-auth/react';
import { ChevronDownIcon } from '@heroicons/react/outline';
import { shuffle } from 'lodash';
import useSpotify from "../hooks/useSpotify";
import { playlistIdState, playlistState} from '../atoms/playlistAtom';
import { useRecoilState, useRecoilValue } from 'recoil';
import Songs from './Songs';
import Link from 'next/link';


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
  const spotifyApi = useSpotify();
  const [color, setColor] = React.useState(null);
  const playlistId = useRecoilValue(playlistIdState);
  const [playlist, setPlaylist] = useRecoilState(playlistState);
  useEffect(() => {
    setColor(shuffle(colors).pop());
  }, [playlistId])
  useEffect(() => {
    spotifyApi
    .getPlaylist(playlistId)
    .then((data) => {
      setPlaylist(data.body);
    }).catch((err) => {
      console.log("Some thing went wrong",err);
    })
  }, [spotifyApi, playlistId])


  return (
    <div className='flex-grow text-white h-screen overflow-y-scroll'>
      <header className='absolute top-5 right-8'>
        <Link href={"login"}>
          <div className='flex items-center bg-black space-x-3 opacity-90 hover:opacity-70 cursor-pointer rounded-full p-1 pr-2'>
            <img className="rounded-full w-10 h-10" src={session?.user.image}  alt='image'/>
            <h2>{session?.user.name}</h2>
            <ChevronDownIcon className='h-5 w-5'/>
          </div>
        </Link>
      </header>
      <section className={`flex items-end space-x-7 bg-gradient-to-b to-black ${color} h-40 text-white p-8`}>
        <img src={playlist?.images[0].url} alt="playlist" className="h-20 w-20 rounded-md shadow-2xl"/>
        <div>
          <p className='text-sm'>PLAYLIST</p>
          <h1 className="text-2xl font-bold">{playlist?.name}</h1>
        </div>
      </section>
      <Songs />
    </div>
  )
}

export default Center
