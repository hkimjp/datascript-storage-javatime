(ns hkimjp.xtdb-tutorial
  (:require
   [babashka.fs :as fs]
   [clojure.edn]
   [clojure.string :as str]
   [hkimjp.datascript :as ds :refer [start stop restore transact! q]]))

;; prep -------------------------------

(defn make-id-positive [infile outfile]
  (fs/write-lines
   outfile
   (for [line (fs/read-all-lines infile)]
     (str/replace line #"-(\d{3})" #(str (second %))))))

(make-id-positive "resources/docs.edn" "doc/docs-positive.edn")

(def my-docs
  (clojure.edn/read-string (slurp "doc/docs-positive.edn")))

(def schema {:movie/cast {:db/cardinality :db.cardinality/many}})

(def conn (start {:schema schema :url "jdbc:sqlite:storage/tutorial.sqlite"}))

(transact! conn my-docs)

(comment
  (ds/gc)
  (ds/stop)
  (def conn nil)
  (ds/conn?)
  :rcf)

(stop)

;; retart ----------

(def conn (restore "jdbc:sqlite:storage/tutorial.sqlite"))

(q '[:find ?title
     :where
     [_ :movie/title ?title]]
   @conn)

(q '[:find ?name
     :where
     [?p :person/name ?name]]
   @conn)

(ds/qq '[:find ?name
         :where
         [_ :person/name ?name]])

;; Q1.
(q '[:find ?title
     :where
     [_ :movie/title ?title]]
   @conn)

(ds/qq '[:find ?title
         :where
         [_ :movie/title ?title]])

;; Basic Queries
(q '[:find ?title
     :where
     [_ :movie/title ?title]]
   @conn)

(q '[:find ?e
     :where
     [?e :person/name "Ridley Scott"]]
   @conn)

(q '[:find ?e
     :where
     [?e :person/name _]]
   @conn)

; Exercises
;; Q1.
(q '[:find ?e
     :where
     [?e :movie/year 1987]]
   @conn)

;; Q2.
(q '[:find ?e ?title
     :where
     [?e :movie/title ?title]]
   @conn)

;; Q3. Find the name of all people in the database
(q '[:find ?name
     :where
     [_ :person/name ?name]]
   @conn)

; Data patterns

(q '[:find ?title
     :where
     [?e :movie/year 1987]
     [?e :movie/title ?title]]
   @conn)

(q '[:find ?name
     :where
     [?m :movie/title "Lethal Weapon"]
     [?m :movie/cast ?p]
     [?p :person/name ?name]]
   @conn)

(q '[:find ?name
     :where
     [?m :movie/title "RoboCop"]
     [?m :movie/cast ?p]
     [?p :person/name ?name]]
   @conn)

;; Exercises

(q '[:find ?title
     :where
     [?e :movie/year 1985]
     [?e :movie/title ?title]]
   @conn)

(q '[:find ?year
     :in $ ?title
     :where
     [?e :movie/title ?title]
     [?e :movie/year ?year]]
   @conn
   "Alien")

;; Q3. Who directed RoboCop?
(q '[:find ?name
     :where
     [?m :movie/title "RoboCop"]
     [?m :movie/director ?p]
     [?p :person/name ?name]]
   @conn)

;; Q4. Find directors who have directed Arnold Shwarzenegger in a movie.

(q '[:find ?name
     :in $ ?actor
     :where
     [?d :person/name ?name]
     [?m :movie/cast ?p]
     [?p :person/name ?actor]
     [?m :movie/director ?d]]
   @conn
   "Arnold Schwarzenegger")

;; Parameterized queries

(q '[:find ?title
     :in $ ?name
     :where
     [?p :person/name ?name]
     [?m :movie/cast ?p]
     [?m :movie/title ?title]]
   @conn
   "Sylvester Stallone")

;; Tuples

(q '[:find ?title
     :in $ [?director ?actor]
     :where
     [?d :person/name ?director]
     [?a :person/name ?actor]
     [?m :movie/director ?d]
     [?m :movie/cast ?a]
     [?m :movie/title ?title]]
   @conn
   ["James Cameron" "Arnold Schwarzenegger"])

(q '[:find ?title
     :in $ ?director ?actor
     :where
     [?d :person/name ?director]
     [?a :person/name ?actor]
     [?m :movie/director ?d]
     [?m :movie/cast ?a]
     [?m :movie/title ?title]]
   @conn
   "James Cameron" "Arnold Schwarzenegger")

;; Collections
;; implementation of `logical OR`
(q '[:find ?title ?director
     :in $ [?director ...]
     :where
     [?p :person/name ?director]
     [?m :movie/director ?p]
     [?m :movie/title ?title]]
   @conn
   ["James Cameron" "Ridley Scott"])

;; Relations
;; the most interesting and powerful of input types.

(q '[:find ?title ?box-office
     :in $ ?director [[?title ?box-office]]
     :where
     [?p :person/name ?director]
     [?m :movie/director ?p]
     [?m :movie/title ?title]]
   @conn
   "Ridley Scott"
   [["Die Hard" 140700000]
    ["Alien" 104931801]
    ["Lethal Weapon" 120207127]
    ["Commando" 57491000]])

; Q1. Find movie title by year

(q '[:find ?title
     :in $ ?year
     :where
     [?e :movie/title ?title]
     [?e :movie/year ?year]]
   @conn 1987)

; Q2.Given a list of movie titles, find the title and the year that movie was released.
(q '[:find ?title ?year
     :in $ [?title ...]
     :where
     [?e :movie/title ?title]
     [?e :movie/year ?year]]
   @conn
   ["Lethal Weapon" "Lethal Weapon 2" "Lethal Weapon 3"])

; Q3. Find all movie `title`s where the `actor` and the `director` has worked together
(q '[:find ?title
     :in $ ?actor ?director
     :where
     [?a :person/name ?actor]
     [?d :person/name ?director]
     [?m :movie/cast ?a]
     [?m :movie/director ?d]
     [?m :movie/title ?title]]
   @conn "Michael Biehn" "James Cameron")

; Q4. Write a query that, given an actor name and a relation with movie-title/rating, finds the movie titles and corresponding rating for which that actor was a cast member.
(def movie-ratings
  [["Die Hard" 8.3]
   ["Alien" 8.5]
   ["Lethal Weapon" 7.6]
   ["Commando" 6.5]
   ["Mad Max Beyond Thunderdome" 6.1]
   ["Mad Max 2" 7.6]
   ["Rambo: First Blood Part II" 6.2]
   ["Braveheart" 8.4]
   ["Terminator 2: Judgment Day" 8.6]
   ["Predator 2" 6.1]
   ["First Blood" 7.6]
   ["Aliens" 8.5]
   ["Terminator 3: Rise of the Machines" 6.4]
   ["Rambo III" 5.4]
   ["Mad Max" 7.0]
   ["The Terminator" 8.1]
   ["Lethal Weapon 2" 7.1]
   ["Predator" 7.8]
   ["Lethal Weapon 3" 6.6]
   ["RoboCop" 7.5]])

(q '[:find ?title ?rating
     :in $ ?name [[?title ?rating]]
     :where
     [?p :person/name ?name]
     [?m :movie/cast ?p]
     [?m :movie/title ?title]]
   @conn "Mel Gibson" movie-ratings)

;; Predicates
;; can use any clojure functions as a predicate function.

(q '[:find ?title
     :where
     [?m :movie/title ?title]
     [?m :movie/year ?year]
     [(< year 1984)]]
   @conn)

(q '[:find ?name
     :where
     [?p :person/name ?name]
     [(clojure.string/starts-with? ?name "M")]]
   @conn)

; Q1. Find movies older than a certain year (inclusive)

(q '[:find ?title
     :in $ ?thres
     :where
     [?m :movie/title ?title]
     [?m :movie/year ?year]
     [(<= ?year ?thres)]]
   @conn 1979)

; Q2. Find actors older than Danny Glover
(q '[:find ?name
     :where
     [?d :person/name "Danny Glover"]
     [?d :person/born ?b1]
     [?e :person/born ?b2]
     [_ :movie/cast ?e]
     [(< ?b2 ?b1)]
     [?e :person/name ?name]]
   @conn)

; movie-ratings

; Q3. Find movies newer than `year` (inclusive) and has a `rating` higher than the one supplied

(q '[:find ?title ?y
     :in $ ?year ?rating [[?title ?r]]
     :where
     [(< ?rating ?r)]
     [?m :movie/title ?title]
     [?m :movie/year ?y]
     [(<= ?year ?y)]]
   @conn 1990 8.0 movie-ratings)

(q '[:find ?title ?year
     :in $ ?y
     :where
     [?m :movie/title ?title]
     [?m :movie/year ?year]
     [(<= ?y ?year)]]
   @conn 1990)

;; Transformation functions

(defn age [^java.util.Date birthday ^java.util.Date today]
  (quot (- (.getTime today)
           (.getTime birthday))
        (* 1000 60 60 24 365)))

(comment
  ; java.util.Date
  ;
  ; `year` is sincee 1900. so, 2025 will be 125.
  ; `january` is the 0th month.
  ; java.util.Data. doesn't have time zone. think 9:00 offset.

  (java.util.Date. 125 7 25 0 0 0)

  :rcf)

(q '[:find ?age
     :in $ ?name ?today
     :where
     [?p :person/name ?name]
     [?p :person/born ?born]
     [(hkimjp.xtdb-tutorial/age ?born ?today) ?age]]
   @conn "Tina Turner" (java.util.Date.))

; Q1. Find people by age. Use the function `user/age` to find the names of people, given their age and a date representing "today".

(q '[:find ?name
     :in $ ?age ?today
     :where
     [?p :person/name ?name]
     [?p :person/born ?born]
     [(hkimjp.xtdb-tutorial/age ?born ?today) ?age]]
   @conn 69 (java.util.Date.))

; Q2. Find the names of people younger than Bruce Willis and their corresponding age.
(q '[:find ?name ?age
     :in $ ?today
     :where
     [?b :person/name "Bruce Willis"]
     [?b :person/born ?bb]
     [?p :person/name ?name]
     [?p :person/born ?pb]
     [(< ?bb ?pb)]
     [(hkimjp.xtdb-tutorial/age ?pb ?today) ?age]]
   @conn #inst "2013-08-02T00:00:00.000-00:00")

; Aggregates

; Q1. `count` the number of movies in the database

(q '[:find (count ?title)
     :where
     [_ :movie/title ?title]]
   @conn)

; Q2. Find the birth date of the oldest person in the database.

(q '[:find (min ?born)
     :where
     [_ :person/born ?born]]
   @conn)

; Q3. Given a collection of actors and (the now familiar) ratings data. Find the average rating for each actor. The query should return the actor name and the `avg` rating.

(q '[:find ?name (avg ?rating)
     :in $ [?name ...] [[?title ?rating]]
     :where
     [?p :person/name ?name]
     [?m :movie/cast ?p]
     [?m :movie/title ?title]]
   @conn
   ["Sylvester Stallone" "Arnold Schwarzenegger" "Mel Gibson"]
   movie-ratings)

(q '[:find ?color (max ?amount ?x) (min ?amount ?x)
     :in $ [[?color ?x]] ?amount]
   @conn
   [[:red 10]  [:red 20] [:red 30] [:red 40] [:red 50]
    [:blue 7] [:blue 8]]
   3)

; Rules

(q '[:find  ?u1 ?u2
     :in    $ %
     :where (follows ?u1 ?u2)]
   [[1 :follows 2]
    [2 :follows 3]
    [3 :follows 4]]
   '[[(follows ?e1 ?e2)
      [?e1 :follows ?e2]]
     [(follows ?e1 ?e2)
      [?e1 :follows ?t]
      (follows ?t ?e2)]])

(q '[:find ?name
     :where
     [?p :person/name ?name]
     [?m :movie/cast ?p]
     [?m :movie/title "The Terminator"]]
   @conn)

(q '[:find ?name
     :in $ %
     :where (actor-movie ?name "The Terminator")]
   @conn
   '[[(actor-movie ?name ?title)
      [?p :person/name ?name]
      [?m :movie/cast ?p]
      [?m :movie/title ?title]]])

(ds/qq '[:find ?name
         :in $ %
         :where (actor-movie ?name "The Terminator")]
       '[[(actor-movie ?name ?title)
          [?p :person/name ?name]
          [?m :movie/cast ?p]
          [?m :movie/title ?title]]])

(ds/qq '[:find ?title
         :in $ %
         :where (actor-movie "Michael Biehn" ?title)]
       '[[(actor-movie ?name ?title)
          [?p :person/name ?name]
          [?m :movie/cast ?p]
          [?m :movie/title ?title]]])

(stop)

;; m4 573ms
