(defn check-seq
  ([is-every? every-sizes?] (fn [seq]
                                ;NOTE: fix error
                              (and (vector? seq)
                                   (apply every-sizes? seq)
                                   (every? is-every? seq))))
  ([is-every?] (partial (check-seq is-every? (constantly identity)))))

(defn ten-shape ([ten]
                 (if (vector? ten)
                   (if (empty? ten) '(0)
                                    (let [sizes (map ten-shape ten)]
                                      (if (apply = sizes)
                                        (cons (count ten) (first sizes))
                                        (assert "Not tensor"))))
                   '())))

(defn ten-shapes [& tens] (map ten-shape tens))
(defn max-shape [& tens] (apply max-key count (apply ten-shapes tens)))
(defn same-shape? [& tens] (apply = (apply ten-shapes tens)))

(defn by-elem [is-every? fun] (fn [& args]
                                {:pre [((check-seq is-every? same-shape?) args)]}
                                (apply mapv fun args)))

(def vec? (check-seq number?))
(def mat? (check-seq vec? same-shape?))

(def v+ (by-elem vec? +))
(def v- (by-elem vec? -))
(def v* (by-elem vec? *))
(def vd (by-elem vec? /))
(def m+ (by-elem mat? v+))
(def m- (by-elem mat? v-))
(def m* (by-elem mat? v*))
(def md (by-elem mat? vd))

(defn scalar [& vectors]
  {:pre [(mat? vectors)]}
  (apply + (apply v* vectors)))

(defn *s [pred fun] (fn [f & args]
                      {:pre [(pred f)
                             (every? number? args)]}
                      (mapv #(fun % (apply * args)) f)))

(def v*s (*s vec? *))
(def m*s (*s mat? v*s))

(defn transpose [mat]
  {:pre [(mat? mat)]}
  (apply mapv vector mat))
(defn m*v [mat vec]
  {:pre [(mat? mat)
         (vec? vec)]}
  (mapv (partial scalar vec) mat))

(defn vec3? [vec] (and (vec? vec) (== 3 (count vec))))

(defn fold-left [is-each fun] (fn [& args]
                                {:pre [(every? is-each args)]}
                                (reduce fun args)))
(def m*m (fold-left mat? #(mapv (partial m*v (transpose %2)) %1)))
(def vect (fold-left vec3? #(letfn
                               [(cross [i j] (- (* (get %1 i) (get %2 j))
                                                (* (get %2 i) (get %1 j))))]
                               [(cross 1 2) (cross 2 0) (cross 0 1)])))

(defn number-broadcast [n shape]
  (if (empty? shape)
    n
    (into [] (repeat (first shape) (number-broadcast n (rest shape))))))

(defn tensor-cast [ten shape]
  (if (number? ten) (number-broadcast ten shape)
                  (mapv #(tensor-cast % (rest shape)) ten)))

(defn broadcast-able [& tens]
  (letfn [(compat [a b] (if (every? true? (map = a b))
                          (max-key count a b)
                          (reduced false)))]
    (unreduced (reduce compat (max-shape tens) (apply ten-shapes tens)))))

(defn broadcast [& tens]
  {:pre [(apply broadcast-able tens)]}
  (let [max (apply max-shape tens)]
    (mapv #(tensor-cast % max) tens)))

(defn ten-op [fun]
  (letfn [(operate [& tens]
            (if (every? number? tens)
              (apply fun tens)
              (apply mapv operate tens)))]
    (fn [& tens]
      (apply operate (apply broadcast tens)))))

(def tb+ (ten-op +))
(def tb- (ten-op -))
(def tb* (ten-op *))
(def tbd (ten-op /))