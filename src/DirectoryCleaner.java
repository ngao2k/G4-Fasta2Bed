import java.io.File;
import java.io.IOException;

public class DirectoryCleaner {

    /**
     * 清空指定目录内的所有文件和子目录。
     *
     * @param directoryPath 要清空的目录路径
     * @throws IOException 如果删除过程中发生错误
     */
    public static void clearDirectory(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("The specified path is not a valid directory: " + directoryPath);
        }

        File[] files = directory.listFiles();
        if (files == null) {
            throw new IOException("Failed to list contents of directory: " + directoryPath);
        }

        for (File file : files) {
            deleteRecursively(file);
        }
    }

    /**
     * 递归删除文件或目录。
     *
     * @param file 要删除的文件或目录
     * @throws IOException 如果删除过程中发生错误
     */
    private static void deleteRecursively(File file) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                throw new IOException("Failed to list contents of directory: " + file.getAbsolutePath());
            }
            for (File child : files) {
                deleteRecursively(child);
            }
        }
        if (!file.delete()) {
            throw new IOException("Failed to delete file or directory: " + file.getAbsolutePath());
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java DirectoryCleaner <directory path>");
            return;
        }

        String directoryPath = args[0];
        try {
            clearDirectory(directoryPath);
            System.out.println("Directory cleared successfully: " + directoryPath);
        } catch (IOException e) {
            System.err.println("Error clearing directory: " + e.getMessage());
        }
    }
}
