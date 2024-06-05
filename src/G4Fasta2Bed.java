import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private String[][] bedDataR;
    private String[][] bedDataF; 
    private Path fastaPath;
    private Path cacheFolder;
    private Path outputPath_F;
    private Path outputPath_R;
    private List<String> headers;
    private List<String> sequences;

    public G4Fasta2Bed(String fastaPath, String cacheFolder, String outputFolder) throws IOException {
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

        // 读取FASTA文件中的所有数据
        headers = new ArrayList<>();
        sequences = new ArrayList<>();
        for (String header : fastaSeq.getChromosomeHeaders()) {
            headers.add(header);
            sequences.add(fastaSeq.getSequenceByHeader(header).replaceAll("N", ""));
        }
    }

    /**
     * 针对FASTA序列中的每个染色体，查找并处理G4结构。
     * G4结构是一种特殊的DNA结构，由四个相邻的G碱基堆叠形成。
     * 此方法首先从FASTA序列中获取每个染色体的标题和序列，
     * 然后对每个序列进行处理，寻找并处理G4结构。
     * 最后，将找到的G4结构写入到指定路径的BED文件中。
     */
    private void matchG4_F() {
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            String sequence = sequences.get(i);

            // 对染色体序列进行处理，寻找G4结构
            processSequence(header, sequence, "+");

            // 将找到的G4结构写入BED文件
            try {
                synchronized (bedOutput) {
                    bedOutput.writeBEDFile(outputPath_F.toString(), bedDataF);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error writing to BED file.");
            }
        }
    }

    /**
     * 针对基因组中的负链DNA序列，寻找并处理G4结构。
     * G4结构是一种特殊的DNA结构，由四个G碱基通过氢键相互作用形成。
     * 本方法首先从FASTA文件中读取每个染色体的DNA序列，然后将这些序列转换为对应的负链序列，
     * 接着对转换后的序列进行G4结构的处理和分析，最后将分析结果写入到指定的BED文件中。
     * @throws IOException 
     */
    private void matchG4_R() throws IOException {
        ConvertSequence convert = new ConvertSequence();
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);
            String sequence = sequences.get(i);

            // 将正链DNA序列转换为负链序列
            String convertedSequence = convert.convertSequence(sequence);

            // 对转换后的负链序列进行G4结构的处理和分析
            processSequence(header, convertedSequence, "-");

            // 将分析得到的G4结构数据写入到指定的BED文件中
            try {
                synchronized (bedOutput) {
                    bedOutput.writeBEDFile(outputPath_R.toString(), bedDataR);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error writing to BED file.");
            }
        }
    }

    /**
     * 处理DNA序列，识别G4结构并转换为Bed格式数据。
     * 
     * @param header   序列的标题，用于标识序列来源或名称。
     * @param sequence DNA序列。
     * @param strand   链的方向。
     */
    private void processSequence(String header, String sequence, String strand) {
        // 设置G4匹配的输入内容
        g4Base.setInput(sequence);

        // 进行G4匹配
        ArrayList<String[]> matches = g4Base.matchPatterns(sequence);

        // 初始化bedData数组，大小为匹配结果的数量
        String[][] bedData = new String[matches.size()][8];
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

        if (strand.equals("+")) {
            bedDataF = bedData;
        } else if (strand.equals("-")) {
            bedDataR = bedData;
        }
    }

   

    /**
     * 匹配正链和负链的G4结构。
     * 
     * 本方法创建两个线程，一个用于匹配正链的G4结构，另一个用于匹配负链的G4结构。
     * 然后，等待两个线程都执行完毕。
     */
    public void matchG4_All_Paralle() {
        // 创建并启动线程执行matchG4_R
        Thread threadR = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    matchG4_R();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        // 创建并启动线程执行matchG4_F
        Thread threadF = new Thread(new Runnable() {
            @Override
            public void run() {
                matchG4_F();
            }
        });

        threadR.start();
        threadF.start();

        try {
            // 等待两个线程都执行完毕
            threadR.join();
            threadF.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void matchG4_All_Serial() throws IOException {
        matchG4_F();
        matchG4_R();
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
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0 || args[0].equals("-h")) {
            System.out.println("Usage: java G4Fasta2Bed <fastaPath> <cacheFolder> <outputFolder> [-all | -aS | -f | -r]");
            System.out.println("用法: java G4Fasta2Bed <fasta文件路径> <缓存文件夹> <输出文件夹> [-all | -aS | -f | -r]");
            System.out.println("Options:");
            System.out.println("选项:");
            System.out.println("  -aP  : Generate both forward and reverse strand G4 BED files in parallel method   [fast]");
            System.out.println("         并行方法   生成正链和负链的G4 BED文件  [快速]");
            System.out.println("  -aS   : Generate both forward and reverse strand G4 BED files in serial method    slow]");
            System.out.println("         串行方法   生成正链和负链的G4 BED文件  [慢速]");
            System.out.println("  -f    : Generate forward strand G4 BED file only");
            System.out.println("         只生成正链的G4 BED文件");
            System.out.println("  -r    : Generate reverse strand G4 BED file only");
            System.out.println("         只生成负链的G4 BED文件");
            System.out.println("  -h    : Print this help message and exit");
            System.out.println("         打印帮助信息并退出");
            System.out.println("Note:");
            System.out.println("备注:");
            System.out.println("The default is to use the parallel method, when the Fasta file to be processed is larger than 1.5GB, it is recommended to use the serial method. If you want to force the parallel method, please add the `-Xmx` parameter to the java command, at least `-Xmx8G`.");
            System.out.println("默认使用并行方法, 当要处理的Fasta文件大于1.5GB时, 建议使用串行方法。如果希望强行使用并行方法, 请在java命令后加上 `-Xmx` 参数，至少为 `-Xmx8G` 。");
            System.exit(0);
        }

        String fastaPath = args[0];
        String cacheFolder = args[1];
        String outputFolder = args[2];

        G4Fasta2Bed g4Fasta2Bed = new G4Fasta2Bed(fastaPath, cacheFolder, outputFolder);

        if (args.length > 3) {
            switch (args[3]) {
                case "-aP":
                    g4Fasta2Bed.matchG4_All_Paralle();
                    g4Fasta2Bed.clearCache();
                    break;
                case "-aS":
                    g4Fasta2Bed.matchG4_All_Serial();
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
            g4Fasta2Bed.matchG4_All_Paralle(); // 默认行为
            g4Fasta2Bed.clearCache();
        }
    }
}