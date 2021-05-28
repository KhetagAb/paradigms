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

(defn expression-parser [constant variables operators]
  (letfn [(parse-expr [exps]
            (cond
              (list? exps) (apply (operators (first exps)) (mapv parse-expr (rest exps)))
              (number? exps) (constant exps)
              :else (variables (str exps))))]
    (fn [input]
      (parse-expr (read-string input)))))

(def parseFunction
  (expression-parser constant
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
(def toStringInfix (method :toString-infix))
(def _value (field :value))

(defn expression-proto [evaluate diff toString toString-infix]
  {:evaluate evaluate
   :diff diff
   :toString toString
   :toString-infix toString-infix})

(defn value-operator-factory [f evaluate diff toString]
  (constructor
      (fn [this value] (assoc this :value (f value)))
      (expression-proto evaluate diff toString toString)))

(declare Zero)
(def Constant
  (value-operator-factory double
                          (fn [this _] (_value this))
                          (fn [_ _] Zero)
                          (fn [this] (str (_value this)))))

(def Zero (Constant 0))
(def One (Constant 1))

(def Variable (letfn [(var-name [this] (clojure.string/lower-case (first (_value this))))]
                (value-operator-factory str
                                        (fn [this vars] (vars (var-name this)))
                                        (fn [this var] (if (= var (var-name this))
                                                         One
                                                         Zero))
                                        (fn [this] (str (_value this))))))

(def Operator-prototype
  (let [_args (field :args)
        _symbol (field :symbol)
        _operate (field :operate)
        _diff-impl (field :diff-impl)
        p-spaced (fn [inp] (str " " inp))
        s-spaced (fn [inp] (str inp " "))
        to-string (fn [this rec begin join end]
                    (str "(" begin (clojure.string/join (p-spaced join) (mapv (partial rec) (_args this))) end ")"))]
    (expression-proto
      (fn [this vars]
        (apply (_operate this)
               (mapv #(evaluate % vars) (_args this))))
      (fn [this var]
        (let [args (_args this)]
          ((_diff-impl this) args (mapv #(diff % var) args))))
      (fn [this]
        (let [symbol (_symbol this)]
          (to-string this toString (s-spaced symbol) "" "")))
      (fn [this]
        (let [[a & args] (_args this)
              symbol (_symbol this)]
          (if (empty? args)
            (str symbol "(" (toStringInfix a) ")")
            (to-string this toStringInfix "" (s-spaced symbol) "")))))))

(defn Operator-factory [symbol operate diff-impl]
  (constructor
    (fn [this & args] (assoc this :args args))
    {:prototype Operator-prototype
     :symbol symbol
     :operate operate
     :diff-impl diff-impl}))

(def Add
  (Operator-factory
    "+" +
    (fn [_ dargs] (apply Add dargs))))

(def Subtract
  (Operator-factory
    "-" -
    (fn [_ dargs] (apply Subtract dargs))))

(declare Multiply)
(defn diff-rule-mul [args dargs]
  (second (reduce
            (fn [[f fd] [s sd]]
              [(Multiply f s)
               (Add (Multiply fd s) (Multiply f sd))]) (mapv vector args dargs))))

(def Multiply (Operator-factory "*" * diff-rule-mul))

(def Square
  (Operator-factory
    "square" square
    (fn [arg darg] (Multiply (Constant 2) (first arg) (first darg)))))

(def Negate
  (Operator-factory
    "negate" -
    (fn [_ darg] (Negate (first darg)))))

(def Divide
  (Operator-factory
    "/" _div
    (fn [[a & as] [d & ds]]
      (if (empty? as)
        (Negate (Divide d (Square a)))
        (let [m (apply Multiply as)]
          (Divide
            (Subtract (Multiply m d)
                      (Multiply (diff-rule-mul as ds) a))
            (Multiply m m)))))))

(def ArithMean
  (Operator-factory
    "arith-mean" arith-mean
    (fn [_ dargs] (Divide (apply Add dargs)
                             (Constant (count dargs))))))

(def GeomMean
  (Operator-factory
    "geom-mean" geom-mean
    (fn [args dargs] (Multiply
                       (Constant (/ (count args)))
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
  (expression-parser Constant
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

; HOMEWORK №11
(load-file "parser.clj")

(defn to-bool [a] (if (pos? a) 1 0))
(defn bitwise [operator] (fn [& args] (apply operator (map to-bool args))))

(defn Bitwise-factory [symbol operate]
  (Operator-factory symbol (bitwise operate) (constantly (delay (assert false "Bitwise operations don't support diff.")))))

(def And (Bitwise-factory "&&" bit-and))
(def Or (Bitwise-factory "||" bit-or))
(def Xor (Bitwise-factory "^^" bit-xor))
(def Impl (Bitwise-factory "->" #(bit-or (bit-xor %1 1) %2)))
(def Iff  (Bitwise-factory "<->" #(bit-xor (bit-xor %1 %2) 1)))

(def *all-chars (mapv char (range 0 128)))
(defn *char-by-pred [pred] (+char (apply str (filter #(pred %) *all-chars))))

(def *space (*char-by-pred #(Character/isWhitespace %)))
(def *ws (+ignore (+star *space)))

(def *digit (*char-by-pred #(Character/isDigit %)))
(def *number (+seqf (comp read-string str)
                    (+opt (+char "-"))
                    (+str (+plus *digit))
                    (+str (+opt (+seq (+char ".") (+plus *digit))))))

(defn +word [word]
  (+str (apply +seq (map (partial (comp +char str)) (str word)))))
(defn +by-map [_map]
  (apply +or (map (fn [word] (+map _map (+word word))) (keys _map))))

(def *variable (+map Variable (+str (+plus (+char "xyzXYZ")))))
(def *constant (+map Constant *number))

(defn assoc-by-fun [fun [f & args]]
  (reduce (fn [left [operator right]] (fun operator left right)) f (partition 2 args)))

(defn left-assoc [args] (assoc-by-fun #(%1 %2 %3) args))
(defn right-assoc [args] (assoc-by-fun #(%1 %3 %2) (reverse args)))

(def *binary-operators [[{"*" Multiply "/" Divide} left-assoc]
                        [{"+" Add "-" Subtract} left-assoc]
                        [{"&&" And} left-assoc]
                        [{"||" Or} left-assoc]
                        [{"^^" Xor} left-assoc]
                        [{"->" Impl} right-assoc]
                        [{"<->" Iff} left-assoc]])

(def *unary-operators [{"negate" Negate}])

(declare *parse-expression)
(def *parse-value (+or
                    (+or (+seqf (fn [oper expr] (oper expr))
                                (apply +or (map +by-map *unary-operators)) *ws (delay *parse-value) *ws))
                    (+seqn 1 (+char "(") *ws (delay *parse-expression) *ws (+char ")") *ws)
                    *constant
                    *variable))

(defn *parse-level [next-level level-operator _assoc]
  (+or (+map (comp _assoc flatten) (+seq
         *ws next-level *ws
         (+star (+seq level-operator *ws (delay next-level) *ws))))
       (delay next-level)))

(def *parse-expression
  (reduce (fn [next-level [oper-map _assoc]]
            (*parse-level next-level (+by-map oper-map) _assoc)) *parse-value *binary-operators))

(defn parseObjectInfix [input]
  ((+parser *parse-expression) input))