(ns litberator.bibtex)

;; Horrible hack and will likely fail on 99% of BibTeX files
(defn read-bibtex [file]
  (for [record (.split (slurp file) "\n\n")
        :let [lines (drop-last 1
                               (drop 1
                                     (.split record "\n")))]]
    (into {}
          (for [line lines
                :let [[k v] (.split line "=" 2)]]
            [(keyword (.trim k))
             (subs v 2
                   (- (count v)
                      (if (.endsWith v ",")
                        4 3)))]))))
