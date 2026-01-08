package by.it.group410971.posvenchuk.lesson10;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class MyPriorityQueue<E> implements Queue<E> {

    private static final int DEFAULT_CAPACITY = 10;
    private Object[] heap;
    private int size;
    private Comparator<? super E> comparator;

    public MyPriorityQueue() {
        this(DEFAULT_CAPACITY, null);
    }

    public MyPriorityQueue(int initialCapacity) {
        this(initialCapacity, null);
    }

    public MyPriorityQueue(Comparator<? super E> comparator) {
        this(DEFAULT_CAPACITY, comparator);
    }

    public MyPriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        this.heap = new Object[initialCapacity];
        this.size = 0;
        this.comparator = comparator;
    }

    // Конструктор для создания из коллекции
    @SuppressWarnings("unchecked")
    public MyPriorityQueue(Collection<? extends E> c) {
        this(c.size(), null);
        addAll(c);
    }

    // Вспомогательный метод для сравнения элементов
    @SuppressWarnings("unchecked")
    private int compare(E a, E b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        } else {
            Comparable<? super E> comparableA = (Comparable<? super E>) a;
            return comparableA.compareTo(b);
        }
    }

    // Увеличение емкости кучи
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > heap.length) {
            int newCapacity = heap.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }

            Object[] newHeap = new Object[newCapacity];
            for (int i = 0; i < size; i++) {
                newHeap[i] = heap[i];
            }
            heap = newHeap;
        }
    }

    // Просеивание вверх (восстановление свойства кучи при добавлении)
    private void siftUp(int index) {
        if (index == 0) return;

        @SuppressWarnings("unchecked")
        E current = (E) heap[index];
        int parentIndex = (index - 1) / 2;

        @SuppressWarnings("unchecked")
        E parent = (E) heap[parentIndex];

        if (compare(current, parent) < 0) {
            // Меняем местами с родителем
            heap[index] = parent;
            heap[parentIndex] = current;
            siftUp(parentIndex);
        }
    }

    // Просеивание вниз (восстановление свойства кучи при удалении корня)
    private void siftDown(int index) {
        int smallest = index;
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;

        @SuppressWarnings("unchecked")
        E current = (E) heap[index];

        // Сравниваем с левым потомком
        if (leftChild < size) {
            @SuppressWarnings("unchecked")
            E left = (E) heap[leftChild];
            if (compare(left, current) < 0) {
                smallest = leftChild;
                current = left;
            }
        }

        // Сравниваем с правым потомком
        if (rightChild < size) {
            @SuppressWarnings("unchecked")
            E right = (E) heap[rightChild];
            @SuppressWarnings("unchecked")
            E smallestElement = (E) heap[smallest];
            if (compare(right, smallestElement) < 0) {
                smallest = rightChild;
            }
        }

        // Если нашли меньший элемент среди потомков
        if (smallest != index) {
            Object temp = heap[index];
            heap[index] = heap[smallest];
            heap[smallest] = temp;
            siftDown(smallest);
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
            sb.append(heap[i]);
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
    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean add(E e) {
        if (e == null) {
            throw new NullPointerException("Element cannot be null");
        }

        ensureCapacity(size + 1);
        heap[size] = e;
        siftUp(size);
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        // Удаление произвольного элемента из кучи
        for (int i = 0; i < size; i++) {
            if (o.equals(heap[i])) {
                removeAt(i);
                return true;
            }
        }
        return false;
    }

    // Удаление элемента по индексу
    private void removeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        size--;
        if (index == size) {
            heap[index] = null;
        } else {
            // Заменяем удаляемый элемент последним
            heap[index] = heap[size];
            heap[size] = null;

            // Восстанавливаем свойство кучи
            siftDown(index);
            // Если элемент не опустился вниз, пробуем поднять его вверх
            if (index > 0) {
                @SuppressWarnings("unchecked")
                E element = (E) heap[index];
                @SuppressWarnings("unchecked")
                E parent = (E) heap[(index - 1) / 2];
                if (compare(element, parent) < 0) {
                    siftUp(index);
                }
            }
        }
    }

    @Override
    public E remove() {
        if (size == 0) {
            throw new NoSuchElementException("Queue is empty");
        }
        return poll();
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < size; i++) {
            if (o.equals(heap[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean offer(E e) {
        return add(e);
    }

    @Override
    public E poll() {
        if (size == 0) {
            return null;
        }

        @SuppressWarnings("unchecked")
        E result = (E) heap[0];

        size--;
        if (size > 0) {
            heap[0] = heap[size];
            heap[size] = null;
            siftDown(0);
        } else {
            heap[0] = null;
        }

        return result;
    }

    @Override
    public E peek() {
        if (size == 0) {
            return null;
        }
        @SuppressWarnings("unchecked")
        E result = (E) heap[0];
        return result;
    }

    @Override
    public E element() {
        if (size == 0) {
            throw new NoSuchElementException("Queue is empty");
        }
        return peek();
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
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

        if (c.isEmpty()) {
            return false;
        }

        ensureCapacity(size + c.size());

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

        if (size == 0) {
            return false;
        }

        boolean modified = false;
        // Создаем временный массив для хранения элементов, которые не нужно удалять
        Object[] temp = new Object[size];
        int newSize = 0;

        for (int i = 0; i < size; i++) {
            if (!c.contains(heap[i])) {
                temp[newSize++] = heap[i];
            } else {
                modified = true;
            }
        }

        // Если что-то удалили, перестраиваем кучу
        if (modified) {
            // Копируем обратно
            for (int i = 0; i < newSize; i++) {
                heap[i] = temp[i];
            }
            // Очищаем остальные
            for (int i = newSize; i < size; i++) {
                heap[i] = null;
            }
            size = newSize;

            // Перестраиваем кучу
            for (int i = (size / 2) - 1; i >= 0; i--) {
                siftDown(i);
            }
        }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("Collection cannot be null");
        }

        if (size == 0) {
            return false;
        }

        boolean modified = false;
        // Создаем временный массив для хранения элементов, которые нужно оставить
        Object[] temp = new Object[size];
        int newSize = 0;

        for (int i = 0; i < size; i++) {
            if (c.contains(heap[i])) {
                temp[newSize++] = heap[i];
            } else {
                modified = true;
            }
        }

        // Если что-то удалили, перестраиваем кучу
        if (modified) {
            // Копируем обратно
            for (int i = 0; i < newSize; i++) {
                heap[i] = temp[i];
            }
            // Очищаем остальные
            for (int i = newSize; i < size; i++) {
                heap[i] = null;
            }
            size = newSize;

            // Перестраиваем кучу
            for (int i = (size / 2) - 1; i >= 0; i--) {
                siftDown(i);
            }
        }

        return modified;
    }

    /////////////////////////////////////////////////////////////////////////
    //////      Методы, которые можно не реализовывать полностью      ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return (E) heap[currentIndex++];
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = heap[i];
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
            for (int i = 0; i < size; i++) {
                result[i] = (T) heap[i];
            }
            return result;
        }

        for (int i = 0; i < size; i++) {
            a[i] = (T) heap[i];
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    /////////////////////////////////////////////////////////////////////////
    //////     Методы, которые выбрасывают UnsupportedOperationException /////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}