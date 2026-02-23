# Unreleased


# 0.7.8-SNAPSHOT

- just clean now uses regexp `\.bak$`
- replaced 'def' with 'alter-var-root' in `/dev/xtdb_tutorial.clj`

# 0.7.7 (2026-02-20)

io.github.hkimjp/datascript-storage-javatime
{:git/tag "0.7.7" :git/sha "ce4e814"}

- updated libraries

| :file    | :name                         | :current | :latest  |
|----------|-------------------------------|----------|----------|
| deps.edn | babashka/fs                   | 0.5.27   | 0.5.31   |
|          | com.taoensso/telemere         | 1.1.0    | 1.2.1    |
|          | io.github.clojure/tools.build | v0.10.10 | v0.10.12 |
|          | io.github.tonsky/clojure-plus | 1.7.0    | 1.7.2    |
|          | nrepl/nrepl                   | 1.5.1    | 1.5.2    |
|          | org.clojure/clojure           | 1.12.3   | 1.12.4   |
|          | org.clojure/test.check        | 1.1.1    | 1.1.3    |
|          | org.xerial/sqlite-jdbc        | 3.50.3.0 | 3.51.2.0 |
| pom.xml  | org.clojure/clojure           | 1.12.3   | 1.12.4   |


- updated bin/ds

    -J--enable-native-access=ALL-UNNAMED

# 0.7.6

- tonsky/datascript 1.7.8 -  Skip storage writes for no-op transactions 
  #492 via @jxonas

# 0.7.5

- updated libraries

| :file    | :name                         | :current | :latest |
|----------|-------------------------------|----------|---------|
| deps.edn | babashka/fs                   | 0.5.26   | 0.5.27  |
|          | datascript/datascript         | 1.7.5    | 1.7.6   |
|          | io.github.tonsky/clj-reload   | 0.9.8    | 1.0.0   |
|          | io.github.tonsky/clojure-plus | 1.6.3    | 1.7.0   |
|          | nrepl/nrepl                   | 1.3.1    | 1.4.0   |
|          | org.clojure/clojure           | 1.12.2   | 1.12.3  |
| pom.xml  | org.clojure/clojure           | 1.12.2   | 1.12.3  |


# 0.7.4 (2025-09-02)

- version
- added `bump-version-local.sh`

# 0.7.3 (2025-09-02)

- restore's argument is a map {:url "jdbc-url"} or a string "jdc-url"
- argument of start must be a map {:schema {schema} :url "url"}
- consistency of log level

# 0.7.2 (2025-09-01)

- added (start-or-restore jdbc-url)
- addded `put!`
- changed default-storage-url "/tmp/db.sqlite"
- changed/fixed resume takes a map `(resume {:url url})`
- fixed  `(restore)` is an alias of `(resotore {:url default-storage-url})`

# 0.7.1 (2025-08-29)

- FIXED: use of default storage.
  (start) or (start {:scheme s}) starts on-memory.
  (start {:url nil}) starts using `default-storage-url`.
  (start {:url url}) create a new database store at url.
  (restore) or (restart url) restart from memoried database.
- updated :git/tag, :git/sha in README.md and bin/ds.

# 0.7.0 (2025-08-28)

- resumed to MAJOR.MINOR.PATCH.
  it is hard to detect how many improvements have done
  in the MAJOR.MINOR starge using MAJOR.MINOR.COMMIT.
- changed `puts!` - if the doc does not have :db/id, add `:db/id -1` to it.
- added `bin/ds`.

# 0.6.138 (2025-08-27)

- clojure 1.12.2

# 0.6.128 (2025-08-27)

- refactored.
- resumed - `resources` in `:path`.
- default storage path.
- included `dev/xtdb-tutorials.clj`.
  copied from the nextjournal site, modified the xtdb/id to positive db/id.

# 0.6.119 (2025-08-26)

- **BREAKING** wrappers and aliases.
- default storage is saved at `resources/db.sqlite`.
- removed `resources` from `:pdaths`.

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
