(defn mat-size [m] (if (and (vector? m) (not (empty? m)))
                     (let [sizes (map mat-size m)]
                       (if (apply = sizes)
                         (cons (count m) (first sizes))
                         nil))
                     []))

(defn same-size? [args] (apply = (map mat-size args)))

(defn check-seq [is-every every-sizes] (fn [vector]
                                         (and (sequential? vector)
                                              (every-sizes vector)
                                              (every? is-every vector))))
(def vec? (check-seq number? identity))
(def mat? (check-seq vec? same-size?))

(defn by-elem [is-each fun] (fn [& args]
                              {:pre [((check-seq is-each same-size?) args)]}
                              (apply mapv fun args)))

(def v+ (by-elem vec? +))
(def v- (by-elem vec? -))
(def v* (by-elem vec? *))
(def vd (by-elem vec? /))
(def m+ (by-elem mat? v+))
(def m- (by-elem mat? v-))
(def m* (by-elem mat? v*))
(def md (by-elem mat? vd))

(defn scalar [& args]
  {:pre [((check-seq vec? same-size?) args)]}
  (apply + (apply v* args)))

(defn *s [pred fun] (fn [f & args]
                      {:pre [(pred f)
                             (every? number? args)]}
                      (mapv #(fun % (apply * args)) f)))

(def v*s (*s vec? *))
(def m*s (*s mat? v*s))

(defn transpose [m]
  {:pre [(mat? m)]}
  (apply mapv vector m))
(defn m*v [m v]
  {:pre [(mat? m)
         (vec? v)]}
  (mapv #(scalar v %) m))

(defn fold-left [is-each fun] (fn [& args]
                                {:pre [(every? is-each args)]}
                                (reduce fun (first args) (rest args))))

(def m*m (fold-left mat?
                    (fn [a b]
                      (mapv #(m*v (transpose b) %) a))))
(def vect (fold-left vec?
                     (fn [a b]
                       {:pre [(== (count a) (count b) 3)]}
                       (letfn [(cross [i j] (- (* (get a i) (get b j))
                                               (* (get b i) (get a j))))]
                         [(cross 1 2) (cross 2 0) (cross 0 1)]))))