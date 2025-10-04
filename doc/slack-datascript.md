
```clojure
(d/q '[:find ?n1 ?n2
       :where
       [?p1 :name ?n1]
       [?p2 :name ?n2]
       [?p1 :born ?b1]
       [?p2 :born ?b2]
       [(clojure.core/second ?b1) ?month]
       [(clojure.core/second ?b2) ?month]
       [(clojure.core/first ?b1) ?day]
       [(clojure.core/first ?b2) ?day]
       [(< ?p1 ?p2)]]
  db)
;; => #{["C" "D"] ["A" "B"]}
```

```clojure
(d/q '[:find ?n1, ?n2
       :in $ %
       :where
       [?p1 :name ?n1]
       [?p2 :name ?n2]
       (birthday ?p1 ?day ?month)
       (birthday ?p2 ?day ?month)
       [(< ?p1 ?p2)]]
  db
  '[[(birthday ?p ?d ?m)
     [?p :born ?b]
     [(clojure.core/first ?b) ?d]
     [(clojure.core/second ?b) ?m]]])
;; => #{["A" "B"]}
```

