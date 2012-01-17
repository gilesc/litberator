(ns litberator
  (:import
   [java.nio.channels Channels]
   [java.io FileOutputStream File BufferedReader])
  (:require
   [litberator.pubmed :as pubmed]
   [litberator.handlers :as handlers])
  (:use
   [clojure.tools.logging :only (info error)]
   [clojure.tools.cli :only (cli)]
   [clojure.java.io :only (file copy)]))

(def ^{:dynamic true} *db* nil)

(defn download [strm target]
  (let [rbc (Channels/newChannel strm)
        fos (FileOutputStream. target)]
    (.transferFrom (.getChannel fos)
                   rbc 0 16777216)))

(defn download-article [article]
  (try
    (if-let [strm (handlers/pdf-stream article)]
      (let [segments (.split (:doi article) "/")
            prefix (apply str (interpose "/" (drop-last segments)))
            suffix (last segments)
            target-dir (File. *db* prefix)
            target (File. target-dir (str suffix ".pdf"))]
        (if (and (.exists target)
                 (not (zero? (.getTotalSpace target))))
          (info (:doi article) "already in database.")
          (do
            (.mkdirs target-dir)
            (copy strm target)
            (.close strm)
            (info (:doi article) "successfully downloaded.")))))
    (catch Exception e
      (error e))))

(defn human-readable-file [article]
  (let [aname  (if (:title article)
                 (str (:year article "0000") " -- "(:title article))
                 (:doi article))]
    (str aname
         (if-not (.endsWith aname ".") ".")
         "pdf")))

(defn -main [& args]
  (let [[conf remaining help]
        (cli args
             ["-n" "--number" "Max number of PDFs to download."
              :parse-fn #(Integer. %)
              :default 50]
             ["-c" "--count"
              (str "Instead of downloading PDFs, just output the"
                   " number that would have been downloaded.")
              :flag true]
             ["-d" "--database"
              "Location of the cache of previously downloaded PDFs."
              :default (or (.get (System/getenv) "LITBERATOR_DB")
                           "/tmp/litberator")])]
    (.mkdirs (file (:database conf)))
    (binding [*db* (file (:database conf))]
      (let [qry (first remaining)
            articles (pubmed/query qry :n (:n conf))]
        (dorun (pmap download-article articles))
        (if-let [target-dir (second remaining)]
          (doseq [article articles
                  :let [src (File. *db* (str (:doi article) ".pdf"))]]
            (copy src
                  (File. target-dir
                         (human-readable-file article)))))))))