; HOMEWORK №9
(defn operator [fun]
  (fn [& args] (fn [vars] (apply fun (mapv #(% vars) args)))))

(def constant constantly)
(defn variable [var] (fn [vars] (vars var)))

(defn _div
  ([f & args] (/ (double f) (apply * args)))
  ([f] (/ 1.0 f)))
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

(defn parser [constant variables operators]
  (letfn [(parse-expr [exps]
            (cond
              (list? exps) (apply (operators (first exps)) (mapv parse-expr (rest exps)))
              (number? exps) (constant exps)
              :else (variables (str exps))))]
    (fn [input]
      (parse-expr (read-string input)))))

(def parseFunction
  (parser constant
          {"x" (variable "x")
           "y" (variable "y")
           "z" (variable "z")}
          {'+ add
           '- subtract
           '* multiply
           '/ divide
           'negate subtract
           'mean mean
           'varn varn}))

; HOMEWORK №10
(load-file "proto.clj")

(defn arith-mean [& args] (/ (apply + args) (count args)))
(defn geom-mean [& args] (Math/pow (Math/abs (double (apply * args))) (/ 1 (count args))))
(defn harm-mean [& args] (/ (double (count args)) (apply + (mapv #(/ 1 (double %)) args))))

(def diff (method :diff))
(def evaluate (method :evaluate))
(def toString (method :toString))
(def _value (field :value))

(defn expression-proto [evaluate diff toString]
  {:evaluate evaluate
   :diff diff
   :toString toString})

(declare Zero)

(def Constant
  (constructor
    (fn [this value]
      (assoc this :value (double value)))
    (expression-proto
      (fn [this _] (_value this))
      (fn [_ _] Zero)
      (fn [this] (str (_value this))))))

(def Zero (Constant 0))
(def One (Constant 1))

(def Variable
  (constructor
    (fn [this value]
      (assoc this :value (str value)))
    (expression-proto
      (fn [this vars] (vars (_value this)))
      (fn [this var]
        (if (= var (_value this))
          One
          Zero))
      (fn [this] (_value this)))))


(def Operator-prototype
  (let [_args (field :args)
        _symbol (field :symbol)
        _operate (field :operate)
        _diff-impl (field :diff-impl)]
    (expression-proto
      (fn [this vars]
        (apply (_operate this)
               (mapv #(evaluate % vars) (_args this))))
      (fn [this var] (let [args (_args this)]
                       ((_diff-impl this) args (mapv #(diff % var) args))))
      (fn [this]
        (str "("  (_symbol this) " "
             (clojure.string/join " " (mapv (partial toString) (_args this))) ")")))))

(defn Operator-factory [symbol operate diff-impl]
  (constructor

    (fn [this & args]
      (assoc this
        :args args,
        ; :NOTE: Прототип
        :symbol symbol
        :operate operate
        :diff-impl diff-impl))
    Operator-prototype))

(declare Multiply)
(def Negate
  (Operator-factory
    "negate" -
    (fn [_ darg] (Negate (first darg)))))

(def Square
  (Operator-factory
    "square" square
    (fn [arg darg] (Multiply (Constant 2) (first arg) (first darg)))))


(def Add
  (Operator-factory
    "+" +
    (fn [_ dargs] (apply Add dargs))))

(def Subtract
  (Operator-factory
    "-" -
    (fn [_ dargs] (apply Subtract dargs))))

(defn diff-rule-mul [args dargs]
  (second (reduce
            (fn [[f fd] [s sd]]
              [(Multiply f s)
               (Add (Multiply fd s) (Multiply f sd))]) (mapv vector args dargs))))

(def Multiply (Operator-factory "*" * diff-rule-mul))

(def Divide
  (Operator-factory
    "/" _div
; :NOTE: Явная рекурсия - fixed
    (fn [[a & as] dargs] (if (nil? as)
                        (Negate (Divide (first dargs) (Square a)))
                        (let [m (apply Multiply as)]
                          (Divide
                            (Subtract (Multiply m (first dargs))
                                      (Multiply (diff-rule-mul a (rest dargs)) a))
                            (Multiply m m)))))))

; :NOTE: Упростить - fixed
(def ArithMean
  (Operator-factory
    "arith-mean" arith-mean
    (fn [_ dargs] (Divide (apply Add dargs)
                             (Constant (count dargs))))))

(def GeomMean
  (Operator-factory
    "geom-mean" geom-mean
    (fn [args dargs] (Multiply
                       (Constant (/ 1 (count args)))
                       (apply GeomMean args)
                       (apply Add (mapv #(Divide %2 %1) args dargs))))))

(def HarmMean
  (Operator-factory
    "harm-mean" harm-mean
    (fn [args dargs]
      (Multiply
        (Square (apply HarmMean args))
        (apply ArithMean (mapv #(Divide %2 (Square %1)) args dargs))))))

(def parseObject
  (parser Constant
          {"x" (Variable "x")
           "y" (Variable "y")
           "z" (Variable "z")}
          {'+ Add
           '- Subtract
           '* Multiply
           '/ Divide
           'negate Negate
           'arith-mean ArithMean
           'geom-mean GeomMean
           'harm-mean HarmMean}))