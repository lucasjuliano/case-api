(ns case-api.core
(:require   [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [case-api.logic.domain :as l.domain]
            [cheshire.core :as json]
            [clojure.data.json :as clj-json]
            [clojure.java.io :as io])
  (:gen-class))

(defn endpoint-body-response
  [code body]
  {:status  code
  :headers {"Content-Type" "text/html"}
  :body    (->>
              (str body))})

(def error-message "ERROR")

(defn endpoint-response-wrapper 
  [results]
  (if 
    (some? results)
    (endpoint-body-response 200 (json/encode results))
    (endpoint-body-response 400 error-message)))

(defn get-body->map
  [req]
  (let
    [body (:body req)]
    (if
      (nil? body)
      (do {})
      (do (clj-json/read-str (slurp body) :key-fn keyword)))))

(defn endpoint-wrapper
  [req f]
  (let
    [params-map (->> req :params)]
    (->> req
        get-body->map
        (merge params-map)
        f
        endpoint-response-wrapper)))

(defroutes app-routes
  (POST "/brokers" req (endpoint-wrapper req l.domain/create-broker!))
  (POST "/brokers/:broker-id/quotes" req (endpoint-wrapper req l.domain/create-quote!))
  (POST "/brokers/:broker-id/policies" req (endpoint-wrapper req l.domain/create-policy!))
  (GET "/brokers/:broker-id/policies/:policy-id" req (endpoint-wrapper req l.domain/get-policy!))
  (POST "/teste/:teste-id" req (endpoint-wrapper req (fn [n] (println n))))

  ;; (POST "/brokers" req (endpoint-wrapper (apply l.domain/create-broker! ((juxt :first_name :last_name) (:params req)))))
  ;; (POST "/brokers/:broker-id/quotes" [broker-id :as req] (endpoint-wrapper (l.domain/create-quote! broker-id (:age (:params req)) (:sex (:params req)))))
  ;; (POST "/brokers/:broker-id/policies" [broker-id :as req] (endpoint-wrapper (l.domain/create-policy! broker-id (:quotation_id (:params req)) (:name (:params req)) (:sex (:params req)) (:date_of_birth (:params req)))))
  ;; (GET "/brokers/:broker-id/policies/:policy-id" [broker-id policy-id :as req] (endpoint-wrapper (l.domain/get-policy! broker-id policy-id)))
  (route/not-found "Error, page not found!"))

(defn -main
  "This is our main entry point"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    (server/run-server (wrap-defaults #'app-routes api-defaults) {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
      