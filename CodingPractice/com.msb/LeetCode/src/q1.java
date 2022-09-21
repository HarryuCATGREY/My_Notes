class Main {
    public static int getBlankNum(String s) {
        int len = s.length();
        int res = 0;
        for (int i = 0; i < len; i ++) {
            if (s.charAt(i) == 32) {
                res++;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        String s = "hel o  worl";
        System.out.println(getBlankNum(s));
    }
}
