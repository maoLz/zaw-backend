package com.zaw.aicode.tool;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.regex.Pattern;

public class SplitByHuiTitle {

    // 匹配：第一百一十二回 ... / 第112回 ... / 第一一二回 ...
    private static final Pattern CHAPTER_TITLE =
            Pattern.compile("^\\s*第[一二三四五六七八九十百千零〇0-9]+回.*$");

    // public static void main(String[] args) throws IOException {
       

    //     Path input = Paths.get("/Users/zhanglizhong/Downloads/三国演义.txt");
    //     Path outDir = Paths.get("/Users/zhanglizhong/personalProjects/zaw-project/sanguo");
    //     Files.createDirectories(outDir);

    //     split(input, outDir);
    //     System.out.println("完成分割，输出目录: " + outDir.toAbsolutePath());
    // }

    public static void split(Path inputFile, Path outputDir) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(inputFile, Charset.forName("GB18030"))) {

            BufferedWriter currentWriter = null;
            String currentTitle = null;
            int chapterIndex = 0;

            String line;
            while ((line = br.readLine()) != null) {
                // 判断是否为回目行
                if (CHAPTER_TITLE.matcher(line).matches()) {
                    // 遇到新章节：关闭旧 writer
                    if (currentWriter != null) {
                        currentWriter.flush();
                        currentWriter.close();
                    }

                    chapterIndex++;
                    currentTitle = line.trim();

                    String fileName = buildFileName(chapterIndex, currentTitle);
                    Path outFile = outputDir.resolve(fileName);

                    currentWriter = Files.newBufferedWriter(outFile, StandardCharsets.UTF_8,
                            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                    // 把回目行也写进该章节文件
                    currentWriter.write(currentTitle);
                    currentWriter.newLine();
                    continue;
                }

                // 如果正文在第一回之前（序言/目录），你可以选择丢弃或写入一个前言文件
                if (currentWriter == null) {
                    // 这里默认：跳过回目之前的内容
                    continue;
                }

                // 写正文行
                currentWriter.write(line);
                currentWriter.newLine();
            }

            // 关闭最后一个 writer
            if (currentWriter != null) {
                currentWriter.flush();
                currentWriter.close();
            }
        }
    }

    private static String buildFileName(int idx, String titleLine) {
        // 文件名：001_第一百一十二回_救寿春于诠死节_取长城伯约鏖兵.txt
        String safeTitle = sanitizeFileName(titleLine);

        // 过长会影响某些系统，截断一下更安全
        int maxLen = 80;
        if (safeTitle.length() > maxLen) safeTitle = safeTitle.substring(0, maxLen);

        return String.format("%03d_%s.txt", idx, safeTitle);
    }

    private static String sanitizeFileName(String s) {
        // Windows/macOS/Linux 常见非法字符清理
        // 另外把空白压成下划线，便于管理
        String cleaned = s.replaceAll("[\\\\/:*?\"<>|]", "_")
                          .replaceAll("\\s+", "_")
                          .replaceAll("_+", "_")
                          .replaceAll("^_+", "")
                          .replaceAll("_+$", "");
        return cleaned;
    }
}
