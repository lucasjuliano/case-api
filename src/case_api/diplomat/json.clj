(ns case-api.diplomat.json
(:require   [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))

(def path "")

(def empty-json "[]")

(defn 
  get-json!
  [file]
  (try 
    (slurp file)
    (catch java.io.FileNotFoundException e
      empty-json)))

(defn
  json->map
  [json]
  (json/read-str json :key-fn keyword))

(defn get-db
  [db-file]
  (->> 
    db-file
    get-json!
    json->map))

(defn collection->json-file!
  [collection json-file]
  (spit json-file (json/write-str collection)))


(def db-brokers-file (str path "brokers-db.json"))
(def db-brokers (get-db db-brokers-file))
(def brokers-collection (atom db-brokers))

(def db-quotes-file (str path "quotes-db.json"))
(def db-quotes (get-db db-quotes-file))
(def quotes-collection (atom db-quotes))

(def db-policies-file (str path "policies-db.json"))
(def db-policies (get-db db-policies-file))
(def policies-collection (atom db-policies))

(defn uuid->str [uuid] (str uuid))

(defn db-insert-broker!
  [first-name last-name new-uuid]
  (swap! brokers-collection conj {:uuid       (uuid->str new-uuid)
                                  :first-name first-name
                                  :last-name  last-name})
  (collection->json-file! @brokers-collection db-brokers-file))

(defn db-filter-value-in-atom
  [value key atom]
  (->>
    atom
    (filter #(= value (key %)))))

(defn db-get-broker
  [broker-id]
  (db-filter-value-in-atom broker-id :uuid @brokers-collection))

(defn db-insert-quote!
  [broker-id quote-id price expire-at age sex status]
  (swap! quotes-collection conj {:broker-id    broker-id
                                 :uuid         quote-id
                                 :price        price 
                                 :expire-at    expire-at
                                 :age          age
                                 :sex          sex
                                 :status       status})
  (collection->json-file! @quotes-collection db-quotes-file))

(defn db-get-quote
  [broker-id quote-id]
  (->>
    @quotes-collection
    (db-filter-value-in-atom broker-id :broker-id)
    (db-filter-value-in-atom quote-id :uuid)))

(defn db-insert-policy!
  [broker-id quote-id policy-id name sex date-of-birth status]
  (swap! policies-collection conj 
                                {:broker-id     broker-id
                                 :quote-id      quote-id
                                 :uuid          policy-id
                                 :name          name
                                 :sex           sex
                                 :date-of-birth date-of-birth
                                 :status        status})
  (collection->json-file! @policies-collection db-policies-file))

  (defn db-get-policy
    [broker-id policy-id]
    (->>
      @policies-collection
      (db-filter-value-in-atom broker-id :broker-id)
      (db-filter-value-in-atom policy-id :uuid)))