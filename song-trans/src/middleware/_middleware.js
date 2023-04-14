import { getToken } from "next-auth/jwt";
import { NextResponse } from "next/server";

export async function middleware(req) {
  const token = await getToken({req, secret: process.env.JWT_SECRET});
  const { pathname } = req.nextUrl;
  if (pathname.includes("/api/auth") || pathname.includes("/api/auth")) {
    return NextResponse.next();
  }
}