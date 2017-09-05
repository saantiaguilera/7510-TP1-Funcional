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
           (def test_facts (:facts (strip-file (inflate-file "./res/test_fact_database.txt"))))
           (def test_rules (:rules (strip-file (inflate-file "./res/test_fact_database.txt"))))
           (is (count test_facts) 7)                             ;; Check that there are 7 facts
           (is (= (:fact (get test_facts 0)) "varon"))           ;; Check that the first fact 'fact' is 'varon'
           (is (= (:params (get test_facts 0) ["juan"])))        ;; Check that the first fact 'params' is 'juan'
           (is (count test_rules) 0)                             ;; Check there are no rules
           (is (= (get test_rules 0) nil))))                     ;; Check that the first accessed element is indeed nil

(deftest strip-rules-returns-them-correctly
         (testing
           "Tests that a wellformed vector of lines is stripped correctly into rules"
           (def test_rules (:rules (strip-file (inflate-file "./res/test_rule_database.txt"))))
           (is (count test_rules) 2)
           (is (= (:fact (:statement (get test_rules 0))) "hijo")) ;; hijo(X, Y) :- varon(X), padre(Y, X).
           (is (= (:params (:statement (get test_rules 0))) ["X" "Y"]))
           (is (= (:fact (nth (:conditions (get test_rules 0)) 0)) "varon"))
           (is (= (:params (nth (:conditions (get test_rules 0)) 0)) ["X"]))
           (is (= (:fact (nth (:conditions (get test_rules 0)) 1)) "padre"))
           (is (= (:params (nth (:conditions (get test_rules 0)) 1)) ["Y" "X"]))))

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
           (is (= (validate-from-facts "padre"
                                       ["juan" "pepe"]
                                       (strip-file (inflate-file "./res/test_fact_database.txt"))) 0))
           (is (= (validate-from-facts "mujer"
                                       ["maria"]
                                       (strip-file (inflate-file "./res/test_fact_database.txt"))) 0))))

(deftest test-validate-an-invalid-fact-from-the-list-of-facts
         (testing
           "Tests that an invalid fact isnt valid if its not in the list of facts"
           (is (= (validate-from-facts "invalid"
                                       ["juan" "pepe"]
                                       (strip-file (inflate-file "./res/test_fact_database.txt"))) 1))
           (is (= (validate-from-facts "mujer"
                                       ["anamarialuz"]
                                       (strip-file (inflate-file "./res/test_fact_database.txt"))) 1))))

(deftest test-validate-a-rule-with-one-condition
         (testing
           "Tests that a valid rule with one condition works"
           (is (= (validate-from-rule (rule/parse "algo(param) :- ejemplo(param).")
                                      (statement/parse "ejemplo(test).")
                                      (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))) 0))))

(deftest test-validate-a-rule-with-multiple-conditions
         (testing
           "Tests that a valid rule with multiple conditions works"
           (is (= (validate-from-rule (rule/parse "hijo(X, Y) :- varon(Y), padre(Y, X).")
                                      (statement/parse "hijo(pepe, juan).")
                                      (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))) 0))
           (is (= (validate-from-rule (rule/parse "hija(X, Y) :- mujer(Y), padre(Y, X).")
                                      (statement/parse "hija(pepe, cecilia).")
                                      (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))) 0))))

(deftest test-validate-an-invalid-rule-with-multiple-conditions
         (testing
           "Tests that an invalid rule with multiple conditions works"
           (is (= (validate-from-rule (rule/parse "hijo(X, Y) :- varon(Y), padre(Y, X).")
                                      (statement/parse "hijo(juan, pepe).")
                                      (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))) 1))))

(deftest test-validate-an-invalid-rule-with-one-condition
         (testing
           "Tests that an invalid rule with one condition works"
           (is (= (validate-from-rule (rule/parse "algo(param) :- ejemplo(param).")
                                      (statement/parse "ejemplo(estenoesta).")
                                      (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))) 1))))

(deftest test-query-with-a-fact
         (testing
           "Tests that given a query of a fact, the one is found"
           (let [data (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))]
                (do
                  (is (= (query "mujer(maria)." data) true))
                  (is (= (query "padre(maria, juan)." data) true))
                  (is (= (query "varon(juan)." data) true))))))

(deftest test-invalid-query-with-a-fact
         (testing
           "Tests that given a query of a fact, the one is found"
           (let [data (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))]
                (do
                  (is (= (query "mujer(juana)." data) false))
                  (is (= (query "padre(juana, juan)." data) false))
                  (is (= (query "varon(maria)." data) false))))))

(deftest test-query-with-a-rule
         (testing
           "Tests that given a query from a rule, the one is found"
           (let [data (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))]
                (do
                  (is (= (query "hijo(pepe, juan)." data) true))
                  (is (= (query "hija(pepe, cecilia)." data) true))
                  (is (= (query "test(test)." data) true))))))


(deftest test-invalid-query-with-a-rule
         (testing
           "Tests that given an invalid query from a rule, the one is found"
           (let [data (strip-file (inflate-file "./res/test_fact_and_rule_database.txt"))]
                (do
                  (is (= (query "hijo(marcos, juan)." data) false))
                  (is (= (query "nonexistentrule(pepe, cecilia)." data) false))
                  (is (= (query "test(bad)." data) false))))))
