package by.it.group410971.posvenchuk.lesson07;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
Задача на программирование: расстояние Левенштейна
    https://ru.wikipedia.org/wiki/Расстояние_Левенштейна
    http://planetcalc.ru/1721/

Дано:
    Две данных непустые строки длины не более 100, содержащие строчные буквы латинского алфавита.

Необходимо:
    Решить задачу МЕТОДАМИ ДИНАМИЧЕСКОГО ПРОГРАММИРОВАНИЯ
    Рекурсивно вычислить расстояние редактирования двух данных непустых строк

    Sample Input 1:
    ab
    ab
    Sample Output 1:
    0

    Sample Input 2:
    short
    ports
    Sample Output 2:
    3

    Sample Input 3:
    distance
    editing
    Sample Output 3:
    5

*/

public class A_EditDist {


    int getDistanceEdinting(String one, String two) {
        int m = one.length();
        int n = two.length();
        int[][] cache = new int[m+1][n+1];
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                cache[i][j] = -1;
            }
        }
        return recursiveEditDistance(one, two, m, n, cache);
    }

    private int recursiveEditDistance(String one, String two, int i, int j, int[][] cache) {
        if (cache[i][j] != -1) {
            return cache[i][j];
        }
        if (i == 0) {
            cache[i][j] = j;
            return j;
        }
        if (j == 0) {
            cache[i][j] = i;
            return i;
        }
        int cost = (one.charAt(i-1) == two.charAt(j-1)) ? 0 : 1;
        int delete = recursiveEditDistance(one, two, i-1, j, cache) + 1;
        int insert = recursiveEditDistance(one, two, i, j-1, cache) + 1;
        int replace = recursiveEditDistance(one, two, i-1, j-1, cache) + cost;
        int result = Math.min(Math.min(delete, insert), replace);
        cache[i][j] = result;
        return result;
    }


    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = A_EditDist.class.getResourceAsStream("dataABC.txt");
        A_EditDist instance = new A_EditDist();
        Scanner scanner = new Scanner(stream);
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
    }
}
