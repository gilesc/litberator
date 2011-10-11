(ns litberator.test
  (:use
   clojure.test
   litberator.handlers))

(def articles
  {:sciencemag.org {:doi "10.1126/science.1133420"}
   :pnas.org {:doi "10.1073/pnas.0702636104"}
   :nature.com {:doi "10.1038/msb4100112"}
   :oxfordjournals.org  {:doi "10.1093/nar/gkh407"}
   :genomebiology.org {:doi "10.1186/gb-2001-2-12-research0051"}
   :biomedcentral.com {:doi "10.1186/1471-2105-4-64"}
   :springerlink.com {:doi "10.1007/s001099900059"}
   :sciencedirect.com {:doi "10.1016/j.sbi.2004.05.003"}})

(deftest test-pdf-stream
  (doseq [[k article] articles]
    (is (let [strm (pdf-stream article)]
          (and strm
               (isa? (class (pdf-stream article)) java.io.InputStream)))
        (str k))))


