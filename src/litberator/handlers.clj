(ns litberator.handlers
  (:use
   [litberator doi]
   clojure.contrib.def)
  (:import
   [java.net HttpURLConnection URL]
   [com.gargoylesoftware.htmlunit WebClient BrowserVersion])
  (:require
   [clojure.contrib.string :as string]))

(def client (WebClient. BrowserVersion/FIREFOX_3_6))
(HttpURLConnection/setFollowRedirects true)

(import 'org.apache.commons.logging.LogFactory)
(import 'java.util.logging.Level)

(.setLevel (.getLogger (LogFactory/getLog "com.gargoylesoftware.htmlunit"))
           Level/OFF)

(defmacro without-js [form]
  `(do
     (.setJavaScriptEnabled client false)
     (let [result# ~form]
       (.setJavaScriptEnabled client true)
       result#)))

(defn url-to-stream [url]
  (.. (.getPage client url) getWebResponse getContentAsStream))

(defn substitute [s substitutions]
  (reduce (fn [s [pattern replace]]
            (.replace s pattern replace))
          s substitutions))

(defn host-root [url]
  (string/join "."
                 (take-last 2
                            (.split (.getHost url) "\\."))))

(defnk url-postfix-pdf-stream [url :postfix ".full.pdf"]
  (url-to-stream 
   (URL. (str (str url) postfix))))

(defnk hyphenate-url-pdf-stream [url :prefix nil]
  "Transforms http://HOST/year/other/identifiers/ into
   http://HOST/content/pdf/[optional prefix]-year-other-identifiers.pdf"
  (url-to-stream
   (URL.
    (string/join "/"
                 ["http:/"
                  (.getHost url)
                  "content/pdf"
                  (str
                   (if prefix
                     (str prefix "-"))
                   (.replace (apply str (rest (.getPath url))) "/" "-")
                   ".pdf")]))))

(defn doi-pdf-stream [article]
  "For sites whose PDFs are named directly after DOIs."
  (url-to-stream
   (URL.
    (string/join "/"
                 ["http:/"
                  (.getHost (:url article))
                  "content/pdf"
                  (str (doi-suffix (:doi article)) ".pdf")]))))


(def pdf-stream* nil)
(defmulti pdf-stream*
  (fn [article]
    (host-root (:url article)))
  :default nil)

(defmethod pdf-stream* nil [_])

(defmethod pdf-stream* "oxfordjournals.org" [article] ;;oxfordjournals
  (url-postfix-pdf-stream (:url article)))

(defmethod pdf-stream* "springerlink.com" [article]
  (url-postfix-pdf-stream (:url article)
                          :postfix "fulltext.pdf"))


(defmethod pdf-stream* "sciencedirect.com" [article]
  (without-js
   (let [start (:url article)
         page (.getPage client start)]
     (.getInputStream
      (.openConnection
       (URL. (.getAttribute (.getElementById page "pdfLink") "pdfurl")))))))

(defmethod pdf-stream* "biomedcentral.com" [article]
  (doi-pdf-stream article))

(defmethod pdf-stream* "nature.com" [article]
  (url-to-stream
   (URL.
    (substitute (str (:url article)) {".html" ".pdf" "full" "pdf"}))))

(defmethod pdf-stream* "sciencemag.org" [article]
  (url-postfix-pdf-stream (:url article)))

(defmethod pdf-stream* "pnas.org" [article]
  (url-postfix-pdf-stream (:url article)))

(defmethod pdf-stream* "genomebiology.com" [article]
  (doi-pdf-stream article))



(defn pdf-stream [article]
  ;;TODO: perhaps other ways of getting URL
  (if-let [url (follow-doi (:doi article))]
    (pdf-stream*
       (assoc article :url url))))


