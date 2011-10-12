(ns litberator
  (:import
   [java.nio.channels Channels]
   [java.io FileOutputStream File BufferedReader])
  (:require
   [litberator.bibtex :as bibtex]
   [litberator.handlers :as handlers]
   [clojure.contrib.io :as io])
  (:use
   clojure.contrib.command-line))

(defn download [strm target]
  (let [rbc (Channels/newChannel strm)
        fos (FileOutputStream. target)]
    (.transferFrom (.getChannel fos)
                   rbc 0 16777216)))

(defn download-article [article target-dir]
  (if-let [strm (handlers/pdf-stream article)]
    (let [target (if (:title article)
                   (str (:year article "0000") " -- "(:title article))
                   (:doi article))]
      (download strm
                (File. target-dir (.replace (str target ".pdf") "/" "-")))
      (println target " successfully downloaded."))))

(defn download-articles [articles target-dir]
  (dorun
   (pmap
    (fn [article]
      (try
       (download-article article target-dir)
       (catch Exception e nil)))
    articles)))

(defn usage []
  (println
   "USAGE: TODO")
  (System/exit 1))

(defn query [qry target n]
  (println "NOT IMPLEMENTED"))
(defn pmid [lines target n]
  (println "NOT IMPLEMENTED"))
(defn doi [lines target n]
  (println "NOT IMPLEMENTED"))
(defn bibtex [lines target n]
  (let [articles (bibtex/read-bibtex lines)]
    (download-articles (take n articles) target)))


(defn -main [& args]
  (let [method (resolve (symbol "litberator" (first args)))]
    (with-command-line (rest args)
      "XYZ"
      [[n "Max number of PDFs to download." "50"]
       remaining]
      (if (= method query)
        (apply query remaining [(Integer/parseInt n)])
        (case (count remaining)
          1 (method (line-seq (BufferedReader. *in*)) (first remaining) (Integer/parseInt n))
          2 (method (io/read-lines (first remaining)) (second remaining) (Integer/parseInt n))
          :default (usage))))))
