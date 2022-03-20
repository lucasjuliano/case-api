(ns case-api.logic.domain
(:require   [clojure.pprint :as pp]
            [clojure.string :as str]
            [case-api.diplomat.json :as d]
            [case-api.diplomat.insurance-api :as i-api])
  (:gen-class))

(defn create-uuid 
  []
  (java.util.UUID/randomUUID))

(def status-complete "SUCCESS")
(def status-failed "FAILED")

(defn not-blank?
  [s]
  (not (str/blank? s)))

(defn not-empty?
  [l]
  (not (empty? l)))

(defn all-vector-not-blank?
  [v]
  (every? not-blank? v))

(defn broker-is-valid?
  [first-name last-name]
  (all-vector-not-blank? [first-name last-name]))

(defn create-broker!
  [params]
  (let
  [{first-name :first_name 
    last-name  :last_name}                        params
    new-uuid                                      (create-uuid)]
  (when (broker-is-valid? first-name last-name)
    (do 
      (d/db-insert-broker! first-name last-name new-uuid)
      {:uuid       (str new-uuid)
      :first-name  first-name
      :last-name   last-name}))))

(defn create-quote!
  [params] 
  (let
    [{broker-id :broker-id
      age       :age
      sex       :sex}                           params
    new-quote                                   (i-api/create-quotation! age sex)
    quote-uuid                                  (:id new-quote)
    brokers                                     (d/db-get-broker broker-id)]
    (if
      (and (some? quote-uuid) (not-empty? brokers))
      (do
        (d/db-insert-quote! broker-id quote-uuid (:price new-quote) (:expire_at new-quote) age sex status-complete)
        {:id (:id new-quote)})
      (do (d/db-insert-quote! broker-id nil nil nil age sex status-failed)))))

(defn quote-exists? 
  [broker-id quote-id]
  (some? (d/db-get-quote broker-id quote-id)))

(defn create-policy!
  [params]
  (let
    [{broker-id       :broker-id 
      quotation-id    :quotation_id 
      name            :name 
      sex             :sex 
      date-of-birth   :date_of_birth} params
     new-policy       (i-api/create-policy! quotation-id name sex date-of-birth)
     new-policy-id    (:id new-policy)
     quotes            (d/db-get-quote broker-id quotation-id)]
    (if 
      (and (some? new-policy-id) (not-empty? quotes))
      (do
        (d/db-insert-policy! broker-id quotation-id new-policy-id name sex date-of-birth status-complete)
        {:id new-policy-id})
      (do (d/db-insert-policy! broker-id quotation-id nil name sex date-of-birth status-failed)))))

(defn get-policy!
  [params]
  (let
    [{broker-id   :broker-id 
      policy-id   :policy-id} params
    policy        (i-api/get-policy! policy-id)
    policies      (d/db-get-policy broker-id policy-id)]
    (when (and (some? (:id policy)) (not-empty? policies))
    (do
      {:id              (:id policy)
       :quotation_id    (:quotation_id policy)
       :name            (:name policy)
       :sex             (:sex policy)
       :date_of_birth   (:date_of_birth policy)}))))