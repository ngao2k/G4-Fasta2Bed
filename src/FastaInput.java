import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * FastaInput类用于读取和处理FASTA格式的文件。
 * 该类将FASTA文件按染色体分割成不同的二进制文件并缓存到指定位置。
 */
public class FastaInput {
    private final Map<String, Path> chromosomeFiles;
    private final Path cacheDir;

    /**
     * 构造函数初始化一个空的染色体文件映射和缓存目录。
     */
    public FastaInput(String filePath, String cacheDirPath) {
        chromosomeFiles = new HashMap<>();
        cacheDir = Paths.get(cacheDirPath);
        try {
            Files.createDirectories(cacheDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        splitFastaFile(filePath);
    }

    /**
     * 将FASTA文件按染色体分割成不同的二进制文件并缓存到指定位置。
     *
     * @param filePath 文件路径
     */
    private void splitFastaFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String header = null;
            ByteArrayOutputStream sequenceStream = new ByteArrayOutputStream();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(">")) {
                    if (header != null) {
                        writeChromosomeFile(header, sequenceStream.toByteArray());
                    }
                    header = line.substring(1).split("\\s+")[0].trim();
                    sequenceStream = new ByteArrayOutputStream();
                } else {
                    sequenceStream.write(line.trim().getBytes());
                }
            }
            if (header != null) {
                writeChromosomeFile(header, sequenceStream.toByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将序列写入对应的染色体文件。
     *
     * @param header          染色体的标题
     * @param sequenceContent 序列内容的字节数组
     */
    private void writeChromosomeFile(String header, byte[] sequenceContent) {
        try {
            Path filePath = cacheDir.resolve(header + ".bin");
            Files.write(filePath, sequenceContent);
            chromosomeFiles.put(header, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据标题获取序列。
     * 通过提供的标题从缓存文件中读取对应的序列。
     *
     * @param header 标题，用于查找对应的序列。
     * @return 如果标题存在，则返回对应的序列；否则返回一个指示性字符串。
     */
    public String getSequenceByHeader(String header) {
        Path filePath = chromosomeFiles.get(header);
        if (filePath == null) {
            return "Sequence not found for header: " + header;
        }
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading sequence for header: " + header;
        }
    }

    /**
     * 获取所有染色体文件的标题列表。
     *
     * @return 染色体文件的标题列表
     */
    public Set<String> getChromosomeHeaders() {
        return chromosomeFiles.keySet();
    }

    /**
     * 程序的入口点。
     * 该方法演示了如何使用FastaInput类来读取和处理FASTA文件，并按染色体分割存储。
     *
     * @param args 命令行参数，应包含FASTA文件的路径和缓存目录的路径。
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java FastaInput <FASTA file path> <cache directory path>");
            return;
        }

        String fastaFilePath = args[0];
        String cacheDirPath = args[1];

        FastaInput fastaInput = new FastaInput(fastaFilePath, cacheDirPath);

        for (String header : fastaInput.getChromosomeHeaders()) {
            System.out.println("Header: " + header);
            System.out.println("Sequence: " + fastaInput.getSequenceByHeader(header));
        }
    }
}
