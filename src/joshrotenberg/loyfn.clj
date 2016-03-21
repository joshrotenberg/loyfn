(ns joshrotenberg.loyfn
  (:require [clojure.string :refer [join]]
            [clojure.walk :refer [stringify-keys]])
  (:require [me.raynes.conch :refer [programs with-programs let-programs] :as sh])
  (:refer-clojure :exclude [name class]))

(defmulti proc-param (fn [a] (class (:value a))))
(defmethod proc-param java.lang.String [a] ((juxt :name :value) a)) ;; --master local[*]
(defmethod proc-param java.lang.Boolean [a] (:name a)) ;; --verbose
(defmethod proc-param clojure.lang.Associative [a]
  (map #(vector (:name a) (join "=" [(first %) (second %)])) (stringify-keys (:value a))))
(defmethod proc-param clojure.lang.Sequential [a]
  (vector (:name a) (join "," (flatten (:value a))))) ; --jars "foo.jar,bar.jar"
(defmethod proc-param :default [a] ((juxt :name :value) a))
(prefer-method proc-param clojure.lang.Sequential clojure.lang.Associative)

(defn master
  [url]
  {:name "--master" :value url})

(defn deploy-mode
  [mode]
  {:name "--deploy-mode" :value mode})

(defn class
  [class]
  {:name "--class" :value class})

(defn verbose
  []
  {:name "--verbose" :value true})

(defn submit
  [& args]
  (map proc-param args))



