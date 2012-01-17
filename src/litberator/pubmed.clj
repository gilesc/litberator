(ns litberator.pubmed
  (:use
   clojure.contrib.def
   [clojure.xml :only [parse]]
   [clojure.zip :only [xml-zip node]]
   [clojure.java.io :only [input-stream]])
  (:require
   [clojure.contrib.zip-filter.xml :as zf]
   [clojure.contrib.string :as string]))

(defrecord Article [pmid year title abstract doi])

(defn- ncbi-query [method & kwargs]
  (parse
   (input-stream
    (format "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/%s.fcgi?db=pubmed&retmode=xml&usehistory=y&%s"
            (name method)
            (string/join "&"
                         (for [[k v] (partition 2 kwargs)]
                           (str (name k) "=" v)))))))


(defn esearch [qry]
  (let [zipper (xml-zip (ncbi-query :esearch :term (.replace qry " " "+")))]
    {:env (zf/xml1-> zipper :WebEnv zf/text)
     :key (zf/xml1-> zipper :QueryKey zf/text)}))

(defn efetch [key env n]
  (for [citation (:content (ncbi-query :efetch :query_key key :WebEnv env :retmax n))
        :let [zipper (xml-zip citation)
              article (zf/xml1-> zipper :MedlineCitation :Article)]]
    (Article. (Integer/parseInt (zf/xml1-> zipper :MedlineCitation :PMID zf/text))
              (if-let [year (zf/xml1-> article :Journal :JournalIssue :PubDate :Year zf/text)]
                (Integer/parseInt year))
              (zf/xml1-> article :ArticleTitle zf/text)
              (zf/xml1-> article :Abstract :AbstractText zf/text)
              (zf/xml1-> zipper :PubmedData :ArticleIdList :ArticleId (zf/attr= :IdType "doi") zf/text))))

(defn query [qry &{:keys [n] :or {n 50}}]
  (let [{key :key env :env} (esearch qry)]
    (efetch key env n)))
