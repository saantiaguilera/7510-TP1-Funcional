(ns rule)
(require ['clojure.string :refer :all])
(require ['statement :as 'statement])

(defn parse
      "Parses a line of type \"statement :- statement...\", into {statement: {statement}, conditions: [{statement}]}"
      [line]
      (hash-map
        :statement (statement/parse (get (split line #" :- ") 0))
        :conditions (let [non-parsed-conditions (split (replace (get (split line #" :- ") 1) #"\)," ");") #";\s*")]
                         (for [item non-parsed-conditions] (statement/parse item)))
      ))