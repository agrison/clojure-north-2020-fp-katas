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
  (let [now (LocalDate/now)
        fmt #(.format % (DateTimeFormatter/ofPattern "MM/dd"))
        feb28 (= "02/28" (fmt now))]
    (or (string/ends-with? date (fmt now))
        (and feb28 (string/ends-with? date "02/29")))))

(defn years-elapsed-since [date]
  (.between ChronoUnit/YEARS
            (LocalDate/parse date
                             (DateTimeFormatter/ofPattern "yyyy/MM/dd"))
            (LocalDate/now)))

(defn except-current [all current]
  (remove #(= % current) all))

(defn sharing-birthday-names [all]
  (clojure.string/join " and " (map first all)))

(defn build-message [all [name email date :as current]]
  (let [msg {:from    "me@example.com"
             :to      email
             :subject "Happy Birthday!"
             :body    (str "Happy Birthday " name "! "
                           "Wow, you're "
                           (years-elapsed-since date)
                           " years already!")}
        sharing (except-current all current)]
    (if (not-empty sharing)
      (update msg :body #(str % "\n\"PS. Did you know that you share birthdays with "
                              (sharing-birthday-names sharing) "?"))
      msg)))

(defn send-message! [sharing row]
  (->> row
       (build-message sharing)
       (postal/send-message
         {:host "localhost"
          :user "azurediamond"
          :pass "hunter2"
          :port 2525})))

(defn greet! []
  (as-> "birthday/employees.csv" $
        (load-csv $)
        (filter birthday-today? $)
        (map (partial send-message! $) $)))