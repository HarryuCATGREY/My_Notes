import Head from 'next/head'
import Image from 'next/image'
import { Content, Inter } from 'next/font/google'
import styles from '@/styles/Home.module.css'
import { useState, useEffect } from 'react'
import Sidebar from "../components/Sidebar.jsx"
import Center from "../components/Center.jsx"
import { getSession } from "next-auth/react";
import Player from "../components/Player.jsx"


const inter = Inter({ subsets: ['latin'] })

export default function Home() {
  

  return (
    <div className='bg-black overflow-hidden '>
      <Head>
        <title>Music-tag</title>
      </Head>
      <main className='flex'>
        <Sidebar/>
        <Center/>
        
      </main>
      <Player/>
      <div className='sticky bottom-0'></div>
      
    </div>
  )
}

export async function getSeverSideProps(context) {
  const session = await getSession(context);


  return {
    props: { session },
  }
}


// export async function getStaticProps() {
//   const res = await fetch("")
//   return {
//     props: {}, // will be passed to the page component as props
//   }
// }