class q2 {
    public static int giveBackMinute(int h1, int m1, int h2, int m2) {
        int res = 0;
        int hDiff = h2 - h1;
        int mDiff = m2 - m1;
        return (hDiff*60 + mDiff);
    }

    public static void main(String[] args) {
        System.out.println(giveBackMinute(11, 29, 15, 30));
    }
}

