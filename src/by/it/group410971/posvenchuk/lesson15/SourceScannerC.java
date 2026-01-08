package by.it.group410971.posvenchuk.lesson15;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.MalformedInputException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class SourceScannerC {

    static class FileData {
        String path;
        String content;

        FileData(String path, String content) {
            this.path = path;
            this.content = content;
        }
    }

    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;
        List<FileData> fileDataList = new ArrayList<>();

        try {
            scanDirectory(Paths.get(src), fileDataList);

            // Находим копии
            Map<String, List<String>> copiesMap = findCopies(fileDataList);

            // Сортируем ВСЕ пути файлов (не только с дубликатами)
            List<String> allFilePaths = new ArrayList<>();
            for (FileData fd : fileDataList) {
                allFilePaths.add(fd.path);
            }
            Collections.sort(allFilePaths);

            // Выводим результат: для каждого файла выводим его путь
            // и если есть дубликаты — выводим их под ним
            for (String filePath : allFilePaths) {
                System.out.println(filePath);
                List<String> copies = copiesMap.get(filePath);
                if (copies != null && !copies.isEmpty()) {
                    Collections.sort(copies);
                    for (String copyPath : copies) {
                        System.out.println(copyPath);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void scanDirectory(Path startPath, List<FileData> fileDataList) throws IOException {
        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".java")) {
                    processJavaFile(file, startPath, fileDataList);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void processJavaFile(Path file, Path srcRoot, List<FileData> fileDataList) {
        try {
            String content = readFileWithFallback(file);

            if (isTestFile(content)) {
                return;
            }

            String processedContent = processContent(content);
            Path relativePath = srcRoot.relativize(file);

            fileDataList.add(new FileData(relativePath.toString(), processedContent));

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
        // Удаляем комментарии
        StringBuilder noComments = removeComments(content);

        // Разделяем на строки
        String[] lines = noComments.toString().split("\\r?\\n");
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            String trimmed = line.trim();

            // Пропускаем package и import строки
            if (trimmed.startsWith("package ") || trimmed.startsWith("import ")) {
                continue;
            }

            // Добавляем строку
            if (!trimmed.isEmpty()) {
                result.append(line).append(" ");
            }
        }

        // Заменяем последовательности символов с кодом <33 на один пробел
        String processed = result.toString();
        processed = normalizeWhitespace(processed);

        // Выполняем trim
        return processed.trim();
    }

    private static StringBuilder removeComments(String content) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        int n = content.length();

        while (i < n) {
            // Однострочные комментарии
            if (i + 1 < n && content.charAt(i) == '/' && content.charAt(i + 1) == '/') {
                while (i < n && content.charAt(i) != '\n') {
                    i++;
                }
                if (i < n && content.charAt(i) == '\n') {
                    result.append('\n');
                    i++;
                }
            }
            // Многострочные комментарии
            else if (i + 1 < n && content.charAt(i) == '/' && content.charAt(i + 1) == '*') {
                i += 2;
                while (i + 1 < n && !(content.charAt(i) == '*' && content.charAt(i + 1) == '/')) {
                    i++;
                }
                i += 2;
            }
            // Обычный символ
            else {
                result.append(content.charAt(i));
                i++;
            }
        }

        return result;
    }

    private static String normalizeWhitespace(String str) {
        StringBuilder result = new StringBuilder();
        boolean lastWasWhitespace = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < 33) {
                if (!lastWasWhitespace) {
                    result.append(' ');
                    lastWasWhitespace = true;
                }
            } else {
                result.append(c);
                lastWasWhitespace = false;
            }
        }

        return result.toString();
    }

    private static Map<String, List<String>> findCopies(List<FileData> fileDataList) {
        Map<String, List<String>> copiesMap = new HashMap<>();
        int n = fileDataList.size();

        // Используем оптимизацию: сравниваем только файлы с похожей длиной
        for (int i = 0; i < n; i++) {
            FileData file1 = fileDataList.get(i);
            String content1 = file1.content;

            for (int j = i + 1; j < n; j++) {
                FileData file2 = fileDataList.get(j);
                String content2 = file2.content;

                // Быстрая проверка: если длины сильно различаются, не сравниваем
                if (Math.abs(content1.length() - content2.length()) > 20) {
                    continue;
                }

                // Вычисляем расстояние Левенштейна
                int distance = levenshteinDistance(content1, content2);

                // Если расстояние < 10, считаем копией
                if (distance < 10) {
                    // Добавляем file2 как копию для file1
                    copiesMap.computeIfAbsent(file1.path, k -> new ArrayList<>()).add(file2.path);
                    // Добавляем file1 как копию для file2
                    copiesMap.computeIfAbsent(file2.path, k -> new ArrayList<>()).add(file1.path);
                }
            }
        }

        return copiesMap;
    }

    private static int levenshteinDistance(String s1, String s2) {
        // Оптимизированная версия с использованием двух строк
        int m = s1.length();
        int n = s2.length();

        // Если одна из строк пустая, расстояние = длина другой строки
        if (m == 0) return n;
        if (n == 0) return m;

        // Используем только два ряда для экономии памяти
        int[] prevRow = new int[n + 1];
        int[] currRow = new int[n + 1];

        // Инициализация первого ряда
        for (int j = 0; j <= n; j++) {
            prevRow[j] = j;
        }

        // Заполнение матрицы
        for (int i = 1; i <= m; i++) {
            currRow[0] = i;

            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                currRow[j] = Math.min(
                        Math.min(
                                prevRow[j] + 1,      // удаление
                                currRow[j - 1] + 1   // вставка
                        ),
                        prevRow[j - 1] + cost    // замена
                );
            }

            // Обновляем ряды
            int[] temp = prevRow;
            prevRow = currRow;
            currRow = temp;
        }

        return prevRow[n];
    }
}