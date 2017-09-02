(ns parser_test
    (:require [clojure.test :refer :all]
      [parser :refer :all])
    (:import (java.io FileNotFoundException)))

(deftest inflate-non-existent-file-returns-empty-vector
         (testing
           "Tests that inflating a non-existent file returns an empty vector (as if there was no lines."
           (is (thrown? FileNotFoundException (inflate-file "./res/asfd.txt")))))

(deftest inflate-existent-file-returns-vector-with-lines
         (testing
           "Tests that inflating an existent file returns a vector with the lines"
           (is (count (inflate-file "./res/test_database.txt")) 7)
           (is (= (get (inflate-file "./res/test_database.txt") 0)) "varon(juan).")
           (is (= (get (inflate-file "./res/test_database.txt") 1)) "varon(pepe).")
           (is (= (get (inflate-file "./res/test_database.txt") 2)) "mujer(maria).")
           (is (= (get (inflate-file "./res/test_database.txt") 3)) "mujer(cecilia).")
           (is (= (get (inflate-file "./res/test_database.txt") 4)) "padre(juan, pepe).")
           (is (= (get (inflate-file "./res/test_database.txt") 5)) "padre(juan, maria).")
           (is (= (get (inflate-file "./res/test_database.txt") 6)) "padre(pepe, cecilia).")
           (is (= (get (inflate-file "./res/test_database.txt") 7)) nil)))

(deftest strip-facts-and-rules-returns-them-correctly
         (testing
           "Tests that a wellformed vector of lines is stripped correctly into facts and rules"
           (strip-file (inflate-file "./res/test_database.txt"))
           (is (count facts) 7)
           (is (= (get facts 0) "varon(juan)."))
           (is (count rules) 0)
           (is (= (get rules 0) nil))))