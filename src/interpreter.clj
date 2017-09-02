(ns interpreter)
(require ['clojure.string :as 'str])


(defn inflate-file
      "Inflates into memmory a file. Output is a vector containing all file lines"
      [file]
      (str/split-lines (slurp file)))

(defn parse
      "Parse a given database file into memory"
      [database]
      (inflate-file database) ;; Create local scope variable with all the file lines
      )