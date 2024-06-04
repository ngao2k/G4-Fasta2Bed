import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * BedOutput 类用于生成 BED 格式的文件。
 * BED 格式是一种常见的基因组数据格式，用于表示基因组中的区间或特征。
 */
public class BedOutput {
    // 定义BED文件的列标题，这些标题对应着每行数据的各个字段
    private static String[] headers = {
            "chromosome", // 染色体编号
            "start", // 起始位置
            "end", // 终止位置
            "id", // 区间ID
            "length", // 区间长度
            "strand", // 链的方向（正负链）
            "sequence", // 序列
            "G4_type" // 识别到的G4类别
    };

    /**
     * 将给定的数据写入BED格式的文件。
     * BED格式是一种用于表示基因组数据的常见格式，此函数将二维字符串数组的数据写入到指定路径的文件中，每行代表一个基因组区间。
     *
     * @param outputPath 文件输出的路径，包括文件名和扩展名。
     * @param data       一个二维字符串数组，其中每个子数组代表BED文件中的一行数据，数据顺序应与headers数组对应。
     *                   1. "chromosome" - 染色体编号
     *                   2. "start" - 起始位置
     *                   3. "end" - 终止位置
     *                   4. "id" - 区间ID
     *                   5. "length" - 区间长度
     *                   6. "strand" - 链的方向（正负链）
     *                   7. "sequence" - 序列
     *                   8. "strand_info" - 链的信息
     *                   9. "G4_type" - 识别到的G4类别
     * @throws IOException 如果在写入文件过程中发生I/O错误。
     */
    public void writeBEDFile(String outputPath, String[][] data) throws IOException {
        // 使用BufferedWriter提高文件写入性能，并通过FileWriter指定写入的文件
        // 使用 BufferedWriter 提高文件写入性能
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            // 遍历headers数组，写入文件的列标题行
            // 写入列头
            for (int i = 0; i < headers.length; i++) {
                writer.write(headers[i]);
                // 除最后一列外，每列之后添加制表符作为分隔符
                if (i < headers.length - 1) {
                    writer.write("\t");
                }
            }
            writer.newLine(); // 写入一行空行作为标题行和数据行的分隔

            // 遍历data数组，写入每一行的数据
            // 写入数据行
            for (String[] row : data) {
                for (int i = 0; i < row.length; i++) {
                    writer.write(row[i]);
                    // 除最后一列外，每列之后添加制表符作为分隔符
                    if (i < row.length - 1) {
                        writer.write("\t");
                    }
                }
                writer.newLine(); // 每行数据写入完毕后，写入一行空行作为分隔
            }
        }
    }

    /**
     * 程序入口点。
     * 示例演示了如何使用 BedOutput 类生成 BED 文件。
     * 
     * @param args 命令行参数。
     */
    public static void main(String[] args) {
        // 创建一个示例数据集
        String outputPath = "output.bed";
        String[][] data = {
                { "chr1", "1000", "1050", "id1", "50", "+", "ATCG", "Type1" },
                { "chr2", "2000", "2050", "id2", "50", "-", "CGTA", "Type2" }
        };
        BedOutput bedOutput = new BedOutput();
        try {
            bedOutput.writeBEDFile(outputPath, data);
            System.out.println("BED file generated successfully.");
        } catch (IOException e) {
            System.err.println("Error writing BED file: " + e.getMessage());
        }
    }
}