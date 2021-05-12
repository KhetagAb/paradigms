(defn operator [fun]
  (fn [& args] (fn [vars] (apply fun (mapv #(% vars) args)))))

(def constant constantly)
(defn variable [var] (fn [vars] (vars var)))

(defn _div
  ([f & args] (/ (double f) (apply * args)))
  ([f] (/ 1.0 f)))
(defn square [a] (* a a))
(defn pow [a b] (Math/pow a b))
(defn sign [a] (Math/signum (double a)))
(defn abs [a] (Math/abs (double a)))

(defn arith-mean [& args] (/ (apply + args) (count args)))
(defn geom-mean [& args] (Math/pow (Math/abs (double (apply * args))) (/ 1 (count args))))
(defn harm-mean [& args] (/ (double (count args)) (apply + (mapv #(/ 1 (double %)) args))))

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
  (fn [input]
    (letfn [(parse-expr [exps]
              (cond
                (list? exps) (apply (operators (first exps)) (mapv parse-expr (rest exps)))
                (number? exps) (constant exps)
                :else (variables (str exps))))]
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


(load-file "proto.clj")

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
      (fn [this] (format "%.1f" (_value this))))))

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
      (fn [this var]
        (apply (_diff-impl this)
               var (mapv #(vector % (diff % var)) (_args this))))
      (fn [this]
        (str "("  (_symbol this) " "
             (clojure.string/join " " (mapv (partial toString) (_args this))) ")")))))

(defn Operator-factory [symbol operate diff-impl]
  (constructor
    (fn [this & args]
      (assoc this
        :args args,
        :symbol symbol
        :operate operate
        :diff-impl diff-impl))
    Operator-prototype))

(declare Multiply)
(def Negate
  (Operator-factory
    "negate" -
    (fn [_ arg] (Negate (arg 1)))))

(def Square
  (Operator-factory
    "square" square
    (fn [_ arg] (Multiply (Constant 2) (get arg 0) (get arg 1)))))

(def Sign
  (Operator-factory
    "sign" sign
    (fn [_ _] Zero)))

(def Abs
  (Operator-factory
    "abs" abs
    (fn [_ a] (Multiply (Sign (first a)) (second a)))))

(def Add
  (Operator-factory
    "+" +
    (fn [_ & args] (apply Add (map (partial second) args)))))

(def Subtract
  (Operator-factory
    "-" -
    (fn [_ & args] (apply Subtract (map (partial second) args)))))

(def Multiply
  (Operator-factory
    "*" *
; :NOTE: (apply Add ...)
    (fn [_ & args] (second (reduce
                             (fn [[f fd] [s sd]]
                               [(Multiply f s)
                                (Add (Multiply fd s) (Multiply f sd))]) args)))))

(def Divide
  (Operator-factory
    "/" _div
; :NOTE: Явная рекурсия
    (fn [d & args] (if (== (count args) 1)
                     (let [f (first (first args))
                           fd (second (first args))]
                       (Negate (Divide fd (Square f))))
                     (let [f (first args)
                           s (apply Multiply (map (partial first) (rest args)))]
                       (Divide
                         (Subtract (Multiply (second f) s)
                                   (Multiply (diff s d) (first f)))
                         (Multiply s s)))))))

(def Pow-const
  (Operator-factory
    "pow" #(Math/pow %1 %2)
    (fn [_ a b] (let [f (first a)
                      fd (second a)
                      s (second b)]
                  (Multiply s (Pow-const f (Subtract s 1)) fd)))))

; :NOTE: Упростить
(def ArithMean
  (Operator-factory
    "arith-mean" arith-mean
    (fn [_ & args] (Divide (apply Add (mapv (partial second) args))
                           (Constant (count args))))))

(def GeomMean
  (Operator-factory
    "geom-mean" geom-mean
    (fn [d & args] (let [f (mapv #(first %) args)]
                     (Multiply (Divide One (Constant (count args)))
                             (Pow-const (apply GeomMean f) (Constant (- 1 (count args))))
                             (diff (Abs (apply Multiply f)) d))))))

(def HarmMean
  (Operator-factory
    "harm-mean" harm-mean
    (fn [_ & args] (Multiply
                     (Constant (count args))
                     (Divide (apply Add (mapv #(Divide (second %) (Multiply (first %) (first %) )) args))
                             (Pow-const (apply Add (mapv #(Divide One (first %)) args)) (Constant 2)))))))

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