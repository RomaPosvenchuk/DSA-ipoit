package by.it.group410971.posvenchuk.lesson12;

import java.util.Map;
import java.util.NoSuchElementException;

public class MyAvlMap implements Map<Integer, String> {

    private static class AvlNode {
        Integer key;
        String value;
        AvlNode left;
        AvlNode right;
        int height;

        AvlNode(Integer key, String value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            this.height = 1;
        }
    }

    private AvlNode root;
    private int size;

    public MyAvlMap() {
        root = null;
        size = 0;
    }

    // Вспомогательные методы для AVL дерева

    private int height(AvlNode node) {
        return node == null ? 0 : node.height;
    }

    private int balanceFactor(AvlNode node) {
        if (node == null) return 0;
        return height(node.left) - height(node.right);
    }

    private void updateHeight(AvlNode node) {
        if (node != null) {
            node.height = Math.max(height(node.left), height(node.right)) + 1;
        }
    }

    // Правый поворот
    private AvlNode rotateRight(AvlNode y) {
        AvlNode x = y.left;
        AvlNode T2 = x.right;

        x.right = y;
        y.left = T2;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    // Левый поворот
    private AvlNode rotateLeft(AvlNode x) {
        AvlNode y = x.right;
        AvlNode T2 = y.left;

        y.left = x;
        x.right = T2;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    // Балансировка узла
    private AvlNode balance(AvlNode node) {
        updateHeight(node);

        int bf = balanceFactor(node);

        // Левый левый случай
        if (bf > 1 && balanceFactor(node.left) >= 0) {
            return rotateRight(node);
        }

        // Левый правый случай
        if (bf > 1 && balanceFactor(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Правый правый случай
        if (bf < -1 && balanceFactor(node.right) <= 0) {
            return rotateLeft(node);
        }

        // Правый левый случай
        if (bf < -1 && balanceFactor(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // Поиск минимального узла в поддереве
    private AvlNode findMin(AvlNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Вспомогательный метод для вставки
    private AvlNode put(AvlNode node, Integer key, String value) {
        if (node == null) {
            size++;
            return new AvlNode(key, value);
        }

        if (key < node.key) {
            node.left = put(node.left, key, value);
        } else if (key > node.key) {
            node.right = put(node.right, key, value);
        } else {
            // Ключ уже существует, обновляем значение
            node.value = value;
            return node;
        }

        return balance(node);
    }

    // Вспомогательный метод для удаления
    private AvlNode remove(AvlNode node, Integer key) {
        if (node == null) {
            return null;
        }

        if (key < node.key) {
            node.left = remove(node.left, key);
        } else if (key > node.key) {
            node.right = remove(node.right, key);
        } else {
            // Нашли узел для удаления
            size--;

            // Узел с одним или без детей
            if (node.left == null || node.right == null) {
                AvlNode temp = (node.left != null) ? node.left : node.right;

                // Нет детей
                if (temp == null) {
                    return null;
                } else {
                    // Один ребенок
                    return temp;
                }
            } else {
                // Узел с двумя детьми
                AvlNode temp = findMin(node.right);
                node.key = temp.key;
                node.value = temp.value;
                node.right = remove(node.right, temp.key);
            }
        }

        return balance(node);
    }

    // Вспомогательный метод для поиска
    private AvlNode getNode(AvlNode node, Integer key) {
        if (node == null) {
            return null;
        }

        if (key < node.key) {
            return getNode(node.left, key);
        } else if (key > node.key) {
            return getNode(node.right, key);
        } else {
            return node;
        }
    }

    // Вспомогательный метод для toString (симметричный обход)
    private void inOrderToString(AvlNode node, StringBuilder sb) {
        if (node != null) {
            inOrderToString(node.left, sb);

            if (sb.length() > 1) { // Уже есть хотя бы одна пара
                sb.append(", ");
            }
            sb.append(node.key).append("=").append(node.value);

            inOrderToString(node.right, sb);
        }
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        inOrderToString(root, sb);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        AvlNode existingNode = getNode(root, key);
        String oldValue = (existingNode != null) ? existingNode.value : null;

        root = put(root, key, value);
        return oldValue;
    }

    @Override
    public String remove(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        if (!(key instanceof Integer)) {
            return null;
        }

        AvlNode node = getNode(root, (Integer) key);
        if (node == null) {
            return null;
        }

        String oldValue = node.value;
        root = remove(root, (Integer) key);
        return oldValue;
    }

    @Override
    public String get(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        if (!(key instanceof Integer)) {
            return null;
        }

        AvlNode node = getNode(root, (Integer) key);
        return (node != null) ? node.value : null;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        if (!(key instanceof Integer)) {
            return false;
        }

        return getNode(root, (Integer) key) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Остальные методы интерфейса Map (опциональные)

    @Override
    public boolean containsValue(Object value) {
        return containsValue(root, value);
    }

    private boolean containsValue(AvlNode node, Object value) {
        if (node == null) {
            return false;
        }

        if (value == null ? node.value == null : value.equals(node.value)) {
            return true;
        }

        return containsValue(node.left, value) || containsValue(node.right, value);
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        if (m == null) {
            throw new NullPointerException("Map cannot be null");
        }

        for (Map.Entry<? extends Integer, ? extends String> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public java.util.Set<Integer> keySet() {
        java.util.Set<Integer> keys = new java.util.HashSet<>();
        collectKeys(root, keys);
        return keys;
    }

    private void collectKeys(AvlNode node, java.util.Set<Integer> keys) {
        if (node != null) {
            collectKeys(node.left, keys);
            keys.add(node.key);
            collectKeys(node.right, keys);
        }
    }

    @Override
    public java.util.Collection<String> values() {
        java.util.Collection<String> values = new java.util.ArrayList<>();
        collectValues(root, values);
        return values;
    }

    private void collectValues(AvlNode node, java.util.Collection<String> values) {
        if (node != null) {
            collectValues(node.left, values);
            values.add(node.value);
            collectValues(node.right, values);
        }
    }

    @Override
    public java.util.Set<Entry<Integer, String>> entrySet() {
        java.util.Set<Entry<Integer, String>> entries = new java.util.HashSet<>();
        collectEntries(root, entries);
        return entries;
    }

    private void collectEntries(AvlNode node, java.util.Set<Entry<Integer, String>> entries) {
        if (node != null) {
            collectEntries(node.left, entries);
            entries.add(new SimpleEntry(node.key, node.value));
            collectEntries(node.right, entries);
        }
    }

    // Вспомогательный класс для Entry
    private static class SimpleEntry implements Entry<Integer, String> {
        private Integer key;
        private String value;

        SimpleEntry(Integer key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Integer getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            String oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) return false;
            Entry<?, ?> e = (Entry<?, ?>) o;
            return key.equals(e.getKey()) && value.equals(e.getValue());
        }

        @Override
        public int hashCode() {
            return key.hashCode() ^ value.hashCode();
        }
    }
}