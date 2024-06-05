import java.io.IOException;
import java.util.ArrayList;
import java.nio.file.*;

/**
 * G4Fasta2Bed类用于将Fasta格式的DNA序列转换为Bed格式，重点标注G4结构。
 * G4结构是一种特殊的DNA结构，由四个相邻的G碱基通过氢键相互作用形成。
 */
public class G4Fasta2Bed {
    // 类成员变量
    private FastaInput fastaSeq;
    private G4_Base g4Base;
    private BedOutput bedOutput;
    private String[][] bedData; // 声明为类成员变量
    private Path fastaPath;
    private Path cacheFolder;
    private Path outputPath_F;
    private Path outputPath_R;

    public G4Fasta2Bed(String fastaPath, String cacheFolder, String outputFolder) {
        this.fastaPath = Paths.get(fastaPath);
        this.cacheFolder = Paths.get(cacheFolder);

        // 提取FASTA文件名（不带扩展名）
        String fastaFileName = this.fastaPath.getFileName().toString();
        int dotIndex = fastaFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fastaFileName = fastaFileName.substring(0, dotIndex);
        }

        this.outputPath_F = Paths.get(outputFolder, fastaFileName + "_正链_G4.bed");
        this.outputPath_R = Paths.get(outputFolder, fastaFileName + "_负链_G4.bed");

        // 创建输出目录
        try {
            Files.createDirectories(Paths.get(outputFolder));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error creating output directory.");
        }

        // 在构造函数中初始化对象
        this.fastaSeq = new FastaInput(this.fastaPath.toString(), this.cacheFolder.toString());
        this.g4Base = new G4_Base();
        this.bedOutput = new BedOutput();
    }

    /**
     * 针对FASTA序列中的每个染色体，查找并处理G4结构。
     * G4结构是一种特殊的DNA结构，由四个相邻的G碱基堆叠形成。
     * 此方法首先从FASTA序列中获取每个染色体的标题和序列，
     * 然后对每个序列进行处理，寻找并处理G4结构。
     * 最后，将找到的G4结构写入到指定路径的BED文件中。
     * 
     * @throws IOException 如果读取FASTA文件时发生错误。
     */
    private void matchG4_F() {
        try {
            // 遍历FASTA文件中的所有染色体标题
            for (String header : fastaSeq.getChromosomeHeaders()) {
                // 根据标题获取染色体序列
                String sequence = fastaSeq.getSequenceByHeader(header).replaceAll("N", "");
                ;
                // 对染色体序列进行处理，寻找G4结构
                processSequence_F(header, sequence, "+");

                // 将找到的G4结构写入BED文件
                bedOutput.writeBEDFile(outputPath_F.toString(), bedData);
            }
        } catch (IOException e) {
            // 打印堆栈跟踪，如果读取文件时发生错误
            e.printStackTrace();
            // 输出错误信息，指示读取FASTA文件时发生错误
            System.out.println("Error reading FASTA file.");
        }
    }

    /**
     * 针对基因组中的负链DNA序列，寻找并处理G4结构。
     * G4结构是一种特殊的DNA结构，由四个G碱基通过氢键相互作用形成。
     * 本方法首先从FASTA文件中读取每个染色体的DNA序列，然后将这些序列转换为对应的负链序列，
     * 接着对转换后的序列进行G4结构的处理和分析，最后将分析结果写入到指定的BED文件中。
     *
     * @throws IOException 如果读取FASTA文件或写入BED文件时发生IO错误。
     */
    private void matchG4_R() {
        try {
            // 遍历FASTA文件中的所有染色体头信息
            // 读取Fasta文件中的DNA序列
            for (String header : fastaSeq.getChromosomeHeaders()) {
                // 根据染色体头信息获取对应的DNA序列
                // 获取染色体的DNA序列
                String sequence = fastaSeq.getSequenceByHeader(header).replaceAll("N", "");
                ;

                // 将正链DNA序列转换为负链序列
                // 将A换成T，C换成G，T换成A，G换成C
                String convertedSequence = convertSequence(sequence);

                // 对转换后的负链序列进行G4结构的处理和分析
                processSequence_F(header, convertedSequence, "-");

                // 将分析得到的G4结构数据写入到指定的BED文件中
                bedOutput.writeBEDFile(outputPath_R.toString(), bedData);
            }
        } catch (IOException e) {
            // 打印IO异常的堆栈跟踪信息
            e.printStackTrace();
            // 输出错误信息，提示用户读取FASTA文件时发生错误
            System.out.println("Error reading FASTA file.");
        }
    }

    /**
     * 处理DNA序列，识别G4结构并转换为Bed格式数据。
     * 
     * @param header   序列的标题，用于标识序列来源或名称。
     * @param sequence DNA序列。
     * @param bedData  用于存储Bed格式数据的二维数组。
     */
    private void processSequence_F(String header, String sequence, String strand) {
        // 设置G4匹配的输入内容
        g4Base.setInput(sequence);

        // 进行G4匹配
        ArrayList<String[]> matches = g4Base.matchPatterns(sequence);

        // 初始化bedData数组，大小为匹配结果的数量
        bedData = new String[matches.size()][8];
        // 遍历匹配结果
        for (int i = 0; i < matches.size(); i++) {
            String[] match = matches.get(i);
            // 填充BED行数据
            bedData[i][0] = header; // 染色体编号
            bedData[i][1] = match[0]; // 起始位置
            bedData[i][2] = match[1]; // 终止位置
            bedData[i][3] = header + "_" + match[0] + "_" + match[1]; // 区间ID
            bedData[i][4] = String.valueOf(Integer.parseInt(match[1]) - Integer.parseInt(match[0])); // 区间长度
            bedData[i][5] = strand; // 链的方向
            bedData[i][6] = match[2]; // 序列
            bedData[i][7] = match[3]; // 识别到的G4类别
        }
    }

    /**
     * 将DNA序列转换为其互补序列。
     * DNA互补配对规则是：A与T配对，C与G配对。此函数同时处理大写和小写字母。
     *
     * @param sequence 输入的DNA序列，可以包含大写和小写字母。
     * @return 返回转换后的互补序列。
     */
    private String convertSequence(String sequence) {
        // 使用StringBuilder来构建结果序列，因为它比String拼接效率更高。
        StringBuilder converted = new StringBuilder();
        // 遍历输入序列的每个字符。
        for (char base : sequence.toCharArray()) {
            // 根据DNA互补配对规则进行转换。
            switch (base) {
                case 'A':
                    converted.append('T');
                    break;
                case 'C':
                    converted.append('G');
                    break;
                case 'T':
                    converted.append('A');
                    break;
                case 'G':
                    converted.append('C');
                    break;
                case 'a':
                    converted.append('t');
                    break;
                case 'c':
                    converted.append('g');
                    break;
                case 't':
                    converted.append('a');
                    break;
                case 'g':
                    converted.append('c');
                    break;
                default:
                    // 如果遇到非DNA字符，则直接将其添加到结果中。
                    converted.append(base);
                    converted.append(base); // 保持非ATCG的字符不变
            }
        }
        // 返回构建好的互补序列。
        return converted.toString();

    }

    /**
     * 实现G4规则的全面匹配。
     * 该方法调用了两个辅助方法，分别处理G4规则中的右半部分和前半部分的匹配。
     * G4规则可能是指一组特定的匹配条件或算法，这里没有具体说明，注释中应避免引入未定义的术语。
     * 
     * @see matchG4_R() 处理G4规则的右半部分
     * @see matchG4_F() 处理G4规则的前半部分
     */
    public void matchG4_All() {
        matchG4_R();
        matchG4_F();
    }

    /**
     * 清理缓存目录。
     * 
     * 本方法尝试清除指定的缓存目录中的所有文件。如果清除操作失败，将打印错误堆栈跟踪。
     * 使用DirectoryCleaner类的clearDirectory方法来执行实际的清除操作。
     * 
     * 注意：此方法不接受任何参数，也不返回任何值。
     */
    private void clearCache() {
        try {
            // 尝试清除缓存目录。cacheFolder之前应该已经被初始化为缓存目录的路径。
            DirectoryCleaner.clearDirectory(cacheFolder.toString());
        } catch (IOException e) {
            // 捕获并处理清除缓存时可能发生的IO异常。
            e.printStackTrace();
            System.out.println("Error clearing cache directory.");
        }
    }

    /**
     * 程序入口。
     * 从Fasta文件中读取DNA序列并转换为Bed格式，标注G4结构的功能。
     * 
     * @param args 命令行参数。
     */
    public static void main(String[] args) {
        if (args.length == 0 || args[0].equals("-h")) {
            System.out.println("Usage: java G4Fasta2Bed <fastaPath> <cacheFolder> <outputFolder> [-all | -f | -r]");
            System.out.println("用法: java G4Fasta2Bed <fasta文件路径> <缓存文件夹> <输出文件夹> [-all | -f | -r]");
            System.out.println("Options:");
            System.out.println("选项:");
            System.out.println("  -all  : Generate both forward and reverse strand G4 BED files");
            System.out.println("         生成正链和负链的G4 BED文件");
            System.out.println("  -f    : Generate forward strand G4 BED file only");
            System.out.println("         只生成正链的G4 BED文件");
            System.out.println("  -r    : Generate reverse strand G4 BED file only");
            System.out.println("         只生成负链的G4 BED文件");
            System.exit(0);
        }

        String fastaPath = args[0];
        String cacheFolder = args[1];
        String outputFolder = args[2];

        G4Fasta2Bed g4Fasta2Bed = new G4Fasta2Bed(fastaPath, cacheFolder, outputFolder);

        if (args.length > 3) {
            switch (args[3]) {
                case "-all":
                    g4Fasta2Bed.matchG4_All();
                    g4Fasta2Bed.clearCache();
                    break;
                case "-f":
                    g4Fasta2Bed.matchG4_F();
                    g4Fasta2Bed.clearCache();
                    break;
                case "-r":
                    g4Fasta2Bed.matchG4_R();
                    g4Fasta2Bed.clearCache();
                    break;
                default:
                    System.out.println("Invalid option / 非法选项: " + args[3]);
                    System.exit(1);
            }
        } else {
            g4Fasta2Bed.matchG4_All(); // 默认行为
        }
    }
}
