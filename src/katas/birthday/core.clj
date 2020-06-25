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

(defn load-csv [path]
  (->> path io/resource io/reader csv/read-csv rest))

(defn birthday-today? [[_ _ date]]
  (string/ends-with? date
                     (.format (LocalDate/now) (DateTimeFormatter/ofPattern "MM/dd"))))

(defn years-elapsed-since [date]
  (.between ChronoUnit/YEARS
            (LocalDate/parse date
                             (DateTimeFormatter/ofPattern "yyyy/MM/dd"))
            (LocalDate/now)))

(defn build-message [[name email date]]
  {:from    "me@example.com"
   :to      email
   :subject "Happy Birthday!"
   :body    (str "Happy Birthday " name "! "
                 "Wow, you're "
                 (years-elapsed-since date)
                 " years already!")})

(defn send-message! [row]
  (->> row
       build-message
       (postal/send-message
         {:host "localhost"
          :user "azurediamond"
          :pass "hunter2"
          :port 2525})))

(defn greet! []
  (->> "birthday/employees.csv"
       load-csv
       (filter birthday-today?)
       (map send-message!)
       doall))