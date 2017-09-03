(ns parser)
(require ['clojure.string :as 'str])
(require ['statement :as 'statement])
(require ['rule :as 'rule])

(defn inflate-file
      "Output is a vector containing all file lines"
      [file]
      (str/split-lines (slurp file)))

(defn strip-file
      "Get the facts/rules from an array. Creates 2 global variables, 'facts' and 'rules', both arrays of strings"
      [file-lines]
      (do
            (def facts [])
            (def rules [])
            (doseq [line file-lines]
                    (if (boolean (re-find #" :- " line))
                      (def rules (conj rules (rule/parse line)))
                      (def facts (conj facts (statement/parse line)))))))

(defn merge-result-list-as-or
      "Merges an array of results into a single value using OR. Eg: [1 1 1 0 1 1] -> 0 // [1 1 1] -> 1 // [0 0 0] -> 0"
      [list]
      (if (= ((some #(= 0 %) list)) nil)
      1
      0))

(defn merge-result-list-as-and
      "Merges an array of results into a single value using AND. Eg: [1 1 1 0 1 1] -> 1 // [1 1 1] -> 1 // [0 0 0] -> 0"
      [list]
      (if (= (reduce + list) 0)
        0
        1))

(defn validate-from-facts
      "Validate that a fact exists in the given facts. Returns 0 if matches, 1 otherwise"
      [p-fact p-params]
      (merge-result-list-as-or (for [item facts]
                              (if (and (= p-fact (:fact item)) (= p-params (:params item))))
                              0
                              1)))

(defn validate-from-rule
      "Validate that a statement matching the statement of a given rule is correct or not. Returns 0 if valid, 1 otherwise"
      [rule query-statement]
      ;; First we map 'param into query_param (eg: hijo(padre, hijo) -> padre: juan, hijo: pepe)
      (let [param-map (zipmap (:params (:statement rule)) (:params query-statement))]
           ;; We have now param-map : { padre: juan, hijo: pepe }. We will iterate on each condition and validate it
           ;; Note that the for returns [0/1...], we should merge it as AND since all conditions must met.
           (merge-result-list-as-and
             (for [condition (:conditions rule)]
                  ;; We delegate the validation to another fn. Note that we transform the params with the map prior to invocation
                  (validate-from-facts (:fact condition) (for [param (:params condition)] (get param-map param)))))))

(defn query
      "Do a query from the database"
      [what]
      ;; Build a statement from the query
      (println
        (let [query-statement (statement/parse what)]
             ;; First we check in the rules to find if it matches one
             ;; We do a merge with OR. If we found at least one 0 then it means a rule matched and its true.
             (if (=
                   (merge-result-list-as-or
                     (for [rule rules]
                          (if (= (:fact (:statement rule)) (:fact query-statement))
                            (validate-from-rule rule query-statement)
                            1)))
                   0)
               ;; If the merged result is a 0, then a rule matched.
               "True"
               ;; Else we will simply check in all the facts
               (if (=
                     (validate-from-facts (:fact query-statement) (:params query-statement))
                     0)
                 "True"
                 "False")))))