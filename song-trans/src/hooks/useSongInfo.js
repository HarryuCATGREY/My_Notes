import React, { useEffect } from 'react'
import { useRecoilState } from 'recoil';
import { currentTrackIdState } from '../atoms/songAtom';
import useSpotify from './useSpotify';

function useSongInfo() {
  const spotifyApi = useSpotify();
  let AccessToken = spotifyApi.getAccessToken();
  const [currentTrackId, setCurrentTrackId] = useRecoilState(currentTrackIdState);
  const [songInfo, setSongInfo] = React.useState(null);
  useEffect(() => {
    const fetchSongInfo = async () => {
      if (currentTrackId) {
        const trackInfo = await fetch(`https://api.spotify.com/v1/tracks/${currentTrackId}`, 
        {
          headers: {
            Authorization: `Bearer ${AccessToken}`,
          }
        }).then(res => res.json());
        console.log("trackInfo is ", trackInfo);
        setSongInfo(trackInfo);
      }
    };
    fetchSongInfo();
  }, [currentTrackId, spotifyApi]);
  return (
    songInfo
  )
}

export default useSongInfo
