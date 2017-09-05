(ns incomplete-database-test
    (:require [clojure.test :refer :all]
      [logical-interpreter :refer :all]))

(deftest incomplete-database-fact-test
         (testing "varon(juan) should be nil"
                  (is (= (evaluate-query "./res/test_incomplete_database.txt" "varon(juan)")
                         nil)))
         (testing "varon(maria) should be nil"
                  (is (= (evaluate-query "./res/test_incomplete_database.txt" "varon(maria)")
                         nil)))
         (testing "mujer(cecilia) should be nil"
                  (is (= (evaluate-query "./res/test_incomplete_database.txt" "mujer(cecilia)")
                         nil)))
         (testing "padre(juan, pepe) should be nil"
                  (is (= (evaluate-query "./res/test_incomplete_database.txt" "padre(juan, pepe)")
                         nil)))
         (testing "padre(mario, pepe) should be nil"
                  (is (= (evaluate-query "./res/test_incomplete_database.txt" "padre(mario, pepe)")
                         nil))))

(deftest incomplete-database-rule-test
         (testing "hijo(pepe, juan) should be nil"
                  (is (= (evaluate-query "./res/test_incomplete_database.txt" "hijo(pepe, juan)")
                         nil)))
         (testing "hija(maria, roberto) should be nil"
                  (is (= (evaluate-query "./res/test_incomplete_database.txt" "hija(maria, roberto)")
                         nil))))