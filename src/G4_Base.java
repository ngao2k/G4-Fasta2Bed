import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * G4_Base类用于识别和匹配DNA序列中的G-四链体结构。
 * G-四链体是一种特殊的DNA结构，由连续的鸟嘌呤（G）碱基形成。
 * 本类定义了多种G-四链体的结构模式，包括标准的4G模式、含bulge的模式、GVBQ模式和4GL15模式。
 */
public class G4_Base {
    // 输入的DNA序列
    public String input;

    // 定义各种G-四链体结构的正则表达式
    // 正则表达式
    private String re_L7 = ".{1,7}?";
    private String re_CG3 = "[CG]{3,}";
    private String re_bulgeCG = "[CG][CG][^CG][CG][CG]?|[CG][CG]?[^CG][CG][CG]";
    private String re_bulge1CG = "(" + re_bulgeCG + ")(" + re_L7 + re_CG3 + "){3,}";
    private String re_bulge2CG = "(" + re_CG3 + re_L7 + ")(" + re_bulgeCG + ")(" + re_L7 + re_CG3 + "){2,}";
    private String re_bulge3CG = "(" + re_CG3 + re_L7 + "){2}(" + re_bulgeCG + ")(" + re_L7 + re_CG3 + "){1,}";
    private String re_bulge4CG = "(" + re_CG3 + re_L7 + "){3}(" + re_bulgeCG + ")";
    private String re_gvbq1CG = "[CG][CG](" + re_L7 + re_CG3 + "){3,}";
    private String re_gvbq2CG = "(" + re_CG3 + re_L7 + ")[CG][CG](" + re_L7 + re_CG3 + "){2,}";
    private String re_gvbq3CG = "(" + re_CG3 + re_L7 + "){2}[CG][CG](" + re_L7 + re_CG3 + "){1,}";
    private String re_gvbq4CG = "(" + re_CG3 + re_L7 + "){3}[CG][CG]";
    private String re_4GL15CG = re_CG3 + ".{1,15}?" + re_CG3;
    private String re_4GL151CG = "(" + re_4GL15CG + ")(" + re_L7 + re_CG3 + "){2,}";
    private String re_4GL152CG = "(" + re_CG3 + re_L7 + ")(" + re_4GL15CG + ")(" + re_L7 + re_CG3 + "){1,}";
    private String re_4GL153CG = "(" + re_CG3 + re_L7 + "){2}(" + re_4GL15CG + ")";
    
    // 各种G-四链体结构模式的组合正则表达式
    private String re_4G = re_CG3 + "(" + re_L7 + re_CG3 + "){3,}";
    private String re_Bulge = re_bulge1CG + "|" + re_bulge2CG + "|" + re_bulge3CG + "|" + re_bulge4CG;
    private String re_GVBQ = re_gvbq1CG + "|" + re_gvbq2CG + "|" + re_gvbq3CG + "|" + re_gvbq4CG;
    private String re_4GL15 = re_4GL151CG + "|" + re_4GL152CG + "|" + re_4GL153CG;
    private String re_PHQS = re_CG3 + "(" + re_L7 + re_CG3 + "){1,2}";

    // 对应各种G-四链体结构模式的正则表达式对象
    private Pattern pattern_4G = Pattern.compile(re_4G);
    private Pattern pattern_Bulge = Pattern.compile(re_Bulge);
    private Pattern pattern_GVBQ = Pattern.compile(re_GVBQ);
    private Pattern pattern_4GL15 = Pattern.compile(re_4GL15);
    private Pattern pattern_PHQS = Pattern.compile(re_PHQS);

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

        // 对输入序列使用不同的模式进行匹配
        Matcher matcher_4G = pattern_4G.matcher(sequence);
        Matcher matcher_Bulge = pattern_Bulge.matcher(sequence);
        Matcher matcher_GVBQ = pattern_GVBQ.matcher(sequence);
        Matcher matcher_4GL15 = pattern_4GL15.matcher(sequence);
        Matcher matcher_PHQS = pattern_PHQS.matcher(sequence);

        // 遍历每个匹配器的结果，并将匹配结果添加到列表中
        while (matcher_4G.find()) {
            // 构建匹配结果的字符串数组，并添加到列表中
            // 构建一个字符串数组，包含匹配的起始位置、结束位置、匹配的字符串和模式类型
            String[] match = { String.valueOf(matcher_4G.start()), String.valueOf(matcher_4G.end()),
                    matcher_4G.group(), "4G" };
            matches.add(match);
        }
        while (matcher_Bulge.find()) {
            String[] match = { String.valueOf(matcher_Bulge.start()), String.valueOf(matcher_Bulge.end()),
                    matcher_Bulge.group(), "Bulge" };
            matches.add(match);
        }
        while (matcher_GVBQ.find()) {
            String[] match = { String.valueOf(matcher_GVBQ.start()), String.valueOf(matcher_GVBQ.end()),
                    matcher_GVBQ.group(), "GVBQ" };
            matches.add(match);
        }
        while (matcher_4GL15.find()) {
            String[] match = { String.valueOf(matcher_4GL15.start()), String.valueOf(matcher_4GL15.end()),
                    matcher_4GL15.group(), "4GL15" };
            matches.add(match);
        }
        while (matcher_PHQS.find()) {
            String[] match = { String.valueOf(matcher_PHQS.start()), String.valueOf(matcher_PHQS.end()),
                    matcher_PHQS.group(), "PHQS" };
            matches.add(match);
        }

        // 返回包含所有匹配结果的列表
        return matches;
    }
}