package by.it.group410971.posvenchuk.lesson13;

import java.util.*;

public class GraphB {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        scanner.close();

        Map<String, List<String>> graph = parseGraph(input);
        boolean hasCycle = hasCycle(graph);

        System.out.print(hasCycle ? "yes" : "no");
    }

    private static Map<String, List<String>> parseGraph(String input) {
        Map<String, List<String>> graph = new HashMap<>();

        // Обрабатываем входную строку
        input = input.replaceAll("\\s+", "");
        if (input.isEmpty()) {
            return graph;
        }

        String[] edges = input.split(",");

        for (String edge : edges) {
            if (edge.isEmpty()) continue;

            String[] vertices = edge.split("->");
            String from = vertices[0];
            String to = vertices[1];

            // Добавляем ребро в граф
            if (!graph.containsKey(from)) {
                graph.put(from, new ArrayList<>());
            }
            if (!graph.containsKey(to)) {
                graph.put(to, new ArrayList<>());
            }
            graph.get(from).add(to);
        }

        return graph;
    }

    private static boolean hasCycle(Map<String, List<String>> graph) {
        // Используем алгоритм с раскраской вершин (DFS)
        Map<String, Integer> colors = new HashMap<>();

        // Инициализация цветов: 0 - белый (не посещён), 1 - серый (в обработке), 2 - чёрный (обработан)
        for (String vertex : graph.keySet()) {
            colors.put(vertex, 0);
        }

        // Запускаем DFS из каждой вершины
        for (String vertex : graph.keySet()) {
            if (colors.get(vertex) == 0) { // Белая вершина
                if (dfsHasCycle(vertex, graph, colors)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean dfsHasCycle(String current, Map<String, List<String>> graph,
                                       Map<String, Integer> colors) {
        colors.put(current, 1); // Серый цвет - вершина в обработке

        // Проверяем всех соседей
        for (String neighbor : graph.get(current)) {
            if (colors.get(neighbor) == 0) { // Белый сосед
                if (dfsHasCycle(neighbor, graph, colors)) {
                    return true;
                }
            } else if (colors.get(neighbor) == 1) { // Серый сосед - найден цикл!
                return true;
            }
            // Чёрный сосед - пропускаем
        }

        colors.put(current, 2); // Чёрный цвет - вершина обработана
        return false;
    }
}