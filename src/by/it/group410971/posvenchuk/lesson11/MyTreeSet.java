package by.it.group410971.posvenchuk.lesson11;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class MyTreeSet<E> implements Set<E> {

    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int size;
    private Comparator<? super E> comparator;

    public MyTreeSet() {
        this(DEFAULT_CAPACITY, null);
    }

    public MyTreeSet(Comparator<? super E> comparator) {
        this(DEFAULT_CAPACITY, comparator);
    }

    public MyTreeSet(int initialCapacity, Comparator<? super E> comparator) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Initial capacity cannot be negative");
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
        this.comparator = comparator;
    }

    // Вспомогательный метод для сравнения элементов
    @SuppressWarnings("unchecked")
    private int compare(E e1, E e2) {
        if (comparator != null) {
            return comparator.compare(e1, e2);
        } else {
            Comparable<? super E> comparable = (Comparable<? super E>) e1;
            return comparable.compareTo(e2);
        }
    }

    // Бинарный поиск элемента
    private int binarySearch(E element) {
        int left = 0;
        int right = size - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            @SuppressWarnings("unchecked")
            E midElement = (E) elements[mid];
            int cmp = compare(element, midElement);

            if (cmp == 0) {
                return mid; // Нашли элемент
            } else if (cmp < 0) {
                right = mid - 1; // Ищем в левой половине
            } else {
                left = mid + 1; // Ищем в правой половине
            }
        }

        return -(left + 1); // Элемент не найден, возвращаем позицию для вставки
    }

    // Увеличение емкости массива
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            Object[] newElements = new Object[newCapacity];
            System.arraycopy(elements, 0, newElements, 0, size);
            elements = newElements;
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
            sb.append(elements[i]);
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
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }

        @SuppressWarnings("unchecked")
        E element = (E) o;
        int index = binarySearch(element);
        return index >= 0;
    }

    @Override
    public boolean add(E e) {
        if (e == null) {
            throw new NullPointerException("Element cannot be null");
        }

        int index = binarySearch(e);

        if (index >= 0) {
            // Элемент уже существует
            return false;
        }

        // Вычисляем позицию для вставки
        int insertPos = -(index + 1);

        // Увеличиваем емкость при необходимости
        ensureCapacity(size + 1);

        // Сдвигаем элементы вправо для освобождения места
        if (insertPos < size) {
            System.arraycopy(elements, insertPos, elements, insertPos + 1, size - insertPos);
        }

        // Вставляем новый элемент
        elements[insertPos] = e;
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null || size == 0) {
            return false;
        }

        @SuppressWarnings("unchecked")
        E element = (E) o;
        int index = binarySearch(element);

        if (index < 0) {
            // Элемент не найден
            return false;
        }

        // Сдвигаем элементы влево
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }

        elements[--size] = null; // Очищаем последний элемент
        return true;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
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
        // Создаем временный массив для элементов, которые нужно оставить
        Object[] temp = new Object[size];
        int newSize = 0;

        for (int i = 0; i < size; i++) {
            if (c.contains(elements[i])) {
                temp[newSize++] = elements[i];
            } else {
                modified = true;
            }
        }

        if (modified) {
            // Заменяем старый массив новым
            elements = temp;
            size = newSize;
        }

        return modified;
    }

    // Опциональные методы интерфейса Set

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int currentIndex = 0;
            private int lastReturnedIndex = -1;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                lastReturnedIndex = currentIndex;
                return (E) elements[currentIndex++];
            }

            @Override
            public void remove() {
                if (lastReturnedIndex == -1) {
                    throw new IllegalStateException();
                }
                MyTreeSet.this.remove(elements[lastReturnedIndex]);
                currentIndex = lastReturnedIndex;
                lastReturnedIndex = -1;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size];
        System.arraycopy(elements, 0, result, 0, size);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            T[] result = (T[]) java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size
            );
            System.arraycopy(elements, 0, result, 0, size);
            return result;
        }

        System.arraycopy(elements, 0, a, 0, size);

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    // Дополнительные методы, полезные для TreeSet

    // Получить первый (наименьший) элемент
    public E first() {
        if (size == 0) {
            throw new java.util.NoSuchElementException("Set is empty");
        }
        @SuppressWarnings("unchecked")
        E result = (E) elements[0];
        return result;
    }

    // Получить последний (наибольший) элемент
    public E last() {
        if (size == 0) {
            throw new java.util.NoSuchElementException("Set is empty");
        }
        @SuppressWarnings("unchecked")
        E result = (E) elements[size - 1];
        return result;
    }

    // Получить наименьший элемент, который больше или равен заданному
    public E ceiling(E e) {
        if (e == null) {
            throw new NullPointerException("Element cannot be null");
        }

        int index = binarySearch(e);
        if (index >= 0) {
            // Элемент найден
            @SuppressWarnings("unchecked")
            E result = (E) elements[index];
            return result;
        } else {
            // Элемент не найден, находим позицию для вставки
            int insertPos = -(index + 1);
            if (insertPos < size) {
                @SuppressWarnings("unchecked")
                E result = (E) elements[insertPos];
                return result;
            }
        }
        return null;
    }

    // Получить наибольший элемент, который меньше или равен заданному
    public E floor(E e) {
        if (e == null) {
            throw new NullPointerException("Element cannot be null");
        }

        int index = binarySearch(e);
        if (index >= 0) {
            // Элемент найден
            @SuppressWarnings("unchecked")
            E result = (E) elements[index];
            return result;
        } else {
            // Элемент не найден, находим позицию для вставки
            int insertPos = -(index + 1);
            if (insertPos > 0) {
                @SuppressWarnings("unchecked")
                E result = (E) elements[insertPos - 1];
                return result;
            }
        }
        return null;
    }

    // Получить подмножество
    public MyTreeSet<E> subSet(E fromElement, E toElement) {
        if (fromElement == null || toElement == null) {
            throw new NullPointerException("Elements cannot be null");
        }

        if (compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("fromElement > toElement");
        }

        MyTreeSet<E> result = new MyTreeSet<>(comparator);

        // Находим индекс начала
        int fromIndex = binarySearch(fromElement);
        if (fromIndex < 0) {
            fromIndex = -(fromIndex + 1);
        }

        // Находим индекс конца
        int toIndex = binarySearch(toElement);
        if (toIndex < 0) {
            toIndex = -(toIndex + 1);
        } else {
            toIndex++; // Включаем toElement
        }

        // Добавляем элементы в подмножество
        for (int i = fromIndex; i < toIndex && i < size; i++) {
            @SuppressWarnings("unchecked")
            E element = (E) elements[i];
            if (compare(element, toElement) < 0) {
                result.add(element);
            }
        }

        return result;
    }
}