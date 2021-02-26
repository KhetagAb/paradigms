package queue;

public abstract class CommonArrayQueue {
    protected static String toStr(Object[] queueArray) {
        StringBuilder sb = new StringBuilder("[" + (queueArray.length == 0 ? "" : queueArray[0].toString()));

        for (int i = 1; i < queueArray.length; i++) {
            sb.append(", ").append(queueArray[i]);
        }

        return sb.append(']').toString();
    }

    protected static Object[] normalize(final Object[] array, final int fromPos, final int fromSize, int toLen) {
        assert fromSize <= toLen;

        Object[] newArray = new Object[toLen];
        int leftPartLen = Math.min(array.length - fromPos, fromSize);

        System.arraycopy(array, fromPos, newArray, 0, leftPartLen);
        if (fromPos + fromSize > array.length) {
            System.arraycopy(array, 0, newArray, leftPartLen, fromSize - leftPartLen);
        }

        return newArray;
    }
}
