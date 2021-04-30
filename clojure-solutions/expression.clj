(defn operator [fun]
  (fn [& args] (fn [vars] (apply fun (mapv #(% vars) args)))))

(def constant constantly)
(defn variable [var] (fn [vars] (vars var)))

(defn _div
  ([f] (/ 1 (double f)))
  ([f & args] (reduce #(/ %1 (double %2)) f args)))
(defn square [a] (* a a))
(defn mean-op [& args] (_div (apply + args) (count args)))
(defn varn-op [& args] (- (apply mean-op (map square args)) (square (apply mean-op args))))

(def add (operator +))
(def subtract (operator -))
(def multiply (operator *))
(def divide (operator _div))
(def mean (operator mean-op))
(def varn (operator varn-op))
(def negate subtract)

(def operators {'+ add '- subtract '* multiply '/ divide 'negate subtract 'mean mean 'varn varn})

(defn parseFunction [input]
  (letfn [(parse-expr [exps] (cond
                               (list? exps) (apply (operators (first exps)) (mapv parse-expr (rest exps)))
                               (number? exps) (constant exps)
                               :else (variable (str exps))))]
    (parse-expr (read-string input))))