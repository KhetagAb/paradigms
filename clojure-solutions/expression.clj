(defn operator [fun]
  (fn [& args] (fn [vars] (apply fun (mapv #(% vars) args)))))

(def constant constantly)
(defn variable [var] (fn [vars] (vars var)))

(defn _div [f & args] (/ (double f) (apply * args)))
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

(def Add
  (Operator-factory
    "+" +
    (fn [_ & args] (apply Add (map (partial second) args)))))

(def Subtract
  (Operator-factory
    "-" -
    (fn [_ & args] (apply Subtract (map (partial second) args)))))

(declare Divide)
(def Multiply
  (Operator-factory
    "*" *
    (fn [_ & args]
      (let [prod (apply Multiply (map (partial first) args))]
        (apply Add (map #(Multiply prod (Divide (second %) (first %))) args))))))

(def Divide
  (Operator-factory
    "/" _div
    (fn [d & args] (let [f (first args)
                         s (apply Multiply (map (partial first) (rest args)))]
                     (Divide
                       (Subtract (Multiply (second f) s)
                                 (Multiply (diff s d) (first f)))
                       (Multiply s s))))))

(def Negate
  (Operator-factory
    "negate" -
    (fn [_ arg] (Negate (get arg 1)))))

(def parseObject
  (parser Constant
          {"x" (Variable "x")
           "y" (Variable "y")
           "z" (Variable "z")}
          {'+ Add
           '- Subtract
           '* Multiply
           '/ Divide
           'negate Negate}))