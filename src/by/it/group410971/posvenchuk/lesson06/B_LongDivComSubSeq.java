package by.it.group410971.posvenchuk.lesson06;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
Задача на программирование: наибольшая кратная подпоследовательность

Дано:
    целое число 1≤n≤1000
    массив A[1…n] натуральных чисел, не превосходящих 2E9.

Необходимо:
    Выведите максимальное 1<=k<=n, для которого гарантированно найдётся
    подпоследовательность индексов i[1]<i[2]<…<i[k] <= длины k,
    для которой каждый элемент A[i[k]] делится на предыдущий
    т.е. для всех 1<=j<k, A[i[j+1]] делится на A[i[j]].

Решить задачу МЕТОДАМИ ДИНАМИЧЕСКОГО ПРОГРАММИРОВАНИЯ

    Sample Input:
    4
    3 6 7 12

    Sample Output:
    3
*/

public class B_LongDivComSubSeq {


    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = B_LongDivComSubSeq.class.getResourceAsStream("dataB.txt");
        B_LongDivComSubSeq instance = new B_LongDivComSubSeq();
        int result = instance.getDivSeqSize(stream);
        System.out.print(result);
    }


    int getDivSeqSize(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        int n = scanner.nextInt();
        int[] m = new int[n];
        for (int i = 0; i < n; i++) {
            m[i] = scanner.nextInt();
        }

        int[] dp = new int[n]; // Массив для хранения длин подпоследовательностей
        int maxLength = 0; // Максимальная длина подпоследовательности

        for (int i = 0; i < n; i++) {
            dp[i] = 1; // Изначально каждый элемент образует подпоследовательность длины 1
            for (int j = 0; j < i; j++) {
                if (m[i] % m[j] == 0) { // Проверяем, делится ли текущий элемент на предыдущий
                    if (dp[j] + 1 > dp[i]) {
                        dp[i] = dp[j] + 1; // Обновляем длину подпоследовательности
                    }
                }
            }
            if (dp[i] > maxLength) {
                maxLength = dp[i]; // Обновляем максимальную длину
            }
        }

        return maxLength;
    }

}