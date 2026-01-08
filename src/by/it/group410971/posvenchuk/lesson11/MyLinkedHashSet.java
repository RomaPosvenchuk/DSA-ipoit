package by.it.group410971.posvenchuk.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyLinkedHashSet<E> implements Set<E> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private static class LinkedNode<E> {
        E data;
        LinkedNode<E> next;    // следующий в цепочке коллизий
        LinkedNode<E> after;   // следующий в порядке добавления
        LinkedNode<E> before;  // предыдущий в порядке добавления
        int hash;

        LinkedNode(E data, int hash) {
            this.data = data;
            this.hash = hash;
            this.next = null;
            this.after = null;
            this.before = null;
        }
    }

    private LinkedNode<E>[] table;
    private int size;

    private LinkedNode<E> head; // первый добавленный элемент
    private LinkedNode<E> tail; // последний добавленный элемент
    private int threshold;

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet() {
        this.table = (LinkedNode<E>[]) new LinkedNode[DEFAULT_CAPACITY];
        this.size = 0;
        this.head = null;
        this.tail = null;
        this.threshold = (int)(DEFAULT_CAPACITY * LOAD_FACTOR);
    }

    // Вычисление хеш-кода
    private int hash(Object key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        return h ^ (h >>> 16);
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
        LinkedNode<E>[] oldTable = table;
        LinkedNode<E>[] newTable = (LinkedNode<E>[]) new LinkedNode[newCapacity];

        // Перехеширование всех элементов
        for (int i = 0; i < oldCapacity; i++) {
            LinkedNode<E> node = oldTable[i];
            while (node != null) {
                LinkedNode<E> next = node.next;
                int newIndex = indexFor(node.hash, newCapacity);
                node.next = newTable[newIndex];
                newTable[newIndex] = node;
                node = next;
            }
        }

        table = newTable;
        threshold = (int)(newCapacity * LOAD_FACTOR);
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        LinkedNode<E> current = head;
        boolean first = true;

        while (current != null) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(current.data);
            first = false;
            current = current.after;
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

        LinkedNode<E> node = table[index];
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
        LinkedNode<E> node = table[index];
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
            index = indexFor(hash, table.length); // Пересчитываем индекс
        }

        LinkedNode<E> newNode = new LinkedNode<>(e, hash);

        // Добавляем в хеш-таблицу
        newNode.next = table[index];
        table[index] = newNode;

        // Добавляем в цепочку порядка добавления
        addToOrderChain(newNode);

        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int hash = hash(o);
        int index = indexFor(hash, table.length);

        LinkedNode<E> prev = null;
        LinkedNode<E> current = table[index];

        while (current != null) {
            if (current.hash == hash &&
                    (o == current.data || (o != null && o.equals(current.data)))) {
                // Нашли элемент для удаления

                // Удаляем из цепочки коллизий
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }

                // Удаляем из цепочки порядка добавления
                removeFromOrderChain(current);

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
        head = null;
        tail = null;
        size = 0;
    }

    // Методы для поддержания порядка добавления

    private void addToOrderChain(LinkedNode<E> newNode) {
        if (head == null) {
            // Первый элемент
            head = newNode;
            tail = newNode;
        } else {
            // Добавляем в конец цепочки
            tail.after = newNode;
            newNode.before = tail;
            tail = newNode;
        }
    }

    private void removeFromOrderChain(LinkedNode<E> node) {
        if (node.before != null) {
            node.before.after = node.after;
        } else {
            // Это голова
            head = node.after;
        }

        if (node.after != null) {
            node.after.before = node.before;
        } else {
            // Это хвост
            tail = node.before;
        }

        // Очищаем ссылки
        node.before = null;
        node.after = null;
    }

    // Остальные методы интерфейса Set

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
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private LinkedNode<E> current = null;
            private LinkedNode<E> next = head;
            private LinkedNode<E> lastReturned = null;

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                lastReturned = next;
                current = next;
                next = next.after;
                return current.data;
            }

            @Override
            public void remove() {
                if (lastReturned == null) {
                    throw new IllegalStateException();
                }
                MyLinkedHashSet.this.remove(lastReturned.data);
                lastReturned = null;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        int index = 0;

        LinkedNode<E> current = head;
        while (current != null) {
            result[index++] = current.data;
            current = current.after;
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

            LinkedNode<E> current = head;
            while (current != null) {
                result[index++] = (T) current.data;
                current = current.after;
            }

            return result;
        }

        int index = 0;
        LinkedNode<E> current = head;
        while (current != null) {
            a[index++] = (T) current.data;
            current = current.after;
        }

        if (index < a.length) {
            a[index] = null;
        }

        return a;
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
        LinkedNode<E> current = head;
        while (current != null) {
            if (current.data != null) {
                result += current.data.hashCode();
            }
            current = current.after;
        }
        return result;
    }
}