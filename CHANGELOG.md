# 2025-08-11

- improved `deps.edn`

# 2025-08-02

- fixed .gitignore - incldue /data/.keep, ignore /data/*
- initialized repository `git@github.com:hkimjp/datascript-strage.git`

# 2025-07-31

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
