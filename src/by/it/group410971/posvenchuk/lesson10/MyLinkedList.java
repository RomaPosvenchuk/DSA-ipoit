package by.it.group410971.posvenchuk.lesson10;

import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyLinkedList<E> implements Deque<E> {

    private static class Node<E> {
        E data;
        Node<E> next;
        Node<E> prev;

        Node(E data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size;

    public MyLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        Node<E> current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }

        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean add(E e) {
        addLast(e);
        return true;
    }

    // Метод remove(int index) - удаляет элемент по индексу
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node<E> nodeToRemove;

        // Оптимизация: если индекс ближе к началу, идем от головы
        if (index < size / 2) {
            nodeToRemove = head;
            for (int i = 0; i < index; i++) {
                nodeToRemove = nodeToRemove.next;
            }
        } else {
            // Если индекс ближе к концу, идем от хвоста
            nodeToRemove = tail;
            for (int i = size - 1; i > index; i--) {
                nodeToRemove = nodeToRemove.prev;
            }
        }

        return removeNode(nodeToRemove);
    }

    @Override
    public boolean remove(Object o) {
        if (size == 0) {
            return false;
        }

        Node<E> current = head;

        while (current != null) {
            if (o == null && current.data == null ||
                    o != null && o.equals(current.data)) {
                removeNode(current);
                return true;
            }
            current = current.next;
        }

        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void addFirst(E e) {
        Node<E> newNode = new Node<>(e);

        if (head == null) {
            // Список пуст
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }

        size++;
    }

    @Override
    public void addLast(E e) {
        Node<E> newNode = new Node<>(e);

        if (tail == null) {
            // Список пуст
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }

        size++;
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E getFirst() {
        if (head == null) {
            throw new NoSuchElementException("Deque is empty");
        }
        return head.data;
    }

    @Override
    public E getLast() {
        if (tail == null) {
            throw new NoSuchElementException("Deque is empty");
        }
        return tail.data;
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E pollFirst() {
        if (head == null) {
            return null;
        }

        return removeNode(head);
    }

    @Override
    public E pollLast() {
        if (tail == null) {
            return null;
        }

        return removeNode(tail);
    }

    // Вспомогательный метод для удаления узла
    private E removeNode(Node<E> node) {
        E data = node.data;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            // Удаляем голову
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            // Удаляем хвост
            tail = node.prev;
        }

        // Помогаем сборщику мусора
        node.data = null;
        node.next = null;
        node.prev = null;

        size--;
        return data;
    }

    // Дополнительный метод для получения элемента по индексу (не из интерфейса Deque)
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node<E> current;

        // Оптимизация: выбираем направление обхода
        if (index < size / 2) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }

        return current.data;
    }

    // Остальные методы интерфейса Deque (опциональные)

    @Override
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    @Override
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    @Override
    public E removeFirst() {
        E element = pollFirst();
        if (element == null) {
            throw new NoSuchElementException("Deque is empty");
        }
        return element;
    }

    @Override
    public E removeLast() {
        E element = pollLast();
        if (element == null) {
            throw new NoSuchElementException("Deque is empty");
        }
        return element;
    }

    @Override
    public E peekFirst() {
        return (head == null) ? null : head.data;
    }

    @Override
    public E peekLast() {
        return (tail == null) ? null : tail.data;
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        if (size == 0) {
            return false;
        }

        Node<E> current = tail;

        while (current != null) {
            if (o == null && current.data == null ||
                    o != null && o.equals(current.data)) {
                removeNode(current);
                return true;
            }
            current = current.prev;
        }

        return false;
    }

    @Override
    public boolean offer(E e) {
        return add(e);
    }

    @Override
    public E remove() {
        return removeFirst();
    }

    @Override
    public E peek() {
        return peekFirst();
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    @Override
    public boolean contains(Object o) {
        Node<E> current = head;

        while (current != null) {
            if (o == null && current.data == null ||
                    o != null && o.equals(current.data)) {
                return true;
            }
            current = current.next;
        }

        return false;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> current = head;
            private Node<E> lastReturned = null;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                lastReturned = current;
                E data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new Iterator<E>() {
            private Node<E> current = tail;
            private Node<E> lastReturned = null;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                lastReturned = current;
                E data = current.data;
                current = current.prev;
                return data;
            }
        };
    }

    @Override
    public void clear() {
        // Помогаем сборщику мусора
        Node<E> current = head;
        while (current != null) {
            Node<E> next = current.next;
            current.data = null;
            current.next = null;
            current.prev = null;
            current = next;
        }

        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean containsAll(java.util.Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeAll(java.util.Collection<?> c) {
        boolean modified = false;
        for (Object o : c) {
            while (remove(o)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(java.util.Collection<?> c) {
        boolean modified = false;
        Node<E> current = head;

        while (current != null) {
            Node<E> next = current.next;
            if (!c.contains(current.data)) {
                removeNode(current);
                modified = true;
            }
            current = next;
        }

        return modified;
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        Node<E> current = head;

        for (int i = 0; i < size; i++) {
            result[i] = current.data;
            current = current.next;
        }

        return result;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            @SuppressWarnings("unchecked")
            T[] result = (T[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size
            );

            Node<E> current = head;
            for (int i = 0; i < size; i++) {
                @SuppressWarnings("unchecked")
                T element = (T) current.data;
                result[i] = element;
                current = current.next;
            }
            return result;
        }

        Node<E> current = head;
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            T element = (T) current.data;
            a[i] = element;
            current = current.next;
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }
}