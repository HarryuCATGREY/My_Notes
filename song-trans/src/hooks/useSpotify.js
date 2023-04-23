import React, { useEffect, useState } from 'react'
import { signIn, useSession } from 'next-auth/react'
import SpotifyWebApi from 'spotify-web-api-node';

const spotifyApi = new SpotifyWebApi({
  clientId: process.env.SPOTIFY_ID,
  clientSecret: process.env.SPOTIFY_SECRET,
  redirectUri: process.env.NEXTAUTH_URL,
});

function useSpotify() {
  const {data:session, status} = useSession();
  useEffect(() => {
    if (session) {
      // If the user access token has expired, redirect to the login page
      if (session.error === 'RefreshAccessTokenError') {
        signIn();
      }
      // Set the access token on the Spotify API object to be used in our
      spotifyApi.setAccessToken(session.user.accessToken);
    }
  }, [session]) 

  // useEffect(() => {
  //   if (spotifyApi.getAccessToken()) {
  //     spotifyApi.getUserPlaylists().then((data) => {
  //       setPlayLists(data.body.items);
  //     });
  //   }
  // }, []);
  return spotifyApi;
}
export default useSpotify

