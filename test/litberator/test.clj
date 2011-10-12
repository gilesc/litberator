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
   :sciencedirect.com {:doi "10.1016/j.sbi.2004.05.003"}
   :plosone.org {:doi "10.1371/journal.pone.0009576"}
   :ploscompbiol.org {:doi "10.1371/journal.pcbi.1000788"}
   :wiley.com {:doi "10.1111/j.1365-2605.2009.00996.x"}
   :aacrjournals.org {:doi "10.1158/0008-5472.CAN-08-4554"}
   :acs.org {:doi "10.1021/mp700124e"}
   :mcponline.org {:doi "10.1074/mcp.M700241-MCP200"}
   :asm.org {:doi "10.1128/JVI.76.17.8757-8768.2002"}
   :sgmjournals.org {:doi "10.1099/vir.0.19152-0"}
   :cshlp.org {:doi "10.1261/rna.7214405"}
   :bmj.com {:doi "10.1197/jamia.M1640"}})

(deftest test-pdf-stream
  (doseq [[k article] articles]
    (is (let [strm (pdf-stream article)]
          (and strm
               (isa? (class (pdf-stream article)) java.io.InputStream)))
        (str k))))

