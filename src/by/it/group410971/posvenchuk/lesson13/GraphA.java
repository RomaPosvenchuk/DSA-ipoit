package by.it.group410971.posvenchuk.lesson13;

import java.util.*;

public class GraphA {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        scanner.close();

        Map<String, List<String>> graph = parseGraph(input);
        List<String> sorted = topologicalSort(graph);

        // Вывод результата с пробелами между вершинами
        for (int i = 0; i < sorted.size(); i++) {
            System.out.print(sorted.get(i));
            if (i < sorted.size() - 1) {
                System.out.print(" ");
            }
        }
        // Не добавляем лишний перевод строки
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

    private static List<String> topologicalSort(Map<String, List<String>> graph) {
        List<String> result = new ArrayList<>();

        // Если граф пустой
        if (graph.isEmpty()) {
            return result;
        }

        Map<String, Integer> inDegree = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>();

        // Инициализация степеней входа
        for (String vertex : graph.keySet()) {
            inDegree.put(vertex, 0);
        }

        // Вычисление степеней входа
        for (String vertex : graph.keySet()) {
            for (String neighbor : graph.get(vertex)) {
                inDegree.put(neighbor, inDegree.get(neighbor) + 1);
            }
        }

        // Добавляем вершины с нулевой степенью входа
        for (String vertex : inDegree.keySet()) {
            if (inDegree.get(vertex) == 0) {
                queue.add(vertex);
            }
        }

        // Алгоритм Кана
        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);

            // Уменьшаем степень входа соседей
            for (String neighbor : graph.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        return result;
    }
}