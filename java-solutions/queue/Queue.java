package queue;

public interface Queue {
    /*
        MODEL:
            [a_1, a_2, ..., a_size]
            size -- размер очереди

        Inv:
            size >= 0
            forall i = 1..size: a_i != null

        Let Imm: forall i = 1..size': a[i] = a'[i]
    */

    /*
        PRED: element != null
        POST: size = size' + 1 && a[size] = element && Imm
    */
    void enqueue(final Object element);


    /*
        PRED: size > 0
        POST: R == a[1] && size = size' - 1 && forall i = 1..size: a[i] = a'[i + 1]
    */
    Object dequeue();


    /*
        PRED: element != null
        POST: size = size' + 1 && a[1] = element && forall i = 2..size': a[i + 1] = a'[i]
    */
    void push(final Object element);


    /*
        PRED: size > 0
        POST: R == a[n] && size = size' - 1 && Imm
    */
    Object remove();


    /*
        PRED: size > 0
        POST: R == a[1] && size = size' && Imm
    */
    Object element();


    /*
        PRED: size > 0
        POST: R == a[n] && size = size' && Imm
    */
    Object peek();


    /*
        PRED: true
        POST: R == size && size = size' && Imm
    */
    int size();


    /*
        PRED: true
        POST: R == [size == 0] && size = size' && Imm
    */
    boolean isEmpty();


    /*
        PRED: true
        POST: size == 0 && size = size' && Imm
    */
    void clear();


    /*
        PRED: true
        POST: R = [a_1, a_2, ..., a_size] && size = size' && Imm
    */
    Object[] toArray();


    /*
        PRED: true
        POST: R = "[a_1, ... , a_size]" && size = size' && Imm
    */
    String toStr();
}
