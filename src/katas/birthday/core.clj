(ns katas.birthday.core
  (:require
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [clojure.string :as string]
    [postal.core :as postal])
  (:import
    (java.time LocalDate)
    (java.time.format DateTimeFormatter)
    (java.time.temporal ChronoUnit)))


(defn row->map [row]
  (zipmap [:name :email :date] row))

(defn load-csv [path]
  (->> path io/resource io/reader csv/read-csv rest (map row->map)))

(defn birthday-today? [now date]
  (string/ends-with? date (.format now (DateTimeFormatter/ofPattern "MM/dd"))))

(defn years-elapsed-since [now date]
  (.between ChronoUnit/YEARS
            (LocalDate/parse date
                             (DateTimeFormatter/ofPattern "yyyy/MM/dd"))
            now))

(defn build-message [now {:keys [name email date]}]
  {:from    "me@example.com"
   :to      email
   :subject "Happy Birthday!"
   :body    (str "Happy Birthday " name "! "
                 "Wow, you're "
                 (years-elapsed-since now date)
                 " years already!")})

(defn send-message! [now row]
  (->> row
       (build-message now)
       (postal/send-message
         {:host "localhost"
          :user "azurediamond"
          :pass "hunter2"
          :port 2525})))

(defn- now []
  (LocalDate/now))

(defn greet! []
  (->> "birthday/employees.csv"
       load-csv
       (filter #(birthday-today? (now) (:date %)))
       (map (partial send-message! (now)))
       doall))