package by.it.group410971.posvenchuk.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyHashSet<E> implements Set<E> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private static class Node<E> {
        E data;
        Node<E> next;
        int hash;

        Node(E data, int hash, Node<E> next) {
            this.data = data;
            this.hash = hash;
            this.next = next;
        }
    }

    private Node<E>[] table;
    private int size;
    private int threshold;

    @SuppressWarnings("unchecked")
    public MyHashSet() {
        this.table = (Node<E>[]) new Node[DEFAULT_CAPACITY];
        this.size = 0;
        this.threshold = (int)(DEFAULT_CAPACITY * LOAD_FACTOR);
    }

    // Вычисление хеш-кода
    private int hash(Object key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        return h ^ (h >>> 16); // Распределение старших битов
    }

    // Получение индекса в таблице
    private int indexFor(int hash, int length) {
        return (hash & 0x7FFFFFFF) % length;
    }

    // Увеличение таблицы
    @SuppressWarnings("unchecked")
    private void resize() {
        int oldCapacity = table.length;
        int newCapacity = oldCapacity * 2;
        if (newCapacity > 0) {
            Node<E>[] oldTable = table;
            Node<E>[] newTable = (Node<E>[]) new Node[newCapacity];

            // Перехеширование всех элементов
            for (int i = 0; i < oldCapacity; i++) {
                Node<E> node = oldTable[i];
                while (node != null) {
                    Node<E> next = node.next;
                    int newIndex = indexFor(node.hash, newCapacity);
                    node.next = newTable[newIndex];
                    newTable[newIndex] = node;
                    node = next;
                }
            }

            table = newTable;
            threshold = (int)(newCapacity * LOAD_FACTOR);
        }
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        boolean first = true;
        for (int i = 0; i < table.length; i++) {
            Node<E> node = table[i];
            while (node != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(node.data);
                first = false;
                node = node.next;
            }
        }

        sb.append("]");
        return sb.toString();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        int hash = hash(o);
        int index = indexFor(hash, table.length);

        Node<E> node = table[index];
        while (node != null) {
            if (node.hash == hash &&
                    (o == node.data || (o != null && o.equals(node.data)))) {
                return true;
            }
            node = node.next;
        }
        return false;
    }

    @Override
    public boolean add(E e) {
        int hash = hash(e);
        int index = indexFor(hash, table.length);

        // Проверяем, есть ли уже такой элемент
        Node<E> node = table[index];
        while (node != null) {
            if (node.hash == hash &&
                    (e == node.data || (e != null && e.equals(node.data)))) {
                return false; // Элемент уже существует
            }
            node = node.next;
        }

        // Добавляем новый элемент
        if (size >= threshold) {
            resize();
            index = indexFor(hash, table.length); // Пересчитываем индекс после resize
        }

        table[index] = new Node<>(e, hash, table[index]);
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int hash = hash(o);
        int index = indexFor(hash, table.length);

        Node<E> prev = null;
        Node<E> current = table[index];

        while (current != null) {
            if (current.hash == hash &&
                    (o == current.data || (o != null && o.equals(current.data)))) {
                // Нашли элемент для удаления
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }

        return false;
    }

    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }

    // Остальные методы интерфейса Set (опциональные)

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int tableIndex = 0;
            private Node<E> current = null;
            private Node<E> next = findNext();
            private int count = 0;

            private Node<E> findNext() {
                // Продолжаем с текущей цепочки
                if (current != null && current.next != null) {
                    return current.next;
                }

                // Ищем следующую непустую ячейку
                while (tableIndex < table.length) {
                    if (table[tableIndex] != null) {
                        return table[tableIndex++];
                    }
                    tableIndex++;
                }
                return null;
            }

            @Override
            public boolean hasNext() {
                return next != null && count < size;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                current = next;
                next = findNext();
                count++;
                return current.data;
            }

            @Override
            public void remove() {
                if (current == null) {
                    throw new IllegalStateException();
                }
                MyHashSet.this.remove(current.data);
                current = null;
                count--;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int index = 0;

        for (int i = 0; i < table.length; i++) {
            Node<E> node = table[i];
            while (node != null) {
                result[index++] = node.data;
                node = node.next;
            }
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            T[] result = (T[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size
            );
            int index = 0;

            for (int i = 0; i < table.length; i++) {
                Node<E> node = table[i];
                while (node != null) {
                    result[index++] = (T) node.data;
                    node = node.next;
                }
            }

            return result;
        }

        int index = 0;
        for (int i = 0; i < table.length; i++) {
            Node<E> node = table[i];
            while (node != null) {
                a[index++] = (T) node.data;
                node = node.next;
            }
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }

        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }

        boolean modified = false;
        for (E e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }

        boolean modified = false;
        for (Object o : c) {
            if (remove(o)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }

        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Set)) {
            return false;
        }

        Set<?> s = (Set<?>) o;
        if (s.size() != size()) {
            return false;
        }

        try {
            return containsAll(s);
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (int i = 0; i < table.length; i++) {
            Node<E> node = table[i];
            while (node != null) {
                if (node.data != null) {
                    result += node.data.hashCode();
                }
                node = node.next;
            }
        }
        return result;
    }
}