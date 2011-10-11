(ns litberator
  (:import
   [java.nio.channels Channels]
   [java.io FileOutputStream File])
  (:use
   [litberator bibtex handlers]))

(defn download-article [article target-dir]
  (if-let [strm (pdf-stream article)]
    (let [target (:title article)
          rbc (Channels/newChannel strm)
          fos (FileOutputStream. (File. target-dir (str target ".pdf")))]
      (.transferFrom (.getChannel fos)
                     rbc 0 16777216))))

(defn -main [bibtex-file target-dir]
  (if-not (.exists (File. target-dir))
    (println "Target directory does not exist. Create it and try again.")
    (doseq [article (read-bibtex bibtex-file)]
      (println (:doi article))
      (try
        (if (download-article article target-dir)
          (println (str (:title article) " successfully downloaded.")))
        (catch Exception e
          (println e))))))
