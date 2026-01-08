package by.it.group410971.posvenchuk.lesson14;

import java.util.*;

public class SitesB {

    static class DSU {
        private Map<String, String> parent;
        private Map<String, Integer> rank;

        public DSU() {
            parent = new HashMap<>();
            rank = new HashMap<>();
        }

        public void makeSet(String site) {
            if (!parent.containsKey(site)) {
                parent.put(site, site);
                rank.put(site, 0);
            }
        }

        public String find(String site) {
            if (!parent.get(site).equals(site)) {
                parent.put(site, find(parent.get(site)));
            }
            return parent.get(site);
        }

        public void union(String site1, String site2) {
            String root1 = find(site1);
            String root2 = find(site2);

            if (!root1.equals(root2)) {
                if (rank.get(root1) < rank.get(root2)) {
                    parent.put(root1, root2);
                } else if (rank.get(root1) > rank.get(root2)) {
                    parent.put(root2, root1);
                } else {
                    parent.put(root2, root1);
                    rank.put(root1, rank.get(root1) + 1);
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DSU dsu = new DSU();
        List<String> allSites = new ArrayList<>();

        // Читаем пары сайтов до строки "end"
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("end")) {
                break;
            }

            String[] sites = line.split("\\+");
            String site1 = sites[0].trim();
            String site2 = sites[1].trim();

            dsu.makeSet(site1);
            dsu.makeSet(site2);
            dsu.union(site1, site2);

            // Сохраняем все сайты для дальнейшей обработки
            if (!allSites.contains(site1)) allSites.add(site1);
            if (!allSites.contains(site2)) allSites.add(site2);
        }
        scanner.close();

        // Собираем размеры кластеров
        Map<String, Integer> clusterSizesMap = new HashMap<>();
        for (String site : allSites) {
            String root = dsu.find(site);
            clusterSizesMap.put(root, clusterSizesMap.getOrDefault(root, 0) + 1);
        }

        // Извлекаем размеры и сортируем
        List<Integer> clusterSizes = new ArrayList<>(clusterSizesMap.values());
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