package by.it.group410971.posvenchuk.lesson12;

import java.util.*;

public class MySplayMap implements NavigableMap<Integer, String> {

    private static class Node {
        Integer key;
        String value;
        Node left;
        Node right;
        Node parent;

        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            this.parent = null;
        }
    }

    private Node root;
    private int size;

    public MySplayMap() {
        root = null;
        size = 0;
    }

    // Основные операции splay-дерева
    private void rotateLeft(Node x) {
        Node y = x.right;
        if (y != null) {
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
    }

    private void rotateRight(Node x) {
        Node y = x.left;
        if (y != null) {
            x.left = y.right;
            if (y.right != null) {
                y.right.parent = x;
            }
            y.parent = x.parent;
            if (x.parent == null) {
                root = y;
            } else if (x == x.parent.right) {
                x.parent.right = y;
            } else {
                x.parent.left = y;
            }
            y.right = x;
            x.parent = y;
        }
    }

    private void splay(Node x) {
        while (x.parent != null) {
            if (x.parent.parent == null) {
                if (x.parent.left == x) {
                    rotateRight(x.parent);
                } else {
                    rotateLeft(x.parent);
                }
            } else if (x.parent.left == x && x.parent.parent.left == x.parent) {
                rotateRight(x.parent.parent);
                rotateRight(x.parent);
            } else if (x.parent.right == x && x.parent.parent.right == x.parent) {
                rotateLeft(x.parent.parent);
                rotateLeft(x.parent);
            } else if (x.parent.left == x && x.parent.parent.right == x.parent) {
                rotateRight(x.parent);
                rotateLeft(x.parent);
            } else {
                rotateLeft(x.parent);
                rotateRight(x.parent);
            }
        }
    }

    private Node findNode(Integer key) {
        Node current = root;
        Node last = null;
        while (current != null) {
            last = current;
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                splay(current);
                return current;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        if (last != null) {
            splay(last);
        }
        return null;
    }

    private Node findClosest(Integer key, boolean less, boolean inclusive) {
        Node current = root;
        Node result = null;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0 && inclusive) {
                result = current;
                break;
            } else if (less) {
                if (cmp > 0) {
                    result = current;
                    current = current.right;
                } else {
                    current = current.left;
                }
            } else {
                if (cmp < 0) {
                    result = current;
                    current = current.left;
                } else {
                    current = current.right;
                }
            }
        }
        if (result != null) {
            splay(result);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        List<Map.Entry<Integer, String>> entries = new ArrayList<>(entrySet());
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<Integer, String> entry = entries.get(i);
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            if (i < entries.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String put(Integer key, String value) {
        if (root == null) {
            root = new Node(key, value);
            size = 1;
            return null;
        }

        Node current = root;
        Node parent = null;
        while (current != null) {
            parent = current;
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                String oldValue = current.value;
                current.value = value;
                splay(current);
                return oldValue;
            } else if (cmp < 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        Node newNode = new Node(key, value);
        newNode.parent = parent;
        if (key.compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }
        splay(newNode);
        size++;
        return null;
    }

    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) return null;
        Integer k = (Integer) key;

        Node node = findNode(k);
        if (node == null) return null;

        String removedValue = node.value;

        if (node.left == null) {
            root = node.right;
            if (root != null) root.parent = null;
        } else if (node.right == null) {
            root = node.left;
            if (root != null) root.parent = null;
        } else {
            Node leftMax = node.left;
            while (leftMax.right != null) {
                leftMax = leftMax.right;
            }
            leftMax.right = node.right;
            if (node.right != null) {
                node.right.parent = leftMax;
            }
            root = node.left;
            if (root != null) root.parent = null;
        }

        size--;
        return removedValue;
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) return null;
        Node node = findNode((Integer) key);
        return node != null ? node.value : null;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof String)) return false;
        String val = (String) value;
        for (String v : values()) {
            if (val.equals(v)) {
                return true;
            }
        }
        return false;
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
        if (root == null) return null;
        Node current = root;
        while (current.left != null) {
            current = current.left;
        }
        splay(current);
        return current.key;
    }

    @Override
    public Integer lastKey() {
        if (root == null) return null;
        Node current = root;
        while (current.right != null) {
            current = current.right;
        }
        splay(current);
        return current.key;
    }

    @Override
    public Integer lowerKey(Integer key) {
        Node node = findClosest(key, true, false);
        return node != null ? node.key : null;
    }

    @Override
    public Integer floorKey(Integer key) {
        Node node = findClosest(key, true, true);
        return node != null ? node.key : null;
    }

    @Override
    public Integer ceilingKey(Integer key) {
        Node node = findClosest(key, false, true);
        return node != null ? node.key : null;
    }

    @Override
    public Integer higherKey(Integer key) {
        Node node = findClosest(key, false, false);
        return node != null ? node.key : null;
    }

    // Методы для работы с поддеревьями
    @Override
    public NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) {
        return new SubMap(null, toKey, false, inclusive, false, true);
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        return headMap(toKey, false);
    }

    @Override
    public NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) {
        return new SubMap(fromKey, null, inclusive, false, true, false);
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        return tailMap(fromKey, true);
    }

    @Override
    public NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive,
                                                Integer toKey, boolean toInclusive) {
        return new SubMap(fromKey, toKey, fromInclusive, toInclusive, true, true);
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        return subMap(fromKey, true, toKey, false);
    }

    // Вспомогательный класс для поддеревьев - УПРОЩЕННАЯ ВЕРСИЯ
    private class SubMap extends AbstractMap<Integer, String>
            implements NavigableMap<Integer, String> {
        private final Integer fromKey;
        private final Integer toKey;
        private final boolean fromInclusive;
        private final boolean toInclusive;
        private final boolean hasLowerBound;
        private final boolean hasUpperBound;

        SubMap(Integer fromKey, Integer toKey,
               boolean fromInclusive, boolean toInclusive,
               boolean hasLowerBound, boolean hasUpperBound) {
            this.fromKey = fromKey;
            this.toKey = toKey;
            this.fromInclusive = fromInclusive;
            this.toInclusive = toInclusive;
            this.hasLowerBound = hasLowerBound;
            this.hasUpperBound = hasUpperBound;
        }

        private boolean inRange(Integer key) {
            if (key == null) return false;
            if (hasLowerBound) {
                int cmp = key.compareTo(fromKey);
                if (cmp < 0 || (cmp == 0 && !fromInclusive)) {
                    return false;
                }
            }
            if (hasUpperBound) {
                int cmp = key.compareTo(toKey);
                if (cmp > 0 || (cmp == 0 && !toInclusive)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public Set<Entry<Integer, String>> entrySet() {
            Set<Entry<Integer, String>> result = new TreeSet<>(Comparator.comparing(Entry::getKey));
            for (Entry<Integer, String> entry : MySplayMap.this.entrySet()) {
                if (inRange(entry.getKey())) {
                    result.add(entry);
                }
            }
            return result;
        }

        @Override
        public String put(Integer key, String value) {
            if (!inRange(key)) {
                throw new IllegalArgumentException("Key out of range");
            }
            return MySplayMap.this.put(key, value);
        }

        @Override
        public String get(Object key) {
            if (!(key instanceof Integer)) return null;
            return inRange((Integer) key) ? MySplayMap.this.get(key) : null;
        }

        @Override
        public boolean containsKey(Object key) {
            if (!(key instanceof Integer)) return false;
            return inRange((Integer) key) && MySplayMap.this.containsKey(key);
        }

        @Override
        public Integer lowerKey(Integer key) {
            // Простая реализация для тестов
            Integer result = null;
            for (Integer k : keySet()) {
                if (k.compareTo(key) < 0) {
                    if (result == null || k.compareTo(result) > 0) {
                        result = k;
                    }
                }
            }
            return result;
        }

        @Override
        public Integer floorKey(Integer key) {
            Integer result = null;
            for (Integer k : keySet()) {
                if (k.compareTo(key) <= 0) {
                    if (result == null || k.compareTo(result) > 0) {
                        result = k;
                    }
                }
            }
            return result;
        }

        @Override
        public Integer ceilingKey(Integer key) {
            for (Integer k : keySet()) {
                if (k.compareTo(key) >= 0) {
                    return k;
                }
            }
            return null;
        }

        @Override
        public Integer higherKey(Integer key) {
            for (Integer k : keySet()) {
                if (k.compareTo(key) > 0) {
                    return k;
                }
            }
            return null;
        }

        @Override
        public Entry<Integer, String> lowerEntry(Integer key) {
            Integer k = lowerKey(key);
            return k != null ? new SimpleEntry<>(k, get(k)) : null;
        }

        @Override
        public Entry<Integer, String> floorEntry(Integer key) {
            Integer k = floorKey(key);
            return k != null ? new SimpleEntry<>(k, get(k)) : null;
        }

        @Override
        public Entry<Integer, String> ceilingEntry(Integer key) {
            Integer k = ceilingKey(key);
            return k != null ? new SimpleEntry<>(k, get(k)) : null;
        }

        @Override
        public Entry<Integer, String> higherEntry(Integer key) {
            Integer k = higherKey(key);
            return k != null ? new SimpleEntry<>(k, get(k)) : null;
        }

        @Override
        public Entry<Integer, String> firstEntry() {
            Integer k = firstKey();
            return k != null ? new SimpleEntry<>(k, get(k)) : null;
        }

        @Override
        public Entry<Integer, String> lastEntry() {
            Integer k = lastKey();
            return k != null ? new SimpleEntry<>(k, get(k)) : null;
        }

        @Override
        public Entry<Integer, String> pollFirstEntry() {
            Entry<Integer, String> entry = firstEntry();
            if (entry != null) {
                remove(entry.getKey());
            }
            return entry;
        }

        @Override
        public Entry<Integer, String> pollLastEntry() {
            Entry<Integer, String> entry = lastEntry();
            if (entry != null) {
                remove(entry.getKey());
            }
            return entry;
        }

        @Override
        public NavigableMap<Integer, String> descendingMap() {
            throw new UnsupportedOperationException();
        }

        @Override
        public NavigableSet<Integer> navigableKeySet() {
            return new TreeSet<>(keySet());
        }

        @Override
        public NavigableSet<Integer> descendingKeySet() {
            NavigableSet<Integer> set = new TreeSet<>(Collections.reverseOrder());
            set.addAll(keySet());
            return set;
        }

        @Override
        public NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) {
            if (!hasLowerBound) {
                return MySplayMap.this.headMap(toKey, inclusive);
            }
            Integer newToKey = toKey;
            if (hasUpperBound && toKey.compareTo(this.toKey) > 0) {
                newToKey = this.toKey;
            }
            return new SubMap(fromKey, newToKey, fromInclusive, inclusive, true, true);
        }

        @Override
        public SortedMap<Integer, String> headMap(Integer toKey) {
            return headMap(toKey, false);
        }

        @Override
        public NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) {
            if (!hasUpperBound) {
                return MySplayMap.this.tailMap(fromKey, inclusive);
            }
            Integer newFromKey = fromKey;
            if (hasLowerBound && fromKey.compareTo(this.fromKey) < 0) {
                newFromKey = this.fromKey;
            }
            return new SubMap(newFromKey, toKey, inclusive, toInclusive, true, true);
        }

        @Override
        public SortedMap<Integer, String> tailMap(Integer fromKey) {
            return tailMap(fromKey, true);
        }

        @Override
        public NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive,
                                                    Integer toKey, boolean toInclusive) {
            return new SubMap(fromKey, toKey, fromInclusive, toInclusive, true, true);
        }

        @Override
        public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
            return subMap(fromKey, true, toKey, false);
        }

        @Override
        public Comparator<? super Integer> comparator() {
            return null; // естественный порядок
        }

        @Override
        public Integer firstKey() {
            for (Integer k : keySet()) {
                return k;
            }
            return null;
        }

        @Override
        public Integer lastKey() {
            Integer last = null;
            for (Integer k : keySet()) {
                last = k;
            }
            return last;
        }

        @Override
        public Set<Integer> keySet() {
            Set<Integer> result = new TreeSet<>();
            for (Entry<Integer, String> entry : entrySet()) {
                result.add(entry.getKey());
            }
            return result;
        }

        @Override
        public Collection<String> values() {
            List<String> result = new ArrayList<>();
            for (Entry<Integer, String> entry : entrySet()) {
                result.add(entry.getValue());
            }
            return result;
        }

        @Override
        public int size() {
            int count = 0;
            for (Entry<Integer, String> entry : entrySet()) {
                count++;
            }
            return count;
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public boolean containsValue(Object value) {
            return values().contains(value);
        }

        @Override
        public String remove(Object key) {
            if (!(key instanceof Integer)) return null;
            if (!inRange((Integer) key)) return null;
            return MySplayMap.this.remove(key);
        }

        @Override
        public void putAll(Map<? extends Integer, ? extends String> m) {
            for (Entry<? extends Integer, ? extends String> entry : m.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public void clear() {
            Set<Integer> keysToRemove = new HashSet<>(keySet());
            for (Integer key : keysToRemove) {
                remove(key);
            }
        }
    }

    // Вспомогательный класс для Entry
    private static class SimpleEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        SimpleEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>) o;
            return Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    // Остальные методы интерфейса
    @Override
    public Entry<Integer, String> lowerEntry(Integer key) {
        Integer k = lowerKey(key);
        return k != null ? new SimpleEntry<>(k, get(k)) : null;
    }

    @Override
    public Entry<Integer, String> floorEntry(Integer key) {
        Integer k = floorKey(key);
        return k != null ? new SimpleEntry<>(k, get(k)) : null;
    }

    @Override
    public Entry<Integer, String> ceilingEntry(Integer key) {
        Integer k = ceilingKey(key);
        return k != null ? new SimpleEntry<>(k, get(k)) : null;
    }

    @Override
    public Entry<Integer, String> higherEntry(Integer key) {
        Integer k = higherKey(key);
        return k != null ? new SimpleEntry<>(k, get(k)) : null;
    }

    @Override
    public Entry<Integer, String> firstEntry() {
        Integer k = firstKey();
        return k != null ? new SimpleEntry<>(k, get(k)) : null;
    }

    @Override
    public Entry<Integer, String> lastEntry() {
        Integer k = lastKey();
        return k != null ? new SimpleEntry<>(k, get(k)) : null;
    }

    @Override
    public Entry<Integer, String> pollFirstEntry() {
        Entry<Integer, String> entry = firstEntry();
        if (entry != null) {
            remove(entry.getKey());
        }
        return entry;
    }

    @Override
    public Entry<Integer, String> pollLastEntry() {
        Entry<Integer, String> entry = lastEntry();
        if (entry != null) {
            remove(entry.getKey());
        }
        return entry;
    }

    @Override
    public NavigableMap<Integer, String> descendingMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<Integer> navigableKeySet() {
        return new TreeSet<>(keySet());
    }

    @Override
    public NavigableSet<Integer> descendingKeySet() {
        NavigableSet<Integer> set = new TreeSet<>(Collections.reverseOrder());
        set.addAll(keySet());
        return set;
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    @Override
    public Set<Entry<Integer, String>> entrySet() {
        Set<Entry<Integer, String>> result = new TreeSet<>(Comparator.comparing(Entry::getKey));
        inOrderTraversal(root, result);
        return result;
    }

    private void inOrderTraversal(Node node, Set<Entry<Integer, String>> result) {
        if (node != null) {
            inOrderTraversal(node.left, result);
            result.add(new SimpleEntry<>(node.key, node.value));
            inOrderTraversal(node.right, result);
        }
    }

    @Override
    public Set<Integer> keySet() {
        Set<Integer> result = new TreeSet<>();
        keySetTraversal(root, result);
        return result;
    }

    private void keySetTraversal(Node node, Set<Integer> result) {
        if (node != null) {
            keySetTraversal(node.left, result);
            result.add(node.key);
            keySetTraversal(node.right, result);
        }
    }

    @Override
    public Collection<String> values() {
        List<String> result = new ArrayList<>();
        valuesTraversal(root, result);
        return result;
    }

    private void valuesTraversal(Node node, List<String> result) {
        if (node != null) {
            valuesTraversal(node.left, result);
            result.add(node.value);
            valuesTraversal(node.right, result);
        }
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        for (Entry<? extends Integer, ? extends String> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
}