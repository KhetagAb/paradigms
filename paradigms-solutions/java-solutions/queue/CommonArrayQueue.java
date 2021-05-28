package queue;

public abstract class CommonArrayQueue {
    protected static Object[] castToSeries(final Object[] array, final int fromPos, final int size, int newSize) {
        assert size <= newSize;

        Object[] newArray = new Object[newSize];
        int onePartLen = Math.min(array.length - fromPos, size);

        System.arraycopy(array, fromPos, newArray, 0, onePartLen);
        if (fromPos + size > array.length) {
            System.arraycopy(array, 0, newArray, onePartLen, size - onePartLen);
        }

        return newArray;
    }
}
