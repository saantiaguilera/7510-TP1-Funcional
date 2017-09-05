(ns number-database-test
    (:require [clojure.test :refer :all]
      [logical-interpreter :refer :all]))

(deftest number-database-fact-test
         (testing "add(one, one, two) should be true"
                  (is (= (evaluate-query "./res/test_numbers_database.txt" "add(one, one, two)")
                         true)))
         (testing "add(two, one, one) should be false"
                  (is (= (evaluate-query "./res/test_numbers_database.txt" "add(two, one, one)")
                         false))))

(deftest number-database-rule-test
         (testing "subtract(one, one, two) should be false"
                  (is (= (evaluate-query "./res/test_numbers_database.txt" "subtract(one, one, two)")
                         false)))
         (testing "subtract(two, one, one) should be true"
                  (is (= (evaluate-query "./res/test_numbers_database.txt" "subtract(two, one, one)")
                         true))))