(ns litberator.doi
  (:import
   [java.net URL]))

(defn follow-doi [doi]
  (let [conn (.openConnection (URL. (str "http://dx.doi.org/" doi)))]
    (.getResponseMessage conn)
    (.getURL conn)))

(defn doi-prefix [doi]
  (if doi
    (first (.split doi "/" 2))))

(defn doi-suffix [doi]
  (if doi
    (second (.split doi "/" 2))))
