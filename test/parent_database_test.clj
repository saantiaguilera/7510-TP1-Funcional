(ns parent-database-test
    (:require [clojure.test :refer :all]
      [logical-interpreter :refer :all]))

(deftest parent-database-fact-test
         (testing "varon(juan) should be true"
                  (is (= (evaluate-query "./res/test_parent_database.txt" "varon(juan)")
                         true)))
         (testing "varon(maria) should be false"
                  (is (= (evaluate-query "./res/test_parent_database.txt" "varon(maria)")
                         false)))
         (testing "mujer(cecilia) should be true"
                  (is (= (evaluate-query "./res/test_parent_database.txt" "mujer(cecilia)")
                         true)))
         (testing "padre(juan, pepe) should be true"
                  (is (= (evaluate-query "./res/test_parent_database.txt" "padre(juan, pepe)")
                         true)))
         (testing "padre(mario, pepe) should be false"
                  (is (= (evaluate-query "./res/test_parent_database.txt" "padre(mario, pepe)")
                         false))))

(deftest parent-database-rule-test
         (testing "hijo(pepe, juan) should be true"
                  (is (= (evaluate-query "./res/test_parent_database.txt" "hijo(pepe, juan)")
                         true)))
         (testing "hija(maria, roberto) should be false"
                  (is (= (evaluate-query "./res/test_parent_database.txt" "hija(maria, roberto)")
                         false))))

(deftest parent-database-empty-query-test
         (testing "varon should be nil"
                  (is (= (evaluate-query "./res/test_parent_database.txt" "varon")
                         nil)))
         (testing "maria should be nil"
                  (is (= (evaluate-query "./res/test_parent_database.txt" "maria")
                         nil)))
         (testing "empty should be nil"
                  (is (= (evaluate-query "./res/test_parent_database.txt" "")
                         nil))))