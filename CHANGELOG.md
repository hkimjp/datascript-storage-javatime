# 0.2.0 (2025-08-02)

- pooled-storage
- com.taoensso/telemere
- fixed .gitignore - incldue /data/.keep, ignore /data/*
- initialized repository

- github does not allow me to create repository name including '+'?

```
❯ gh repo create hkimjp/datascript+stroage.git --public
✓ Created repository hkimjp/datascript-stroage on github.com
  https://github.com/hkimjp/datascript-stroage
```

# 0.1.0 (2025-07-31)


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
