# 0.2.29 (2025-08-11)

- improve README.md

# 0.2.24 (2025-08-11)

- fixed: once closed the connection, can not reconnect in a same session.
  after restarting clojure, it's OK.

```
NullPointerException: "Cannot invoke \"clojure.lang.IFn.invoke(Object)\"
because the return value of \"clojure.lang.Var.getRawRoot()\" is null"
  hkimjp.datascript/make-storage  datascript.clj 36
  hkimjp.datascript/start         datascript.clj 62
  Compiler$InvokeExpr.eval        Compiler.java 4209
  Compiler$DefExpr.eval           Compiler.java 464
```

# 0.2.0 (2025-08-11)

- improved `deps.edn`
- fixed .gitignore - incldue /data/.keep, ignore /data/*
- initialized repository `git@github.com:hkimjp/datascript-strage.git`
- hkimjp/datascript/{start,stop}

- github does not allow me to create repository name including '+'?

```
❯ gh repo create hkimjp/datascript+stroage.git --public
✓ Created repository hkimjp/datascript-stroage on github.com
  https://github.com/hkimjp/datascript-stroage
```

# 0.1.0 (2025-07-31)

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
