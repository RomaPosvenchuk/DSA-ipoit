package by.it.group410971.posvenchuk.lesson14;

import java.util.Scanner;

public class StatesHanoiTowerC {

    static class DSU {
        private int[] parent;
        private int[] size;

        public DSU(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                if (size[rootX] < size[rootY]) {
                    parent[rootX] = rootY;
                    size[rootY] += size[rootX];
                } else {
                    parent[rootY] = rootX;
                    size[rootX] += size[rootY];
                }
            }
        }

        public int getSize(int x) {
            return size[find(x)];
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        scanner.close();

        int totalSteps = (1 << N) - 1; // 2^N - 1

        // Вместо DSU будем просто считать сколько раз встречается каждое состояние
        // Максимально возможное состояние = N (все диски на одной башне)
        int[] stateCounts = new int[N + 1];

        // Генерируем состояния и считаем их
        generateAndCountStates(N, 'A', 'B', 'C',
                new int[]{N, 0, 0},
                new int[]{0},
                stateCounts);

        // Теперь нам нужно получить размеры групп
        // Количество групп = количество ненулевых элементов в stateCounts
        int groupCount = 0;
        for (int count : stateCounts) {
            if (count > 0) {
                groupCount++;
            }
        }

        // Собираем ненулевые размеры
        int[] groupSizes = new int[groupCount];
        int index = 0;
        for (int count : stateCounts) {
            if (count > 0) {
                groupSizes[index++] = count;
            }
        }

        // Сортируем
        bubbleSort(groupSizes);

        // Выводим
        for (int i = 0; i < groupSizes.length; i++) {
            System.out.print(groupSizes[i]);
            if (i < groupSizes.length - 1) {
                System.out.print(" ");
            }
        }
    }

    static void generateAndCountStates(int n, char from, char to, char aux,
                                       int[] heights, int[] stepCounter, int[] stateCounts) {
        if (n == 0) return;

        generateAndCountStates(n-1, from, aux, to, heights, stepCounter, stateCounts);

        moveDisk(from, to, heights);

        int maxHeight = Math.max(Math.max(heights[0], heights[1]), heights[2]);
        stateCounts[maxHeight]++;
        stepCounter[0]++;

        generateAndCountStates(n-1, aux, to, from, heights, stepCounter, stateCounts);
    }

    static void moveDisk(char from, char to, int[] heights) {
        switch(from) {
            case 'A': heights[0]--; break;
            case 'B': heights[1]--; break;
            case 'C': heights[2]--; break;
        }
        switch(to) {
            case 'A': heights[0]++; break;
            case 'B': heights[1]++; break;
            case 'C': heights[2]++; break;
        }
    }

    static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n-i-1; j++) {
                if (arr[j] > arr[j+1]) {
                    int temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }
    }
}