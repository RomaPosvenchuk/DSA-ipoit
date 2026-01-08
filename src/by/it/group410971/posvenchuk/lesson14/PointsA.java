package by.it.group410971.posvenchuk.lesson14;

import java.util.*;

public class PointsA {

    static class DSU {
        private int[] parent;
        private int[] rank;
        private int[] size; // Добавляем массив размеров

        public DSU(int n) {
            parent = new int[n];
            rank = new int[n];
            size = new int[n]; // Инициализируем массив размеров
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 1;
                size[i] = 1; // Каждый кластер изначально имеет размер 1
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // Path compression
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                // Union by rank
                if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                    size[rootY] += size[rootX]; // Обновляем размер
                } else if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                    size[rootX] += size[rootY]; // Обновляем размер
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                    size[rootX] += size[rootY]; // Обновляем размер
                }
            }
        }

        public int getSize(int x) {
            return size[find(x)];
        }
    }

    static class Point {
        double x, y, z;
        int index;

        public Point(double x, double y, double z, int index) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.index = index;
        }

        public double distanceTo(Point other) {
            double dx = x - other.x;
            double dy = y - other.y;
            double dz = z - other.z;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Читаем допустимое расстояние и количество точек
        double maxDistance = scanner.nextDouble();
        int n = scanner.nextInt();

        // Читаем точки
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            double x = scanner.nextDouble();
            double y = scanner.nextDouble();
            double z = scanner.nextDouble();
            points[i] = new Point(x, y, z, i);
        }
        scanner.close();

        // Инициализируем DSU
        DSU dsu = new DSU(n);

        // Объединяем точки, которые находятся на допустимом расстоянии
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double distance = points[i].distanceTo(points[j]);
                if (distance < maxDistance) { // [0, D) - не включая D
                    dsu.union(i, j);
                }
            }
        }

        // Собираем размеры уникальных кластеров (только корневых элементов)
        List<Integer> clusterSizes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (dsu.find(i) == i) { // Если это корень кластера
                clusterSizes.add(dsu.getSize(i));
            }
        }

        // Сортируем в порядке убывания
        clusterSizes.sort(Collections.reverseOrder());

        // Выводим результат
        for (int i = 0; i < clusterSizes.size(); i++) {
            System.out.print(clusterSizes.get(i));
            if (i < clusterSizes.size() - 1) {
                System.out.print(" ");
            }
        }
    }
}