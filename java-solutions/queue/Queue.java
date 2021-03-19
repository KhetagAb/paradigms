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
        // :NOTE: Не сходится


        Let Nth: forall i = 1..size: a[i] = a'[i + (i - 1) / (n - 1)]
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
        PRED: size > 0
        POST: R == a[1] && size = size' && Imm
    */
    Object element();


    /*
        PRED: true
        POST: R == size && size = size' && Imm
    */
    int size();


    /*
        PRED: true
        POST: R == {size == 0} && size = size' && Imm
    */
    boolean isEmpty();


    /*
        PRED: true
        POST: size == 0 && size = size' && Imm
    */
    void clear();


    /*
        PRED: n > 0
        POST: R == [a[1 * n], a[2 * n], ..., a[size / n]] && size = size' && Imm
    */
    Queue getNth(int n);


    /*
        PRED: n > 0
        POST: R == [a'[1 * n], a'[2 * n], ..., a'[size / n]] && size = size' - (size' / n) && Nth
    */
    Queue removeNth(int n);

    /*
        PRED: n > 0
        POST: size = size' - (size' / n) && Nth
    */
    void dropNth(int n);
}
