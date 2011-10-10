(ns litberator
  (:import
   [java.net HttpURLConnection URL]
   [java.nio.channels Channels]
   [java.io FileOutputStream File]
   [com.gargoylesoftware.htmlunit WebClient BrowserVersion])
  (:require
   [clojure.contrib.string :as string]))

(def client (WebClient. BrowserVersion/FIREFOX_3_6))
(HttpURLConnection/setFollowRedirects true)

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
                        2 1)))]))))

(defn follow-doi [doi]
  (let [conn (.openConnection (java.net.URL. (str "http://dx.doi.org/" doi)))]
    (.getResponseMessage conn)
    (.getURL conn)))

(defn doi-prefix [doi]
  (if doi
    (first (.split doi "/" 2))))

(defn url-to-stream [url]
  (.. (.getPage client url) getWebResponse getContentAsStream))

(def pdf-stream nil)
(defmulti pdf-stream
  #(doi-prefix (:doi %))
  :default nil)
(defmethod pdf-stream nil [_])

(defmethod pdf-stream "10.1093" [article] ;;oxfordjournals
  (url-to-stream
   (URL.
    (string/join "/"
                 ["http:/"
                  (.getHost (follow-doi (:doi article)))
                  "content"
                  (:volume article)
                  (:issue article)
                  (str (first (.split (:pages article) "--")) ".full.pdf")]))))

(defmethod pdf-stream "10.1007" [article] ;;nature & springerlink
  (let [url (follow-doi (:doi article))]
    (if (.equals (.getHost url) "www.springerlink.com")
      (url-to-stream (URL. (str url "fulltext.pdf"))))))
;;TODO: the ones actually on the nature website

(defmacro without-js [form]
  `(do
     (.setJavaScriptEnabled client false)
     (let [result# ~form]
       (.setJavaScriptEnabled client true)
       result#)))

(defmethod pdf-stream "10.1016" [article]
  ;; sciencedirect
  ;; right now throws an error if you try to access an article w/o subscription
  (without-js
   (let [start (follow-doi (:doi article))
         page (.getPage client start)]
     (.getInputStream
      (.openConnection
       (URL. (.getAttribute (.getElementById page "pdfLink") "pdfurl")))))))

(defn download-article [article]
  (if-let [strm (pdf-stream article)]
    (let [target (:title article)
          rbc (Channels/newChannel strm)
          fos (FileOutputStream. (File. "test" (str target ".pdf")))]
      (.transferFrom (.getChannel fos)
                     rbc 0 16777216))))
