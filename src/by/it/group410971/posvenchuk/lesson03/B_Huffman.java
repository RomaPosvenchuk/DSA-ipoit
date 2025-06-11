package by.it.group410971.posvenchuk.lesson03;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Lesson 3. B_Huffman.
// Восстановите строку по её коду и беспрефиксному коду символов.

// В первой строке входного файла заданы два целых числа
// kk и ll через пробел — количество различных букв, встречающихся в строке,
// и размер получившейся закодированной строки, соответственно.
//
// В следующих kk строках записаны коды букв в формате "letter: code".
// Ни один код не является префиксом другого.
// Буквы могут быть перечислены в любом порядке.
// В качестве букв могут встречаться лишь строчные буквы латинского алфавита;
// каждая из этих букв встречается в строке хотя бы один раз.
// Наконец, в последней строке записана закодированная строка.
// Исходная строка и коды всех букв непусты.
// Заданный код таков, что закодированная строка имеет минимальный возможный размер.
//
//        Sample Input 1:
//        1 1
//        a: 0
//        0

//        Sample Output 1:
//        a


//        Sample Input 2:
//        4 14
//        a: 0
//        b: 10
//        c: 110
//        d: 111
//        01001100100111

//        Sample Output 2:
//        abacabad

public class B_Huffman {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream inputStream = B_Huffman.class.getResourceAsStream("dataB.txt");
        B_Huffman instance = new B_Huffman();
        String result = instance.decode(inputStream);
        System.out.println(result);
    }

    String decode(InputStream inputStream) throws FileNotFoundException {
        StringBuilder result = new StringBuilder();
        Scanner scanner = new Scanner(inputStream);

        // Чтение количества символов и длины закодированной строки
        int k = scanner.nextInt();
        int l = scanner.nextInt();
        scanner.nextLine(); // Пропуск оставшейся части строки

        // Создание карты для хранения кодов (битовая последовательность -> символ)
        Map<String, Character> codeMap = new HashMap<>();

        // Чтение k строк с кодами символов
        for (int i = 0; i < k; i++) {
            String line = scanner.nextLine();
            // Разделение строки на символ и его код
            String[] parts = line.split(": ");
            char letter = parts[0].charAt(0);
            String code = parts[1];
            codeMap.put(code, letter);
        }

        // Чтение закодированной строки
        String encoded = scanner.nextLine();

        // Декодирование строки
        StringBuilder currentCode = new StringBuilder();
        for (char c : encoded.toCharArray()) {
            currentCode.append(c);
            // Проверка наличия текущего кода в карте
            if (codeMap.containsKey(currentCode.toString())) {
                // Добавление соответствующего символа к результату
                result.append(codeMap.get(currentCode.toString()));
                currentCode.setLength(0); // Сброс текущего кода
            }
        }

        return result.toString();
    }


}
