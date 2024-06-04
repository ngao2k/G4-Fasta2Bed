# G4-Fasta2Bed

## English Guide
[Click here for English Guide](./README-EN.md)

## 概述
本项目包括几个用于基序匹配、序列分析、读取和处理FASTA文件以及在FASTA文件中匹配G4序列并将结果写入BED文件的Java类。

## 类
- BedOutput.java
    - 此类处理BED文件的输出。
- FastaInput.java
    - 此类负责读取和处理FASTA文件。
- G4_Base.java
    - 此类提供了G4序列分析的基本功能。
- G4Fasta2Bed.java
    - 此类集成了其他类的功能，用于在FASTA文件中匹配G4序列并将结果写入BED文件。

## 使用方法
- 获取`jar`文件
    - 在右侧的[Release](https://github.com/ngao2k/G4-Fasta2Bed/releases)中下载。
- 使用必要的参数运行JAR文
```
java -jar G4-Fasta2Bed.jar <input_fasta_file> <cache_folder>  <output_Folder>
```
- 示例
```
java -jar G4-Fasta2Bed.jar input.fasta cacheFolder outputFolder
```