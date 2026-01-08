package by.it.group410971.posvenchuk.lesson12;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.SortedMap;

public class MyRbMap implements SortedMap<Integer, String> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private static class RbNode {
        Integer key;
        String value;
        RbNode left;
        RbNode right;
        RbNode parent;
        boolean color;

        RbNode(Integer key, String value, boolean color) {
            this.key = key;
            this.value = value;
            this.color = color;
            this.left = null;
            this.right = null;
            this.parent = null;
        }
    }

    private RbNode root;
    private int size;

    public MyRbMap() {
        root = null;
        size = 0;
    }

    // Вспомогательные методы для красно-черного дерева

    private boolean isRed(RbNode node) {
        return node != null && node.color == RED;
    }

    private boolean isBlack(RbNode node) {
        return node == null || node.color == BLACK;
    }

    // Левый поворот
    private void rotateLeft(RbNode x) {
        RbNode y = x.right;
        x.right = y.left;
        if (y.left != null) {
            y.left.parent = x;
        }
        y.parent = x.parent;

        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }

        y.left = x;
        x.parent = y;
    }

    // Правый поворот
    private void rotateRight(RbNode y) {
        RbNode x = y.left;
        y.left = x.right;
        if (x.right != null) {
            x.right.parent = y;
        }
        x.parent = y.parent;

        if (y.parent == null) {
            root = x;
        } else if (y == y.parent.left) {
            y.parent.left = x;
        } else {
            y.parent.right = x;
        }

        x.right = y;
        y.parent = x;
    }

    // Исправление нарушения свойств после вставки
    private void fixAfterInsert(RbNode node) {
        while (node != root && isRed(node.parent)) {
            if (node.parent == node.parent.parent.left) {
                RbNode uncle = node.parent.parent.right;

                if (isRed(uncle)) {
                    // Случай 1: дядя красный
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right) {
                        // Случай 2: узел является правым потомком
                        node = node.parent;
                        rotateLeft(node);
                    }
                    // Случай 3: узел является левым потомком
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateRight(node.parent.parent);
                }
            } else {
                RbNode uncle = node.parent.parent.left;

                if (isRed(uncle)) {
                    // Случай 1: дядя красный
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left) {
                        // Случай 2: узел является левым потомком
                        node = node.parent;
                        rotateRight(node);
                    }
                    // Случай 3: узел является правым потомком
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateLeft(node.parent.parent);
                }
            }
        }

        root.color = BLACK;
    }

    // Поиск узла по ключу
    private RbNode getNode(Integer key) {
        RbNode current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                return current;
            }
        }
        return null;
    }

    // Поиск минимального узла в поддереве
    private RbNode findMin(RbNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Поиск максимального узла в поддереве
    private RbNode findMax(RbNode node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    // Вспомогательный метод для удаления
    private void transplant(RbNode u, RbNode v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }

        if (v != null) {
            v.parent = u.parent;
        }
    }

    // Исправление нарушения свойств после удаления
    private void fixAfterDelete(RbNode node) {
        while (node != root && isBlack(node)) {
            if (node == node.parent.left) {
                RbNode sibling = node.parent.right;

                if (isRed(sibling)) {
                    // Случай 1: брат красный
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateLeft(node.parent);
                    sibling = node.parent.right;
                }

                if (isBlack(sibling.left) && isBlack(sibling.right)) {
                    // Случай 2: оба ребенка брата черные
                    sibling.color = RED;
                    node = node.parent;
                } else {
                    if (isBlack(sibling.right)) {
                        // Случай 3: правый ребенок брата черный
                        sibling.left.color = BLACK;
                        sibling.color = RED;
                        rotateRight(sibling);
                        sibling = node.parent.right;
                    }

                    // Случай 4: правый ребенок брата красный
                    sibling.color = node.parent.color;
                    node.parent.color = BLACK;
                    sibling.right.color = BLACK;
                    rotateLeft(node.parent);
                    node = root;
                }
            } else {
                RbNode sibling = node.parent.left;

                if (isRed(sibling)) {
                    // Случай 1: брат красный
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateRight(node.parent);
                    sibling = node.parent.left;
                }

                if (isBlack(sibling.left) && isBlack(sibling.right)) {
                    // Случай 2: оба ребенка брата черные
                    sibling.color = RED;
                    node = node.parent;
                } else {
                    if (isBlack(sibling.left)) {
                        // Случай 3: левый ребенок брата черный
                        sibling.right.color = BLACK;
                        sibling.color = RED;
                        rotateLeft(sibling);
                        sibling = node.parent.left;
                    }

                    // Случай 4: левый ребенок брата красный
                    sibling.color = node.parent.color;
                    node.parent.color = BLACK;
                    sibling.left.color = BLACK;
                    rotateRight(node.parent);
                    node = root;
                }
            }
        }

        if (node != null) {
            node.color = BLACK;
        }
    }

    // Вспомогательный метод для toString (симметричный обход)
    private void inOrderToString(RbNode node, StringBuilder sb) {
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

        RbNode parent = null;
        RbNode current = root;

        // Поиск места для вставки
        while (current != null) {
            parent = current;
            int cmp = key.compareTo(current.key);

            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                // Ключ уже существует, обновляем значение
                String oldValue = current.value;
                current.value = value;
                return oldValue;
            }
        }

        // Создаем новый узел (красный по умолчанию)
        RbNode newNode = new RbNode(key, value, RED);
        newNode.parent = parent;

        // Вставляем узел
        if (parent == null) {
            root = newNode;
        } else if (key.compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        // Исправляем возможные нарушения свойств красно-черного дерева
        fixAfterInsert(newNode);
        size++;

        return null;
    }

    @Override
    public String remove(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        if (!(key instanceof Integer)) {
            return null;
        }

        RbNode node = getNode((Integer) key);
        if (node == null) {
            return null;
        }

        String oldValue = node.value;
        deleteNode(node);
        size--;
        return oldValue;
    }

    private void deleteNode(RbNode node) {
        RbNode y = node;
        RbNode x;
        boolean yOriginalColor = y.color;

        if (node.left == null) {
            x = node.right;
            transplant(node, node.right);
        } else if (node.right == null) {
            x = node.left;
            transplant(node, node.left);
        } else {
            y = findMin(node.right);
            yOriginalColor = y.color;
            x = y.right;

            if (y.parent == node) {
                if (x != null) {
                    x.parent = y;
                }
            } else {
                transplant(y, y.right);
                y.right = node.right;
                y.right.parent = y;
            }

            transplant(node, y);
            y.left = node.left;
            y.left.parent = y;
            y.color = node.color;
        }

        if (yOriginalColor == BLACK && x != null) {
            fixAfterDelete(x);
        }
    }

    @Override
    public String get(Object key) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }

        if (!(key instanceof Integer)) {
            return null;
        }

        RbNode node = getNode((Integer) key);
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

        return getNode((Integer) key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValue(root, value);
    }

    private boolean containsValue(RbNode node, Object value) {
        if (node == null) {
            return false;
        }

        if (value == null ? node.value == null : value.equals(node.value)) {
            return true;
        }

        return containsValue(node.left, value) || containsValue(node.right, value);
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

    @Override
    public Integer firstKey() {
        if (root == null) {
            throw new NoSuchElementException("Map is empty");
        }

        RbNode node = findMin(root);
        return node.key;
    }

    @Override
    public Integer lastKey() {
        if (root == null) {
            throw new NoSuchElementException("Map is empty");
        }

        RbNode node = findMax(root);
        return node.key;
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        if (toKey == null) {
            throw new NullPointerException("Key cannot be null");
        }

        MyRbMap result = new MyRbMap();
        collectHeadMap(root, toKey, result);
        return result;
    }

    private void collectHeadMap(RbNode node, Integer toKey, MyRbMap result) {
        if (node != null) {
            collectHeadMap(node.left, toKey, result);

            if (node.key.compareTo(toKey) < 0) {
                result.put(node.key, node.value);
                collectHeadMap(node.right, toKey, result);
            }
        }
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        if (fromKey == null) {
            throw new NullPointerException("Key cannot be null");
        }

        MyRbMap result = new MyRbMap();
        collectTailMap(root, fromKey, result);
        return result;
    }

    private void collectTailMap(RbNode node, Integer fromKey, MyRbMap result) {
        if (node != null) {
            collectTailMap(node.right, fromKey, result);

            if (node.key.compareTo(fromKey) >= 0) {
                result.put(node.key, node.value);
                collectTailMap(node.left, fromKey, result);
            }
        }
    }

    // Остальные методы интерфейса SortedMap и Map (опциональные)

    @Override
    public Comparator<? super Integer> comparator() {
        return null; // Используется естественный порядок
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        if (fromKey == null || toKey == null) {
            throw new NullPointerException("Keys cannot be null");
        }

        if (fromKey.compareTo(toKey) > 0) {
            throw new IllegalArgumentException("fromKey > toKey");
        }

        MyRbMap result = new MyRbMap();
        collectSubMap(root, fromKey, toKey, result);
        return result;
    }

    private void collectSubMap(RbNode node, Integer fromKey, Integer toKey, MyRbMap result) {
        if (node != null) {
            if (node.key.compareTo(fromKey) >= 0 && node.key.compareTo(toKey) < 0) {
                collectSubMap(node.left, fromKey, toKey, result);
                result.put(node.key, node.value);
                collectSubMap(node.right, fromKey, toKey, result);
            } else if (node.key.compareTo(fromKey) < 0) {
                collectSubMap(node.right, fromKey, toKey, result);
            } else {
                collectSubMap(node.left, fromKey, toKey, result);
            }
        }
    }

    @Override
    public void putAll(java.util.Map<? extends Integer, ? extends String> m) {
        if (m == null) {
            throw new NullPointerException("Map cannot be null");
        }

        for (java.util.Map.Entry<? extends Integer, ? extends String> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public java.util.Set<Integer> keySet() {
        java.util.Set<Integer> keys = new java.util.HashSet<>();
        collectKeys(root, keys);
        return keys;
    }

    private void collectKeys(RbNode node, java.util.Set<Integer> keys) {
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

    private void collectValues(RbNode node, java.util.Collection<String> values) {
        if (node != null) {
            collectValues(node.left, values);
            values.add(node.value);
            collectValues(node.right, values);
        }
    }

    @Override
    public java.util.Set<java.util.Map.Entry<Integer, String>> entrySet() {
        java.util.Set<java.util.Map.Entry<Integer, String>> entries = new java.util.HashSet<>();
        collectEntries(root, entries);
        return entries;
    }

    private void collectEntries(RbNode node, java.util.Set<java.util.Map.Entry<Integer, String>> entries) {
        if (node != null) {
            collectEntries(node.left, entries);
            entries.add(new java.util.AbstractMap.SimpleEntry<>(node.key, node.value));
            collectEntries(node.right, entries);
        }
    }
}