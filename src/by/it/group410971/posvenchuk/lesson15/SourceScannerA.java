package by.it.group410971.posvenchuk.lesson15;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.MalformedInputException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class SourceScannerA {

    static class FileInfo implements Comparable<FileInfo> {
        long size;
        String path;

        FileInfo(long size, String path) {
            this.size = size;
            this.path = path;
        }

        @Override
        public int compareTo(FileInfo other) {
            if (this.size != other.size) {
                return Long.compare(this.size, other.size);
            }
            return this.path.compareTo(other.path);
        }
    }

    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;
        List<FileInfo> fileInfos = new ArrayList<>();

        try {
            scanDirectory(Paths.get(src), fileInfos);

            Collections.sort(fileInfos);

            for (FileInfo info : fileInfos) {
                System.out.println(info.size + " " + info.path);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void scanDirectory(Path startPath, List<FileInfo> fileInfos) throws IOException {
        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".java")) {
                    processJavaFile(file, startPath, fileInfos);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void processJavaFile(Path file, Path srcRoot, List<FileInfo> fileInfos) {
        try {
            String content = readFileWithFallback(file);

            if (isTestFile(content)) {
                return;
            }

            String processedContent = processContent(content);
            long size = processedContent.getBytes(StandardCharsets.UTF_8).length;

            // Получаем относительный путь БЕЗ изменения разделителей
            Path relativePath = srcRoot.relativize(file);

            fileInfos.add(new FileInfo(size, relativePath.toString()));

        } catch (IOException e) {
            // Игнорируем файлы с ошибками чтения
        }
    }

    private static String readFileWithFallback(Path file) throws IOException {
        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (MalformedInputException e) {
            List<Charset> charsets = Arrays.asList(
                    StandardCharsets.ISO_8859_1,
                    Charset.forName("Windows-1251"),
                    StandardCharsets.US_ASCII
            );

            for (Charset charset : charsets) {
                try {
                    return Files.readString(file, charset);
                } catch (MalformedInputException ex) {
                    continue;
                }
            }

            byte[] bytes = Files.readAllBytes(file);
            return new String(bytes, StandardCharsets.UTF_8)
                    .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "");
        }
    }

    private static boolean isTestFile(String content) {
        // Более точная проверка на тестовые файлы
        String[] lines = content.split("\\r?\\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("@Test") || trimmed.contains("org.junit.Test")) {
                return true;
            }
        }
        return false;
    }

    private static String processContent(String content) {
        String[] lines = content.split("\\r?\\n");
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            String trimmed = line.trim();
            // Пропускаем ТОЛЬКО строки, начинающиеся с package или import
            if (trimmed.startsWith("package ") || trimmed.startsWith("import ")) {
                continue;
            }
            result.append(line).append("\n");
        }

        String processed = result.toString();
        return trimControlChars(processed);
    }

    private static String trimControlChars(String str) {
        if (str.isEmpty()) {
            return str;
        }

        int start = 0;
        while (start < str.length() && str.charAt(start) < 33) {
            start++;
        }

        int end = str.length();
        while (end > start && str.charAt(end - 1) < 33) {
            end--;
        }

        return str.substring(start, end);
    }
}