(ns litberator.pubmed
  (:use
   clojure.contrib.def
   [clojure.xml :only [parse]]
   [clojure.zip :only [xml-zip node]]
   [clojure.java.io :only [input-stream]])
  (:require
   [clojure.contrib.zip-filter.xml :as zf]
   [clojure.contrib.string :as string]))

(defn- ncbi-query [method & kwargs]
  (xml-zip
   (parse
    (input-stream
     (format "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/%s.fcgi?db=pubmed&retmode=xml&usehistory=y&%s"
             (name method)
             (string/join "&"
                          (for [[k v] (partition 2 kwargs)]
                            (str (name k) "=" v))))))))


(defnk esearch [qry :n 10]
  (let [zipper (ncbi-query :esearch :term qry :retmax n)]
    {:env (zf/xml1-> zipper :WebEnv zf/text)
     :key (zf/xml1-> zipper :QueryKey zf/text)}))

(defn efetch [key env]
  (let [zipper (zf/xml-> (ncbi-query :efetch :query_key key :WebEnv env) :PubmedArticle)]
    {:t (zf/xml-> zipper :MedlineCitation :Article :ArticleTitle zf/text)
     :d (zf/xml-> zipper :PubmedData :ArticleIdList :ArticleId (zf/attr= "IdType" "doi") zf/text)}))

(defn query [qry]
  (let [env (esearch query)]
    (efetch env)))
