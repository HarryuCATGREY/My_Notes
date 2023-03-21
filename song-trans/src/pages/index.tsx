import Head from 'next/head'
import Image from 'next/image'
import { Inter } from 'next/font/google'
import styles from '@/styles/Home.module.css'
import { useState, useEffect } from 'react'
import Sidebar from "../components/Sidebar.jsx"


const inter = Inter({ subsets: ['latin'] })

export default function Home() {
  

  return (
    <>
      <Head>
        <title>Music-tag</title>
      </Head>
      <Sidebar/>

      {/* Sidebar */}
      {/* Content */}
    </>
  )
}


// export async function getStaticProps() {
//   const res = await fetch("")
//   return {
//     props: {}, // will be passed to the page component as props
//   }
// }