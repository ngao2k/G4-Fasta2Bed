import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * G4_Base类用于识别和匹配DNA序列中的G-四链体结构。
 * G-四链体是一种特殊的DNA结构，由连续的鸟嘌呤（G）碱基形成。
 * 本类定义了多种G-四链体的结构模式，包括标准的4G模式、含bulge的模式、GVBQ模式和4GL15模式。
 */
public class G4_Base {
    // 输入的DNA序列
    public String input;

    // 定义各种G-四链体结构的正则表达式
    private String re_L7 = ".{1,7}?";
    private String re_G3 = "G{3,}";
    private String re_C3 = "C{3,}";

    // G
    private String re_bulgeG = "G{2}[^G]G{2}?|G{2}?[^G]G{2}";
    private String re_bulge1G = "(" + re_bulgeG + ")(" + re_L7 + re_G3 + "){3,}";
    private String re_bulge2G = "(" + re_G3 + re_L7 + ")(" + re_bulgeG + ")(" + re_L7 + re_G3 + "){2,}";
    private String re_bulge3G = "(" + re_G3 + re_L7 + "){2}(" + re_bulgeG + ")(" + re_L7 + re_G3 + "){1,}";
    private String re_bulge4G = "(" + re_G3 + re_L7 + "){3}(" + re_bulgeG + ")";
    private String re_gvbq1G = "G{2}(" + re_L7 + re_G3 + "){3,}";
    private String re_gvbq2G = "(" + re_G3 + re_L7 + ")G{2}(" + re_L7 + re_G3 + "){2,}";
    private String re_gvbq3G = "(" + re_G3 + re_L7 + "){2}G{2}(" + re_L7 + re_G3 + "){1,}";
    private String re_gvbq4G = "(" + re_G3 + re_L7 + "){3}G{2}";
    private String re_4GL15G = re_G3 + ".{1,15}?" + re_G3;
    private String re_4GL151G = "(" + re_4GL15G + ")(" + re_L7 + re_G3 + "){2,}";
    private String re_4GL152G = "(" + re_G3 + re_L7 + ")(" + re_4GL15G + ")(" + re_L7 + re_G3 + "){1,}";
    private String re_4GL153G = "(" + re_G3 + re_L7 + "){2}(" + re_4GL15G + ")";
    // C
    private String re_bulgeC = "C{2}[^C]C{2}?|C{2}?[^C]C{2}";
    private String re_bulge1C = "(" + re_bulgeC + ")(" + re_L7 + re_C3 + "){3,}";
    private String re_bulge2C = "(" + re_C3 + re_L7 + ")(" + re_bulgeC + ")(" + re_L7 + re_C3 + "){2,}";
    private String re_bulge3C = "(" + re_C3 + re_L7 + "){2}(" + re_bulgeC + ")(" + re_L7 + re_C3 + "){1,}";
    private String re_bulge4C = "(" + re_C3 + re_L7 + "){3}(" + re_bulgeC + ")";
    private String re_gvbq1C = "C{2}(" + re_L7 + re_C3 + "){3,}";
    private String re_gvbq2C = "(" + re_C3 + re_L7 + ")C{2}(" + re_L7 + re_C3 + "){2,}";
    private String re_gvbq3C = "(" + re_C3 + re_L7 + "){2}C{2}(" + re_L7 + re_C3 + "){1,}";
    private String re_gvbq4C = "(" + re_C3 + re_L7 + "){3}C{2}";
    private String re_4GL15C = re_C3 + ".{1,15}?" + re_C3;
    private String re_4GL151C = "(" + re_4GL15C + ")(" + re_L7 + re_C3 + "){2,}";
    private String re_4GL152C = "(" + re_C3 + re_L7 + ")(" + re_4GL15C + ")(" + re_L7 + re_C3 + "){1,}";
    private String re_4GL153C = "(" + re_C3 + re_L7 + "){2}(" + re_4GL15C + ")";

    // 各种G-四链体结构模式的组合正则表达式
    private String re_4G = re_G3 + "(" + re_L7 + re_G3 + "){3,}" + "|" 
                         + re_C3 + "(" + re_L7 + re_C3 + "){3,}";
    private String re_Bulge = re_bulge1G + "|" + re_bulge2G + "|" + re_bulge3G + "|" + re_bulge4G + "|"
                            + re_bulge1C + "|" + re_bulge2C + "|" + re_bulge3C + "|" + re_bulge4C;
    private String re_GVBQ = re_gvbq1G + "|" + re_gvbq2G + "|" + re_gvbq3G + "|" + re_gvbq4G + "|"
                           + re_gvbq1C + "|" + re_gvbq2C + "|" + re_gvbq3C + "|" + re_gvbq4C;
    private String re_4GL15 = re_4GL151G + "|" + re_4GL152G + "|" + re_4GL153G + "|"
                            + re_4GL151C + "|" + re_4GL152C + "|" + re_4GL153C;
    private String re_PHQS = re_G3 + "(" + re_L7 + re_G3 + "){1,2}" + "|" 
                           + re_C3 + "(" + re_L7 + re_C3 + "){1,2}";

    // 对应各种G-四链体结构模式的正则表达式对象
    private Pattern pattern_4G = Pattern.compile(re_4G, Pattern.CASE_INSENSITIVE);
    private Pattern pattern_Bulge = Pattern.compile(re_Bulge, Pattern.CASE_INSENSITIVE);
    private Pattern pattern_GVBQ = Pattern.compile(re_GVBQ, Pattern.CASE_INSENSITIVE);
    private Pattern pattern_4GL15 = Pattern.compile(re_4GL15, Pattern.CASE_INSENSITIVE);
    private Pattern pattern_PHQS = Pattern.compile(re_PHQS, Pattern.CASE_INSENSITIVE);

    /**
     * 设置输入的DNA序列。
     * 
     * @param input 待分析的DNA序列字符串。
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     * 根据预定义的模式匹配输入序列中的G-四链体结构。
     * 
     * @param sequence 输入的DNA序列。
     * @return 匹配结果的列表，每个结果包括起始位置、结束位置、匹配的字符串和匹配的模式类型。
     */
    public ArrayList<String[]> matchPatterns(String sequence) {
        // 存储所有匹配结果的列表
        ArrayList<String[]> matches = new ArrayList<>();

        // 使用并行流来处理每个匹配器的结果
        List<Matcher> matchers = List.of(
                pattern_4G.matcher(sequence),
                pattern_Bulge.matcher(sequence),
                pattern_GVBQ.matcher(sequence),
                pattern_4GL15.matcher(sequence),
                pattern_PHQS.matcher(sequence));

        matches.addAll(matchers.parallelStream()
                .flatMap(matcher -> {
                    List<String[]> localMatches = new ArrayList<>();
                    while (matcher.find()) {
                        String[] match = {
                                String.valueOf(matcher.start()),
                                String.valueOf(matcher.end()),
                                matcher.group(),
                                getPatternType(matcher)
                        };
                        localMatches.add(match);
                    }
                    return localMatches.stream();
                })
                .collect(Collectors.toList()));

        // 返回包含所有匹配结果的列表
        return matches;
    }

    /**
     * 获取匹配器对应的模式类型。
     *
     * @param matcher 正在匹配的Matcher对象。
     * @return 匹配的模式类型字符串。
     */
    private String getPatternType(Matcher matcher) {
        if (matcher.pattern().pattern().equals(pattern_4G.pattern())) {
            return "4G";
        } else if (matcher.pattern().pattern().equals(pattern_Bulge.pattern())) {
            return "Bulge";
        } else if (matcher.pattern().pattern().equals(pattern_GVBQ.pattern())) {
            return "GVBQ";
        } else if (matcher.pattern().pattern().equals(pattern_4GL15.pattern())) {
            return "4GL15";
        } else if (matcher.pattern().pattern().equals(pattern_PHQS.pattern())) {
            return "PHQS";
        } else {
            return "Unknown";
        }
    }

    public static void main(String[] args) {
        G4_Base matcher = new G4_Base();
        String sequence = "你的DNA序列"; // 替换为实际序列
        ArrayList<String[]> results = matcher.matchPatterns(sequence);

        // 打印匹配结果
        results.forEach(result -> System.out.println("Start: " + result[0] + ", End: " + result[1] +
                ", Match: " + result[2] + ", Type: " + result[3]));
    }
}
