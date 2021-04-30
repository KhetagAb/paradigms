(defn operator [fun]
  (fn [& args] (fn [vars] (apply fun (mapv #(% vars) args)))))

(def constant constantly)
(defn variable [var] (fn [vars] (vars var)))

(defn _div
  ([f] (/ 1 (double f)))
  ([f & args] (reduce #(/ %1 (double %2)) f args)))

(def add (operator +))
(def subtract (operator -))
(def multiply (operator *))
(def divide (operator _div))
(def negate subtract)

(def operators {'+ add '- subtract '* multiply '/ divide 'negate subtract})

(defn parseFunction [input]
  (letfn [(parse-operator [operator] (operators operator))
          (parse-expression [exps] (cond
                                     (list? exps) (apply (parse-operator (first exps)) (mapv parse-expression (rest exps)))
                                     (number? exps) (constant exps)
                                     :else (variable (str exps))))]
    (parse-expression (read-string input))))