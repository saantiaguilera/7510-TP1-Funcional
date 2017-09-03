(ns statement)
(require ['clojure.string :refer :all])

(defn parse
      "Parses a line of type fact(params...), into {fact: string, params: [string]}"
      [line]
      (hash-map
        :fact (get (split line #"\(") 0)
        :params (split (get (split (get (split line #"\(") 1) #"\)") 0) #",\s*")
        ))