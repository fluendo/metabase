(ns metabase.driver.bigquery-test
  (:require [expectations :refer :all]
            [metabase
             [driver :as driver]
             [query-processor :as qp]
             [query-processor-test :as qptest]]
            [metabase.driver.bigquery :as bigquery]
            [metabase.models
             [field :refer [Field]]
             [table :refer [Table]]]
            [metabase.query-processor.middleware.expand :as ql]
            [metabase.test
             [data :as data]
             [util :as tu]]
            [metabase.test.data.datasets :refer [expect-with-engine]]))

(def ^:private col-defaults
  {:remapped_to nil, :remapped_from nil})

;; Test native queries
(expect-with-engine :bigquery
  [[100]
   [99]]
  (get-in (qp/process-query
            {:native   {:query (str "SELECT [test_data.venues.id] "
                                    "FROM [test_data.venues] "
                                    "ORDER BY [test_data.venues.id] DESC "
                                    "LIMIT 2;")}
             :type     :native
             :database (data/id)})
          [:data :rows]))

;;; table-rows-sample
(expect-with-engine :bigquery
  [[1 "Red Medicine"]
   [2 "Stout Burgers & Beers"]
   [3 "The Apple Pan"]
   [4 "Wurstküche"]
   [5 "Brite Spot Family Restaurant"]]
  (->> (driver/table-rows-sample (Table (data/id :venues))
         [(Field (data/id :venues :id))
          (Field (data/id :venues :name))])
       (sort-by first)
       (take 5)))


;; make sure that BigQuery native queries maintain the column ordering specified in the SQL -- post-processing
;; ordering shouldn't apply (Issue #2821)
(expect-with-engine :bigquery
  {:columns ["venue_id" "user_id" "checkins_id"],
   :cols    (mapv #(merge col-defaults %)
                  [{:name "venue_id",    :display_name "Venue ID",    :base_type :type/Integer}
                   {:name "user_id",     :display_name  "User ID",    :base_type :type/Integer}
                   {:name "checkins_id", :display_name "Checkins ID", :base_type :type/Integer}])}

  (select-keys (:data (qp/process-query
                        {:native   {:query (str "SELECT [test_data.checkins.venue_id] AS [venue_id], "
                                                "       [test_data.checkins.user_id] AS [user_id], "
                                                "       [test_data.checkins.id] AS [checkins_id] "
                                                "FROM [test_data.checkins] "
                                                "LIMIT 2")}
                         :type     :native
                         :database (data/id)}))
               [:cols :columns]))

;; make sure that the bigquery driver can handle named columns with characters that aren't allowed in BQ itself
(expect-with-engine :bigquery
  {:rows    [[113]]
   :columns ["User_ID_Plus_Venue_ID"]}
  (qptest/rows+column-names
    (qp/process-query {:database (data/id)
                       :type     "query"
                       :query    {:source_table (data/id :checkins)
                                  :aggregation  [["named" ["max" ["+" ["field-id" (data/id :checkins :user_id)]
                                                                      ["field-id" (data/id :checkins :venue_id)]]]
                                                  "User ID Plus Venue ID"]]}})))

;; make sure BigQuery can handle two aggregations with the same name (#4089)
(expect
  ["sum" "count" "sum_2" "avg" "sum_3" "min"]
  (#'bigquery/deduplicate-aliases ["sum" "count" "sum" "avg" "sum" "min"]))

(expect
  ["sum" "count" "sum_2" "avg" "sum_2_2" "min"]
  (#'bigquery/deduplicate-aliases ["sum" "count" "sum" "avg" "sum_2" "min"]))

(expect
  ["sum" "count" nil "sum_2"]
  (#'bigquery/deduplicate-aliases ["sum" "count" nil "sum"]))

(expect
  [[:user_id "user_id_2"] :venue_id]
  (#'bigquery/update-select-subclause-aliases [[:user_id "user_id"] :venue_id]
                                              ["user_id_2" nil]))


(expect-with-engine :bigquery
  {:rows [[7929 7929]], :columns ["sum" "sum_2"]}
  (qptest/rows+column-names
    (qp/process-query {:database (data/id)
                       :type     "query"
                       :query    (-> {}
                                     (ql/source-table (data/id :checkins))
                                     (ql/aggregation (ql/sum (ql/field-id (data/id :checkins :user_id)))
                                                     (ql/sum (ql/field-id (data/id :checkins :user_id)))))})))

(expect-with-engine :bigquery
  {:rows [[7929 7929 7929]], :columns ["sum" "sum_2" "sum_3"]}
  (qptest/rows+column-names
    (qp/process-query {:database (data/id)
                       :type     "query"
                       :query    (-> {}
                                     (ql/source-table (data/id :checkins))
                                     (ql/aggregation (ql/sum (ql/field-id (data/id :checkins :user_id)))
                                                     (ql/sum (ql/field-id (data/id :checkins :user_id)))
                                                     (ql/sum (ql/field-id (data/id :checkins :user_id)))))})))

(expect-with-engine :bigquery
  "UTC"
  (tu/db-timezone-id))
