(ns logical-interpreter)
(require ['parser :as 'parser])

(defn evaluate-query
      "Returns true if the rules and facts in database imply query, false if not. If
      either input can't be parsed, returns nil"
      [database-file query]
      (try
        (do
          (parser/strip-file (parser/inflate-file database-file))
          (parser/query query))
        (catch Exception functionalException nil)))