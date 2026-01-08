package by.it.group410971.posvenchuk.lesson13;

import java.util.*;

public class GraphC {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        scanner.close();

        Map<String, List<String>> graph = parseGraph(input);
        Map<String, List<String>> reverseGraph = reverseGraph(graph);
        List<List<String>> components = kosaraju(graph, reverseGraph);

        // Выводим результат
        for (int i = 0; i < components.size(); i++) {
            List<String> component = components.get(i);
            Collections.sort(component); // Сортируем вершины внутри компонента
            for (String vertex : component) {
                System.out.print(vertex);
            }
            if (i < components.size() - 1) {
                System.out.println();
            }
        }
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

    private static Map<String, List<String>> reverseGraph(Map<String, List<String>> graph) {
        Map<String, List<String>> reversed = new HashMap<>();

        // Инициализируем все вершины
        for (String vertex : graph.keySet()) {
            reversed.put(vertex, new ArrayList<>());
        }

        // Строим обратный граф
        for (String from : graph.keySet()) {
            for (String to : graph.get(from)) {
                reversed.get(to).add(from);
            }
        }

        return reversed;
    }

    private static List<List<String>> kosaraju(Map<String, List<String>> graph,
                                               Map<String, List<String>> reverseGraph) {
        List<List<String>> components = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        List<String> order = new ArrayList<>();

        // Первый проход DFS для получения порядка выхода
        // Важно: обходим вершины в лексикографическом порядке
        List<String> vertices = new ArrayList<>(graph.keySet());
        Collections.sort(vertices);

        for (String vertex : vertices) {
            if (!visited.contains(vertex)) {
                dfs1(vertex, graph, visited, order);
            }
        }

        // Второй проход DFS в обратном порядке на обратном графе
        visited.clear();
        Collections.reverse(order);

        for (String vertex : order) {
            if (!visited.contains(vertex)) {
                List<String> component = new ArrayList<>();
                dfs2(vertex, reverseGraph, visited, component);
                components.add(component);
            }
        }

        // НЕ переворачиваем компоненты - выводим в том порядке, в котором их нашел алгоритм
        return components;
    }

    private static void dfs1(String vertex, Map<String, List<String>> graph,
                             Set<String> visited, List<String> order) {
        visited.add(vertex);

        // Получаем соседей и сортируем их для детерминированного порядка
        List<String> neighbors = new ArrayList<>(graph.get(vertex));
        Collections.sort(neighbors);

        for (String neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                dfs1(neighbor, graph, visited, order);
            }
        }

        order.add(vertex); // Добавляем вершину после обработки всех соседей
    }

    private static void dfs2(String vertex, Map<String, List<String>> graph,
                             Set<String> visited, List<String> component) {
        visited.add(vertex);
        component.add(vertex);

        // Получаем соседей и сортируем их для детерминированного порядка
        List<String> neighbors = new ArrayList<>(graph.get(vertex));
        Collections.sort(neighbors);

        for (String neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                dfs2(neighbor, graph, visited, component);
            }
        }
    }
}