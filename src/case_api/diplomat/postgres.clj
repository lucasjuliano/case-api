(ns case-api.diplomat.postgres
(:require   [clojure.string :as str]
            [clojure.data.json :as json]
            [clojure.java.jdbc :as sql])
  (:gen-class))

;; (sql/insert! db-spec :alunos {:id 323 :nome "Hello World" :nota 12})
;; (println (sql/query db-spec ["SELECT * FROM alunos"])

(def db-spec {:dbtype    "postgresql" 
              :dbname    "docker" 
              :user      "clojure"      
              :password  "clojure!" 
              :host      "postgres-case-api"})

(def insert (partial sql/insert! db-spec))

(def select (partial sql/query db-spec))

(defn db-insert-broker!
  [first-name last-name new-uuid]
  (insert :brokers {:id         new-uuid
                    :first_name first-name 
                    :last_name  last-name}))

(defn db-get-broker!
  [broker-id]
  (select ["SELECT * FROM brokers WHERE id = ?" broker-id] 
          {:row-fn :id_count}))
  

(defn db-insert-quote!
  [broker-id quote-id price expire-at age sex]
  (insert :quotes {:broker_id   broker-id
                   :id          quote-id
                   :price       price
                   :expire_at   expire-at
                   :age         age
                   :sex         sex}))
