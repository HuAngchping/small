; src/clojure_lucene_demo/core.clj: Demonstrating Lucene API using Clojure
;
; Copyright 2011, F.M. (Filip) de Waard <fmw@vix.io>.
;
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
; http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
;org.apache.lucene.index.IndexWriterConfig;

(ns small_repair.lucene.core
  (:use [clojure.string :only (lower-case)])
  (:require [clojure.tools.logging :as log])
  (:import (org.apache.lucene.document
             Document Field Field$Store Field$Index )
           (org.wltea.analyzer.lucene IKAnalyzer)
           (org.apache.lucene.store Directory FSDirectory RAMDirectory)
           (org.apache.lucene.search
             IndexSearcher QueryWrapperFilter TermQuery Sort FilteredQuery FieldCacheTermsFilter SortField SortField$Type)
           (org.apache.lucene.queryparser.classic QueryParser)
           (org.apache.lucene.index IndexWriter
             IndexReader Term IndexWriterConfig IndexWriterConfig$OpenMode)
           (org.apache.lucene.util Version)
           (java.io File)))

(declare get-index-writer)

(defn create-analyzer []
  (IKAnalyzer.))

(defn create-directory [path]
  (if (= path :RAM )
    (RAMDirectory.)
    (FSDirectory/open (File. path))))

(defn create-index-reader [#^Directory directory]
  (try (IndexReader/open directory)
    (catch Exception e (.close (get-index-writer directory)) (create-index-reader directory))
    ))

(defn #^Field create-field
  "Creates a new Lucene Field object."
  ([field-name value]
    (create-field field-name value :stored :analyzed ))
  ([field-name value & options]
    (Field. field-name (str value)
      (if (some #{:stored } options)
        (Field$Store/YES)
        (Field$Store/NO))
      (if (some #{:analyzed } options)
        (Field$Index/ANALYZED)
        (if (some #{:dont-index } options)
          (Field$Index/NO)
          (Field$Index/NOT_ANALYZED))))))

(defn create-filter-of-exist [field-name value]
  (QueryWrapperFilter.
    (TermQuery. (Term. field-name value)))
  )

(defn create-field-of-index [field-name value]
  (create-field field-name value :analyzed ))

(defn create-field-of-stored [field-name value]
  (create-field field-name value :stored ))

(defn create-document-with-field [& fields]
  (let [#^Document document (Document.)]
    (doall (map #(.add document %) fields))
    document
    ))



(defn get-index-writer [directory]
  (let [analyzer (create-analyzer)
        config (IndexWriterConfig. Version/LUCENE_36 analyzer)
        _ (.setOpenMode config IndexWriterConfig$OpenMode/CREATE_OR_APPEND)
        writer (IndexWriter. directory config)
        _ (.commit writer)]
    writer
    )
  )


(defn convert-docs2-map [docs]
  (into []
    (map
      (fn [doc]
        (into {}
          (map
            (fn [field] [(keyword (.name field)) (.stringValue field)] )
            (.getFields doc))))
      docs)
    )
  )

(defn del-index-with-query [directory query]
  (let [writer (get-index-writer directory)]
    (doto writer
      (.deleteDocuments query)
      (.commit)
      ;;(.optimize)
      (.close)
      )
    )
  )


(defn write-index-with-doc! [directory doc]
  (let [writer (get-index-writer directory)]
    (doto writer
      ;;(.setRAMBufferSizeMB 64)
      (.addDocument doc)
      ;; (.optimize)
      (.close)
      ) ;maybe .setUseCompoundFile false?
    ))

(defn get-doc [reader doc-id]
  (.document reader doc-id))

(defn get-docs [reader docs]
  (doall (map #(get-doc reader (.doc %)) docs)))


(defn add-filter-to-query
  ([query filter] (FilteredQuery. query filter))
  ([query filter & others]
    (reduce add-filter-to-query (concat [query filter] others))
    )
  )

(defn create-query [key value analyzer]
  (let [parser (QueryParser. (Version/LUCENE_30) key analyzer)
        q (.parse parser value)
        ]
    q
    )
  )

(defn search
  ([query limit reader]
    (let [searcher (IndexSearcher. reader)
          top-docs (.search searcher query limit)]
      ;;(.  searcher close)
      {:total-hits (.totalHits top-docs)
       :docs (.scoreDocs top-docs)
       }))
  ([query limit reader sort-field-name]
    (let [ stf (SortField. sort-field-name SortField$Type/STRING true)
           st (Sort. stf)
           searcher (IndexSearcher. reader)
           top-docs (.search searcher query limit st)]
      ;;(.  searcher close)
      {:total-hits (.totalHits top-docs)
       :docs (.scoreDocs top-docs)
       }))
  ([query limit reader sort-field-name order-type]
    (let [ stf (SortField. sort-field-name SortField$Type/STRING order-type)
           st (Sort. stf)
           searcher (IndexSearcher. reader)
           top-docs (.search searcher query limit st)]
      ;;(.  searcher close)
      {:total-hits (.totalHits top-docs)
       :docs (.scoreDocs top-docs)
       }))
  )


(defn search-paging
  ( [query page counts-per-page reader]
    (if (= 1 page) (search query counts-per-page reader)
      (let [searcher (IndexSearcher. reader)
            last-top-docs (.search searcher query (* (- page 1) counts-per-page))
            last-docs (.scoreDocs last-top-docs)
            last-doc (last last-docs)
            docs (.searchAfter searcher last-doc query counts-per-page)
            ]
        ;;(. searcher close)
        {:total-hits (.totalHits docs)
         :docs (.scoreDocs docs)
         })
      )
    )
  ( [query page counts-per-page reader sort-field-name]
    (log/info "search sort field=>" sort-field-name)
    (if (= 1 page) (search query counts-per-page reader sort-field-name)
      (let [ stf (SortField. sort-field-name SortField$Type/STRING false)
             st (Sort. stf)
             searcher (IndexSearcher. reader)
             last-top-docs (.search searcher query (* (- page 1) counts-per-page) st)
             last-docs (.scoreDocs last-top-docs)
             last-doc (last last-docs)
             docs (.searchAfter searcher last-doc query counts-per-page st)
             ]
        ;;(. searcher close)
        {:total-hits (.totalHits docs)
         :docs (.scoreDocs docs)
         })
      )
    )
  ( [query page counts-per-page reader sort-field-name order-type]
    (log/info "search sort field=>" sort-field-name)
    (if (= 1 page) (search query counts-per-page reader sort-field-name order-type)
      (let [ stf (SortField. sort-field-name SortField$Type/STRING order-type)
             st (Sort. stf)
             searcher (IndexSearcher. reader)
             last-top-docs (.search searcher query (* (- page 1) counts-per-page) st)
             last-docs (.scoreDocs last-top-docs)
             last-doc (last last-docs)
             docs (.searchAfter searcher last-doc query counts-per-page st)
             ]
        ;;(. searcher close)
        {:total-hits (.totalHits docs)
         :docs (.scoreDocs docs)
         })
      )
    )
  )
