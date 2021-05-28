package search;

public class BinarySearch {
    /*
    Pred:
        * a != null
        * Immutable: forall i, j >= 0 (i < j -> a[i] >= a[j])
    Post:
        * R = k in [0; a.length]: forall L: (0 <= L < k) a[L] > x
     */
    private static int iterBinSearch(final int x, final int[] a) {
        int l = -1, r = a.length, m;
        // l = -1, r = a.length, l < r

        // Inv: k - searchable, l < k <= r && forall L: (0 <= L <= l < k): a[L] > x
        // l < r && Inv
        while (r - l > 1) {
            // l + 1 < r && (l != -1 || r != 0)
            m = (l + r) / 2;
            // l <= m < r && m != -1

            // m != -1
            if (x < a[m]) {
                // x < a[m]
                l = m;
                // l' := m
                // m < k && m = l' < k <= r
                // Immutable && x < a[m] -> forall L: (0 <= L <= l' = m): a[L] > x
            } else {
                // a[m] <= x
                r = m;
                // r' := m
                // k <= m && l < k <= r' = m
            }
            // l' < k <= r' && forall L: (0 <= L <= l'): a[L] > x
        }

        // (r == l + 1 && l < k <= r) -> k = r
        // forall L: (0 <= L < k = r): a[L] > x
        return r;
    }

    /*
    Pred:
        * a != null
        * -1 <= l < r <= a.size
        * Immutable: forall i, j >= 0 (i < j -> a[i] >= a[j])
    Post:
        * R = k in (l; r]: forall L: (0 <= L < k) a[L] > x
    Inv:
        * k - searchable, l < k <= r && forall L: (0 <= L <= l < k): a[L] > x
     */
    private static int recBinSearch(final int x, final int[] a, int l, int r) {
        if (r == l +  1) {
            // r == l + 1 && l < k <= r && forall L: (0 <= L <= l < k): a[L] > x -> R = r = k
            return r;
        } else {
            // l + 1 < r && (l != -1 || r != 0)
            int m = (l + r) / 2;
            // l <= m < r && m != -1

            // m != -1
            if (x < a[m]) {
                // x < a[m]
                // a != null && l <= m = l' < r' = r && Immutable
                return recBinSearch(x, a, m, r);
                // R' in (m, r]: forall L: (0 <= L < R') a[L] > xc&& x < a[m] ->
                // R = R': forall L: (0 <= L <= l < R): a[L] > x
            } else {
                // a[m] <= x
                // a != null && l <= m = l' < r' = r && Immutable
                return recBinSearch(x, a, l, m);
                // R' in (l, m]: forall L: (0 <= L < R') a[L] > x && a[m] <= x ->
                // R = R': forall L: (0 <= L <= l < R): a[L] > x
            }
        }
    }

    /*
    * Pred:
        * args.length > 0
        * args - forall i a[i] - integer: "x, a[]"
        * forall i, j > 0 (i < j => a[i] >= a[j])
    * Post:
        * k - searchable, forall L: (0 <= L <= l < k): a[L] > x
     */
    public static void main(String[] args) {
        // args.length > 0
        int[] a = new int[args.length - 1];
        // a != null && a.length >= 0

        // args.length > 0
        int x = Integer.parseInt(args[0]);
        for (int i = 0; i < a.length; i++) {
            a[i] = Integer.parseInt(args[i + 1]);
        }

        // a != null
        int res1 = recBinSearch(x, a, -1, a.length);
        // res1 == k && a != null

        // a != null
        int res2 = iterBinSearch(x, a);

        assert res1 != res2;
        // res1 == res2 == k

        // res1 = k
        System.out.println(res1);
    }
}
