(ns litberator
  (:import
   [java.nio.channels Channels]
   [java.io FileOutputStream File BufferedReader])
  (:require
   [litberator.pubmed :as pubmed]
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

(defn -main [& args]
  (with-command-line args
    "XYZ"
    [[n "Max number of PDFs to download." "50"]
     [c? count? "Instead of downloading PDFs, just count and output the number of PDFs that would be downloaded."]
     remaining]
    (if-not (= (count remaining) 2)
      (usage)
      (let [[qry target] remaining
            articles (pubmed/query qry :n (Integer/parseInt n))]
        (if c?
          (println (count (set (map :doi articles))))
          (download-articles articles target))))))
