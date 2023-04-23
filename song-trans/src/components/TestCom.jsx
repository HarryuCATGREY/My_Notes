import { useEffect, useState } from 'react';
import { spotifyApi } from '../hooks/useSpotify';

export default function MyComponent() {
  const [topTracks, setTopTracks] = useState([]);

  useEffect(() => {
    // 获取用户的最喜爱曲目
    spotifyApi.getMyTopTracks({ limit: 10 })
      .then((response) => {
        setTopTracks(response.body.items);
      })
      .catch((error) => {
        console.log(error);
      });
  }, []);

  return (
    <ul>
      {topTracks.map((track) => (
        <li key={track.id}>{track.name}</li>
      ))}
    </ul>
  );
}