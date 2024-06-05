import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class ConvertSequence {

    /**
     * 将DNA序列转换为其互补序列。
     * DNA互补配对规则是：A与T配对，C与G配对。此函数同时处理大写和小写字母。
     *
     * @param sequence 输入的DNA序列，可以包含大写和小写字母。
     * @return 返回转换后的互补序列。
     * @throws IOException 如果读写操作发生错误。
     */
    public String convertSequence(String sequence) throws IOException {
        Reader reader = new StringReader(sequence);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder result = new StringBuilder();
        int base;
        while ((base = bufferedReader.read()) != -1) {
            // 根据DNA互补配对规则进行转换。
            switch (base) {
                case 'A':
                    result.append('T');
                    break;
                case 'C':
                    result.append('G');
                    break;
                case 'T':
                    result.append('A');
                    break;
                case 'G':
                    result.append('C');
                    break;
                case 'a':
                    result.append('t');
                    break;
                case 'c':
                    result.append('g');
                    break;
                case 't':
                    result.append('a');
                    break;
                case 'g':
                    result.append('c');
                    break;
                default:
                    // 如果遇到非DNA字符，则直接将其添加到结果中。
                    result.append((char) base);
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        ConvertSequence c = new ConvertSequence();
        String sequence = "ATCGatcg";
        try {
            System.out.println(c.convertSequence(sequence));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
