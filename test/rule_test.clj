(ns rule_test
    (:require [clojure.test :refer :all]
      [rule :refer :all]))

(deftest statement-fact-is-parsed-correctly
         (testing
           "Tests parsing a rule parses the fact of the statement correctly"
           (is (= (:fact (:statement (parse "hijo(padre, hijo) :- varon(padre), varon(hijo)."))) "hijo"))))

(deftest statement-params-are-parsed-correctly
         (testing
           "Tests parsing a rule parses the params of the statement correctly"
           (is (= (:params (:statement (parse "hijo(padre, hijo) :- varon(padre), varon(hijo)."))) ["padre", "hijo"]))))

(deftest conditions-are-splitted-correctly
         (testing
           "Tests parsing a rule splits the conditions correctly"
           (is (= (count (:conditions (parse "hijo(padre, hijo) :- varon(padre), varon(hijo)."))) 2))))

(deftest conditions-facts-are-parsed-correctly
         (testing
           "Tests parsing a rule parses the facts of the params correctly"
           (is (= (:fact (nth (:conditions (parse "hijo(padre, hijo) :- varon(padre), varon(hijo).")) 0)) "varon"))
           (is (= (:fact (nth (:conditions (parse "hijo(padre, hijo) :- varon(padre), varon(hijo).")) 1)) "varon"))))

(deftest conditions-params-are-parsed-correctly
         (testing
           "Tests parsing a rule parses the params of the params correctly"
           (is (= (:params (nth (:conditions (parse "hijo(padre, hijo) :- varon(padre), varon(hijo).")) 0)) ["padre"]))
           (is (= (:params (nth (:conditions (parse "hijo(padre, hijo) :- varon(padre), varon(hijo).")) 1)) ["hijo"]))))