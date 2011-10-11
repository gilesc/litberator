(ns litberator
  (:import
   [java.nio.channels Channels]
   [java.io FileOutputStream File])
  (:use
   [litberator bibtex handlers])
  (:require
   [clojure.contrib.io :as io]))

(defn download-article [article target-dir]
  (if-let [strm (pdf-stream article)]
    (let [target (or (:title article) (:doi article))
          rbc (Channels/newChannel strm)
          fos (FileOutputStream. (File. target-dir
                                        (.replace (str target ".pdf") "/" "-")))]
      (.transferFrom (.getChannel fos)
                     rbc 0 16777216))))

(defn usage []
  (println
   "USAGE: litberator [-d|-p] dois-or-pmids.txt target-dir"))

(defn -main [flag input-file target-dir]
  (assert (.exists (File. target-dir))) ;;"Target directory does not exist. Create it and try again."
  (assert (= flag "-d")) ;;"Only DOI accessions (-d flag) currently implemented."
  (doseq [accession (io/read-lines input-file)]
    (try
      (if (download-article {:doi accession} target-dir)
        (println (:doi accession) " successfully downloaded."))
      (catch Exception e
        (println e)))))
