(ns interpreter_test
    (:require [clojure.test :refer :all]
      [interpreter :refer :all])
    (:import (java.io FileNotFoundException)))

(deftest parse-non-existent-file-returns-empty-vector
         (testing
           "Tests that parsing a non-existent file returns an empty vector (as if there was no lines."
           (is (thrown? FileNotFoundException (parse "./res/asfd.txt")))))

(deftest parse-existent-file-returns-vector-with-lines
         (testing
           "Tests that parsing an existent file returns a vector with the lines"
           (is (count (parse "./res/test_database.txt")) 7)
           (is (= (get (parse "./res/test_database.txt") 0)) "varon(juan).")
           (is (= (get (parse "./res/test_database.txt") 1)) "varon(pepe).")
           (is (= (get (parse "./res/test_database.txt") 2)) "mujer(maria).")
           (is (= (get (parse "./res/test_database.txt") 3)) "mujer(cecilia).")
           (is (= (get (parse "./res/test_database.txt") 4)) "padre(juan, pepe).")
           (is (= (get (parse "./res/test_database.txt") 5)) "padre(juan, maria).")
           (is (= (get (parse "./res/test_database.txt") 6)) "padre(pepe, cecilia).")
           (is (= (get (parse "./res/test_database.txt") 7)) nil)))
