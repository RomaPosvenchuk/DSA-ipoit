package by.it.group410971.posvenchuk.lesson10;

import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyArrayDeque<E> implements Deque<E> {

    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int head; // индекс первого элемента
    private int tail; // индекс следующего за последним элементом
    private int size;

    public MyArrayDeque() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    // Вспомогательный метод для увеличения емкости массива
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }

            Object[] newElements = new Object[newCapacity];

            // Копируем элементы с учетом кругового буфера
            if (head < tail) {
                // Элементы в непрерывном блоке
                System.arraycopy(elements, head, newElements, 0, size);
            } else if (size > 0) {
                // Элементы в двух блоках (начало и конец массива)
                int firstPart = elements.length - head;
                System.arraycopy(elements, head, newElements, 0, firstPart);
                System.arraycopy(elements, 0, newElements, firstPart, tail);
            }

            elements = newElements;
            head = 0;
            tail = size;
        }
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < size; i++) {
            int index = (head + i) % elements.length;
            sb.append(elements[index]);
            if (i < size - 1) {
                sb.append(", ");
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
    public boolean add(E e) {
        addLast(e);
        return true;
    }

    @Override
    public void addFirst(E e) {
        ensureCapacity(size + 1);

        // Вычисляем новый индекс для head
        head = (head - 1 + elements.length) % elements.length;
        elements[head] = e;
        size++;
    }

    @Override
    public void addLast(E e) {
        ensureCapacity(size + 1);

        elements[tail] = e;
        tail = (tail + 1) % elements.length;
        size++;
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E getFirst() {
        if (size == 0) {
            throw new NoSuchElementException("Deque is empty");
        }

        @SuppressWarnings("unchecked")
        E element = (E) elements[head];
        return element;
    }

    @Override
    public E getLast() {
        if (size == 0) {
            throw new NoSuchElementException("Deque is empty");
        }

        int lastIndex = (tail - 1 + elements.length) % elements.length;
        @SuppressWarnings("unchecked")
        E element = (E) elements[lastIndex];
        return element;
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E pollFirst() {
        if (size == 0) {
            return null;
        }

        @SuppressWarnings("unchecked")
        E element = (E) elements[head];
        elements[head] = null; // для сборщика мусора
        head = (head + 1) % elements.length;
        size--;

        return element;
    }

    @Override
    public E pollLast() {
        if (size == 0) {
            return null;
        }

        tail = (tail - 1 + elements.length) % elements.length;
        @SuppressWarnings("unchecked")
        E element = (E) elements[tail];
        elements[tail] = null; // для сборщика мусора
        size--;

        return element;
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
        return (size == 0) ? null : getFirst();
    }

    @Override
    public E peekLast() {
        return (size == 0) ? null : getLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        throw new UnsupportedOperationException();
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
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int currentIndex = 0;
            private int currentPos = head;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                @SuppressWarnings("unchecked")
                E element = (E) elements[currentPos];
                currentPos = (currentPos + 1) % elements.length;
                currentIndex++;

                return element;
            }
        };
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new Iterator<E>() {
            private int currentIndex = 0;
            private int currentPos = (tail - 1 + elements.length) % elements.length;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                @SuppressWarnings("unchecked")
                E element = (E) elements[currentPos];
                currentPos = (currentPos - 1 + elements.length) % elements.length;
                currentIndex++;

                return element;
            }
        };
    }

    @Override
    public void clear() {
        for (int i = 0; i < elements.length; i++) {
            elements[i] = null;
        }
        head = 0;
        tail = 0;
        size = 0;
    }

    @Override
    public boolean addAll(java.util.Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(java.util.Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];

        if (size == 0) {
            return result;
        }

        if (head < tail) {
            System.arraycopy(elements, head, result, 0, size);
        } else {
            int firstPart = elements.length - head;
            System.arraycopy(elements, head, result, 0, firstPart);
            System.arraycopy(elements, 0, result, firstPart, tail);
        }

        return result;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }
}