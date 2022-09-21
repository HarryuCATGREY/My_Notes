import java.util.ArrayList;
import java.util.List;

public class N06Z型变化 {
    public String convert(String s, int numRows) {
        List<StringBuilder> rows = new ArrayList<StringBuilder>();
        for (int i = 0; i < numRows; i++) {
            rows.add(new StringBuilder());
        }
        int flag = 1;
        int i = 0;
        for (char cha: s.toCharArray()) {
            rows.get(i).append(cha);
            i += flag;
            if (i == 0 || i == numRows - 1) {
                flag = - flag;
            }
        }
        StringBuilder result = new StringBuilder();
        for (StringBuilder b: rows) {
            result.append(b);
        }
        return result.toString();

    }

    public static void main(String[] args) {
        N06Z型变化 n6 = new N06Z型变化();
        System.out.println(n6.convert("PAYPALISHIRING", 3));
    }
}
