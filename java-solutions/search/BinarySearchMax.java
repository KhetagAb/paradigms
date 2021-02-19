package search;

public class BinarySearchMax {
    /*
    Pred:
        * a != null
        * Immutable: exists k in [0, a.size): forall i, j:
            1) i < j <= k: a[i] < a[j]
            2) k <= j < i: a[i] < a[j]
    Post:
        * R = MAX: forall i, l < i < r: a[MAX] >= a[i]
     */
    private static int iterBinSearchMax(final int[] a) {
        // a.length >= 0
        int l = -1, r = a.length, m;
        // l = -1, r = a.length, l + 1 < r

        // Inv: l + 1 < r && exists i: l < i < r: a[i] = max(a) && Immutable
        // l < r && Inv
        while (r - l > 2) {
            // l + 2 < r
            m = (l + r) / 2;
            // l < m < r - 1

            // -1 <= l < m < m + 1 < r <= a.size
            if (a[m] < a[m + 1]) {
                // a[m] < a[m + 1] && forall i in [0; m]: a[m + 1] > a[i]
                l = m;
                // l' = m
                // a[m] < a[m + 1] && exists i: l < i < r: a[i] = max(a)
            } else {
                // a[m] >= a[m + 1]
                r = m + 1;
                // r' = m + 1
                // a[m] >= a[m + 1] && l + 1 < r && exists i: l < i < r: a[i] = max(a)
            }
            // l' + 1 < r' = r && exists i: l' < i < r': a[i] = max(a)
        }

        // l + 2 == r && l < MAX < r && Immutable -> MAX = a[l + 1]
        return a[l + 1];
    }

    /*
    Pred:
        * a != null
        * 0 <= l + 1 < r <= a.size
        * Immutable: exists k in [0, a.size): forall i, j:
            1) i < j <= k: a[i] < a[j]
            2) k <= j < i: a[i] < a[j]
    Post:
        * R = MAX: forall i, l < i < r: a[MAX] >= a[i]
    Inv:
        * l + 1 < r && exists i: l < i < r: a[i] = max(a) && Immutable
     */
    private static int recBinSearchMax(final int[] a, int l, int r) {
        if (l + 2 == r) {
            // l + 2 == r && l < MAX < r && Immutable -> MAX = a[l + 1]
            return a[l + 1];
        } else {
            // l + 2 < r
            int m = (l + r) / 2;
            // l < m < r - 1

            // -1 <= l < m < m + 1 < r <= a.size
            if (a[m] < a[m + 1]) {
                // a[m] < a[m + 1] && forall i in [0; m]: a[m + 1] > a[i]
                // a != null && 0 <= m + 1 < r <= a.size && Immutable
                return recBinSearchMax(a, m, r);
                // R = R': forall i, m < i < r: a[i] <= a[R'] && a[m] < a[m + 1] && Inv -> Inv
            } else {
                // a[m] >= a[m + 1]
                // a != null && 0 <= m + 1 < r <= a.size && Immutable
                return recBinSearchMax(a, l, m + 1);
                // R = R': forall i, l < i < m + 1: a[i] <= a[R'] && a[m + 1] <= a[m] && Inv -> Inv
            }
        }
    }

    /*
     * Pred:
        * args.length > 0
        * args - forall i a[i] - integer: "a[]"
        * exists k in [0, a.size): forall i, j:
            1) i < j <= k: a[i] < a[j]
            2) k <= j < i: a[i] < a[j]
     * Post:
        * MAX, forall i in [0; a.length) a[i] <= MAX
     */
    public static void main(String[] args) {
        assert args.length > 0;
        // args.length > 0
        int[] a = new int[args.length];
        // a != null && a.length > 0

        // args.length > 0
        for (int i = 0; i < a.length; i++) {
            a[i] = Integer.parseInt(args[i]);
        }

        // a != null
        int res1 = recBinSearchMax(a, -1, a.length);
        // res1 == k && a != null

        // a != null
        int res2 = iterBinSearchMax(a);

        assert res1 == res2;
        // res1 == res2 == k

        // res1 = k
        System.out.println(res2);
    }
}
