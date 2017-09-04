(ns parser_test
    (:require [clojure.test :refer :all]
      [parser :refer :all]
      [statement :refer :all])
    (:import (java.io FileNotFoundException)))

(deftest inflate-non-existent-file-returns-empty-vector
         (testing
           "Tests that inflating a non-existent file returns an empty vector (as if there was no lines."
           (is (thrown? FileNotFoundException (inflate-file "./res/asfd.txt")))))

(deftest inflate-existent-file-returns-vector-with-lines
         (testing
           "Tests that inflating an existent file returns a vector with the lines"
           (is (count (inflate-file "./res/test_fact_database.txt")) 7)
           (is (= (get (inflate-file "./res/test_fact_database.txt") 0)) "varon(juan).")
           (is (= (get (inflate-file "./res/test_fact_database.txt") 1)) "varon(pepe).")
           (is (= (get (inflate-file "./res/test_fact_database.txt") 2)) "mujer(maria).")
           (is (= (get (inflate-file "./res/test_fact_database.txt") 3)) "mujer(cecilia).")
           (is (= (get (inflate-file "./res/test_fact_database.txt") 4)) "padre(juan, pepe).")
           (is (= (get (inflate-file "./res/test_fact_database.txt") 5)) "padre(juan, maria).")
           (is (= (get (inflate-file "./res/test_fact_database.txt") 6)) "padre(pepe, cecilia).")
           (is (= (get (inflate-file "./res/test_fact_database.txt") 7)) nil)))

(deftest strip-facts-returns-them-correctly
         (testing
           "Tests that a wellformed vector of lines is stripped correctly into facts"
           (strip-file (inflate-file "./res/test_fact_database.txt"))
           (is (count facts) 7)                             ;; Check that there are 7 facts
           (is (= (:fact (get facts 0)) "varon"))           ;; Check that the first fact 'fact' is 'varon'
           (is (= (:params (get facts 0) ["juan"])))        ;; Check that the first fact 'params' is 'juan'
           (is (count rules) 0)                             ;; Check there are no rules
           (is (= (get rules 0) nil))))                     ;; Check that the first accessed element is indeed nil

(deftest strip-rules-returns-them-correctly
         (testing
           "Tests that a wellformed vector of lines is stripped correctly into rules"
           (strip-file (inflate-file "./res/test_rule_database.txt"))
           (is (count rules) 2)
           (is (= (:fact (:statement (get rules 0))) "hijo")) ;; hijo(X, Y) :- varon(X), padre(Y, X).
           (is (= (:params (:statement (get rules 0))) ["X" "Y"]))
           (is (= (:fact (nth (:conditions (get rules 0)) 0)) "varon"))
           (is (= (:params (nth (:conditions (get rules 0)) 0)) ["X"]))
           (is (= (:fact (nth (:conditions (get rules 0)) 1)) "padre"))
           (is (= (:params (nth (:conditions (get rules 0)) 1)) ["Y" "X"]))))

(deftest test-merge-results-as-or
         (testing
           "Tests combinations for merging a result list with an OR conj"
           (is (= (merge-result-list-as-or [0 0 0 0]) 0))
           (is (= (merge-result-list-as-or [1 1 1 0]) 0))
           (is (= (merge-result-list-as-or [1 1 1 1]) 1))
           (is (= (merge-result-list-as-or [1 1 1 0 1]) 0))))

(deftest test-merge-results-as-and
         (testing
           "Tests combinations for merging a result list with an AND conj"
           (is (= (merge-result-list-as-and [0 0 0 0]) 0))
           (is (= (merge-result-list-as-and [1 1 1 0]) 1))
           (is (= (merge-result-list-as-and [1 1 1 1]) 1))
           (is (= (merge-result-list-as-and [1 1 1 0 1]) 1))))

(deftest test-validate-a-fact-from-the-list-of-facts
         (testing
           "Tests that a fact is valid if its in the list of facts"
           (strip-file (inflate-file "./res/test_fact_database.txt"))
           (is (= (validate-from-facts "padre" ["juan" "pepe"]) 0))
           (is (= (validate-from-facts "mujer" ["maria"]) 0))))

(deftest test-validate-an-invalid-fact-from-the-list-of-facts
         (testing
           "Tests that an invalid fact isnt valid if its not in the list of facts"
           (strip-file (inflate-file "./res/test_fact_database.txt"))
           (is (= (validate-from-facts "invalid" ["juan" "pepe"]) 1))
           (is (= (validate-from-facts "mujer" ["anamarialuz"]) 1))))

(deftest test-validate-a-rule-with-one-condition
         (testing
           "Tests that a valid rule with one condition works"
           (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))
           (is (= (validate-from-rule (rule/parse "algo(param) :- ejemplo(param).") (statement/parse "ejemplo(test).")) 0))))

(deftest test-validate-a-rule-with-multiple-conditions
         (testing
           "Tests that a valid rule with multiple conditions works"
           (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))
           (is (= (validate-from-rule (rule/parse "hijo(X, Y) :- varon(Y), padre(Y, X).") (statement/parse "hijo(pepe, juan).")) 0))
           (is (= (validate-from-rule (rule/parse "hija(X, Y) :- mujer(Y), padre(Y, X).") (statement/parse "hija(pepe, cecilia).")) 0))))

(deftest test-validate-an-invalid-rule-with-multiple-conditions
         (testing
           "Tests that an invalid rule with multiple conditions works"
           (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))
           (is (= (validate-from-rule (rule/parse "hijo(X, Y) :- varon(Y), padre(Y, X).") (statement/parse "hijo(juan, pepe).")) 1))))

(deftest test-validate-an-invalid-rule-with-one-condition
         (testing
           "Tests that an invalid rule with one condition works"
           (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))
           (is (= (validate-from-rule (rule/parse "algo(param) :- ejemplo(param).") (statement/parse "ejemplo(estenoesta).")) 1))))

(deftest test-query-with-a-fact
         (testing
           "Tests that given a query of a fact, the one is found"
           (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))
           (is (= (query "mujer(maria).") true))
           (is (= (query "padre(maria, juan).") true))
           (is (= (query "varon(juan).") true))))

(deftest test-invalid-query-with-a-fact
         (testing
           "Tests that given a query of a fact, the one is found"
           (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))
           (is (= (query "mujer(juana).") false))
           (is (= (query "padre(juana, juan).") false))
           (is (= (query "varon(maria).") false))))

(deftest test-query-with-a-rule
         (testing
           "Tests that given a query from a rule, the one is found"
           (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))
           (is (= (query "hijo(pepe, juan).") true))
           (is (= (query "hija(pepe, cecilia).") true))
           (is (= (query "test(test).") true))))

(deftest test-invalid-query-with-a-rule
         (testing
           "Tests that given an invalid query from a rule, the one is found"
           (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))
           (is (= (query "hijo(marcos, juan).") false))
           (is (= (query "nonexistentrule(pepe, cecilia).") false))
           (is (= (query "test(bad).") false))))