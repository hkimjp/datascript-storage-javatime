# TODO

## 0.2-SNAPSHOT

- improved `deps.edn`
- fixed .gitignore - incldue /data/.keep, ignore /data/*
- initialized repository `git@github.com:hkimjp/datascript-strage.git`

## 0.1.0 (2025-07-31)

- sucessed java-time objects roundtrip by `time-literals.read-write`.

```clojure
(require '[time-literals.read-write])

(time-literals.read-write/print-time-literals-clj!)

(def datasource
  (doto (org.sqlite.SQLiteDataSource.)
    (.setUrl "jdbc:sqlite:data/db.sqlite")))

(def storage
  (storage-sql/make
   datasource
   {:dbtype :sqlite
    :freeze-str pr-str
    :thaw-str   #(edn/read-string
                  {:readers time-literals.read-write/tags}
                  %)}))
```
