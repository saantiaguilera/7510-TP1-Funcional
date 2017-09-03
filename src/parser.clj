(ns parser)
(require ['clojure.string :as 'str])
(require ['statement :as 'statement])


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
                      (def rules (conj rules line))
                      (def facts (conj facts (statement/parse line)))))))