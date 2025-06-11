package by.it.group410971.posvenchuk.lesson05;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/*
Видеорегистраторы и площадь 2.
Условие то же что и в задаче А.

        По сравнению с задачей A доработайте алгоритм так, чтобы
        1) он оптимально использовал время и память:
            - за стек отвечает элиминация хвостовой рекурсии
            - за сам массив отрезков - сортировка на месте
            - рекурсивные вызовы должны проводиться на основе 3-разбиения

        2) при поиске подходящих отрезков для точки реализуйте метод бинарного поиска
        для первого отрезка решения, а затем найдите оставшуюся часть решения
        (т.е. отрезков, подходящих для точки, может быть много)

    Sample Input:
    2 3
    0 5
    7 10
    1 6 11
    Sample Output:
    1 0 0

*/


public class C_QSortOptimized {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_QSortOptimized.class.getResourceAsStream("dataC.txt");
        C_QSortOptimized instance = new C_QSortOptimized();
        int[] result = instance.getAccessory2(stream);
        for (int index : result) {
            System.out.print(index + " ");
        }
    }

    int[] getAccessory2(InputStream stream) {
        Scanner scanner = new Scanner(stream);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        int[] starts = new int[n];
        int[] ends = new int[n];
        int[] points = new int[m];
        int[] result = new int[m];

        for (int i = 0; i < n; i++) {
            starts[i] = scanner.nextInt();
            ends[i] = scanner.nextInt();
        }
        for (int i = 0; i < m; i++) {
            points[i] = scanner.nextInt();
        }

        quickSort3Way(starts, 0, starts.length - 1);
        quickSort3Way(ends, 0, ends.length - 1);

        for (int i = 0; i < m; i++) {
            int p = points[i];
            int count1 = upperBound(starts, p);
            int count2 = lowerBound(ends, p);
            result[i] = count1 - count2;
        }

        return result;
    }

    private void quickSort3Way(int[] a, int lo, int hi) {
        while (lo < hi) {
            int[] p = partition3(a, lo, hi);
            if (p[0] - lo < hi - p[1]) {
                quickSort3Way(a, lo, p[0] - 1);
                lo = p[1] + 1;
            } else {
                quickSort3Way(a, p[1] + 1, hi);
                hi = p[0] - 1;
            }
        }
    }

    private int[] partition3(int[] a, int lo, int hi) {
        int pivot = a[lo];
        int lt = lo;
        int gt = hi;
        int i = lo + 1;
        while (i <= gt) {
            if (a[i] < pivot) {
                swap(a, lt, i);
                lt++;
                i++;
            } else if (a[i] > pivot) {
                swap(a, i, gt);
                gt--;
            } else {
                i++;
            }
        }
        return new int[]{lt, gt};
    }

    private void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    private int upperBound(int[] a, int key) {
        int low = 0;
        int high = a.length;
        while (low < high) {
            int mid = (low + high) / 2;
            if (a[mid] <= key) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }

    private int lowerBound(int[] a, int key) {
        int low = 0;
        int high = a.length;
        while (low < high) {
            int mid = (low + high) / 2;
            if (a[mid] < key) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }



    //отрезок
    private class Segment implements Comparable {
        int start;
        int stop;

        Segment(int start, int stop) {
            this.start = start;
            this.stop = stop;
        }

        @Override
        public int compareTo(Object o) {
            //подумайте, что должен возвращать компаратор отрезков
            return 0;
        }
    }

}
