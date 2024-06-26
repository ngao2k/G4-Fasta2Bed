# G4-Fasta2Bed

## English Guide
[Click here for English Guide](./README-EN.md)

## 概述
本项目包括几个用于基序匹配、序列分析、读取和处理FASTA文件以及在FASTA文件中匹配G4序列并将结果写入 BED 文件的 Java 类。

## 类
- BedOutput.java
    - 此类处理 BED 文件的输出。
- FastaInput.java
    - 此类负责读取和处理 FASTA 文件。
- G4_Base.java
    - 此类提供了 G4 序列分析的基本功能。
- G4Fasta2Bed.java
    - 此类集成了其他类的功能，用于在FASTA文件中匹配G4序列并将结果写入BED文件。
- DirectoryCleaner.java
    - 此类用于清理缓存文件夹。

## 使用方法
- 获取`jar`文件
    - 在右侧的[Release](https://github.com/ngao2k/G4-Fasta2Bed/releases)中下载。
- 使用必要的参数运行 JAR 文件
```
java -jar G4-Fasta2Bed.jar <input_fasta_file> <cache_folder>  <output_Folder> [Parameters]
```
- 可选参数
    - 在不设置参数时，默认使用 `-aP` 方法执行。
    - `-aP`：使用并行方法进行处理。
    - `-aS`：使用串行方法进行处理。
    - `-f`：只生成正链的 G4 BED 文件。
    - `-r`：只生成负链的 G4 BED 文件。
    - `-h`：显示帮助信息。
- 示例
```
java -jar G4-Fasta2Bed.jar input.fasta cacheFolder outputFolder -aP
```

> [!IMPORTANT]
>    - 如果您希望同时运行多个实例，请确保每个实例的缓存文件夹都不相同。
>    - 如果您希望同时运行多个实例，请确保您有充足的内存。
>    - 缓存文件夹中的文件会被自动清理，但请不要删除它们。
