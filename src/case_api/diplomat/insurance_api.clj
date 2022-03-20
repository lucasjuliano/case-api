(ns case-api.diplomat.insurance-api
(:require   [clj-http.client :as client]
            [cheshire.core :as json]
            [clojure.data.json :as clj-json])
  (:gen-class))

(defn
  json->map
  [json]
  (clj-json/read-str json :key-fn keyword))

(def case-api-key (System/getenv "CASE_API_KEY"))
;; (def case-api-key "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrVERSeXlQcG5QQnJEemQiLCJhY2NvdW50LWlkIjoiYmIzNTI1ZWUtM2NhMi00NmQ1LTlhYTItYTkxNmZmODhlNTBmIn0.xPsGazEv0n22QQiTs_ccKhJGUA9A08t4Kzx8kQFTUMc")

(defn -request
  [request-type body endpoint]
  (try
  (let
    [json-body (json/encode body)
     req       (request-type (str "https://api.180s.com.br/api/" endpoint)
               {:body         json-body
                :headers       {
                                "Authorization" (str "Bearer " case-api-key)
                                "Content-Type"  "application/json"
                                }})
     body-req (:body req)
     map-body-req (json->map body-req)]
    map-body-req)
  (catch Exception e (str "Caught exception: " (.getMessage e) ". Body:" body))))

(def -post-request (partial -request client/post))

(def -get-request (partial -request client/get))

(defn create-quotation!
  [age sex]
  (let
    [body {"age" age "sex" sex}]
    (-post-request body "quotations")))

(defn create-policy!
  [quotation-id name sex date-of-birth]
  (let
    [body {"quotation_id"       quotation-id 
           "name"               name 
           "sex"                sex 
           "date_of_birth"      date-of-birth}]
    (-post-request body "policies")))

(defn get-policy!
  [policy-id]
  (let
    [body {}
     url (str "policies/" policy-id)]
    (-get-request body url)))