(ns parser)
(require ['clojure.string :as 'str])


(defn inflate-file
      "Output is a vector containing all file lines"
      [file]
      (str/split-lines (slurp file)))

(defn strip-file
      "Get the facts/rules from an array. Output is a tuple of {[facts], [rules]}"
      [file-lines]
      (do
            (def facts [])
            (def rules [])
            (doseq [line file-lines]
                    (if (boolean (re-find #" :- " line))
                      (def rules (conj rules line))
                      (def facts (conj facts line))))))

(defn parse
      "Parse a given database file into memory"
      [database]
      (let [file-lines (inflate-file database)]
           )) ;; Create local scope variable with all the file lines