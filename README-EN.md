# G4-Fasta2Bed

## 中文指南
[点击这里获取中文指南](./README.md)

## Overview
This project includes several Java classes for motif matching, sequence analysis, reading and processing FASTA files, and matching G4 sequences in FASTA files and writing the results to BED files.

## Classes
- BedOutput.java
    - This class handles the output of BED files.
- FastaInput.java
    - This class is responsible for reading and processing FASTA files.
- G4_Base.java
    - This class provides the basic functionalities for G4 sequence analysis.
- G4Fasta2Bed.java
    - This class integrates the functionalities of other classes to match G4 sequences in FASTA files and write the results to BED files.
- DirectoryCleaner.java
    - This class is used to clean up the cache folder.

## Usage
- Obtain the `jar` file
    - Download it from the [Release](https://github.com/ngao2k/G4-Fasta2Bed/releases) on the right side.
- Run the JAR file with the necessary parameters
```
java -jar G4-Fasta2Bed.jar <input_fasta_file> <cache_folder> <output_folder> [Parameters]
```
- Optional Parameters
    - If no parameters are set, the default execution method is `-aP`.
    - `-aP`: Process using parallel method.
    - `-aS`: Process using serial method.
    - `-f`: Generate G4 BED file for the positive strand only.
    - `-r`: Generate G4 BED file for the negative strand only.
    - `-h`: Display help information.
- Example
```
java -jar G4-Fasta2Bed.jar input.fasta cacheFolder outputFolder -aP
```

> [!IMPORTANT]
>    - If you want to run multiple instances simultaneously, ensure that each instance has a different cache folder.
>    - If you want to run multiple instances simultaneously, ensure that you have sufficient memory.
>    - Files in the cache folder will be automatically cleaned, but please do not delete them manually.