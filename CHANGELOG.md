# Unreleased

- create ds-client.
- on-memory (gc) works?

# 0.6.119 (2025-08-26)

- **BREAKING** wrappers and aliases.
- default storage is saved at `resources/db.sqlite`.
- removed `resources` from `:paths`.

# 0.5.101 (2025-08-23)

- (restore url) is a short form of (start {:url url})

# 0.5.83 (2025-08-22)

- **BREAKING** (start) takes option. (start {:schema schema :url url})
- (gc) does not start on-memory mode.
- CHANGED repository `datascript-storage-javatime`.
- improved README.md.
- CHANGED ds/conn resumed to public.

# 0.4.66 (2025-08-19)

- CHANGED (start) or (start schema storage) - if exists `storage`,
  ignores `schema`.
- CHANGED `ds/q` - defined as a function not a macro.
- conn and storage are privare.
- in a single transaction, negative db/id keep consistent?
- CHANGED create-conn - multi arity function.

# 0.3.54 (2025-08-19)

- {:git/tag :git/sha} - use `git show-ref --abbrev=7 --tags`
- convenience funcitons - q, entity, pull, puts
- usage entity

# 0.2.31 (2025-08-19)

- imporved `README.md`

# 0.2.30 (2025-08-17)

- only `start`, `stop`, `conn?`, `gc` are public
- removed `clj-reload` from dependency

# 0.2.29 (2025-08-11)

- improved README.md

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
