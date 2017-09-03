(ns statement_test
    (:require [clojure.test :refer :all]
      [statement :refer :all]))

(deftest empty-statement
         (testing
           "Tests parsing an empty statement"
           (is (thrown? NullPointerException (parse "")))))

(deftest no-fact-statement
         (testing
           "Tests parsing a statement with no params (eg: ())"
           (is (thrown? NullPointerException (parse "()")))))

(deftest no-params-statement
         (testing
           "Tests parsing a statement with no params (eg: varon())"
           (is (thrown? NullPointerException (parse "varon()")))))

(deftest full-statement
         (testing
           "Tests parsins a correct statement."
           (is (= (:fact (parse "padre(juan, pepe)")) "padre"))
           (is (= (:params (parse "padre(juan, pepe)")) ["juan", "pepe"]))
           (is (= (:params (parse "amigos(juan, pepe, pepa, pupo)")) ["juan", "pepe", "pepa", "pupo"]))))
