import React, { useEffect } from 'react'
import { useSession } from 'next-auth/react'
import spotifyApi from '../lib/spotify'

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


  return spotifyApi;
}
export default useSpotify

