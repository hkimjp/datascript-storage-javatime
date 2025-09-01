# datascript-storage-javatime

My private datascript wrapper.
Datascript + Sqlite backend with java-time support.

I realy love tonsky's `Datascript` and `datascript-sql-stroage`.
However, I could not store java-time objects onto it.
Henryw374's `com.widdindustries/time-literals` solved this issue.
I much thank you two.

I added some simple convenience functions for my own use.

## Installation

deps.edn:
```
io.github.hkimjp/datascript-storage-javatime {:git/tag "0.7.2" :git/sha "9abe965"}
```

## Usage

Start REPL as;

    ❯ clj -M:nrepl

Then connect your REPL client.

    ❯ trench -p `cat .nrepl-port`
    user=> (require '[hkimjp.datascript :as ds])
    nil

## Examples

    user=> (require '[java-time.api :as jt])
    user=> (def schema nil)
    user=> (def db-url "jdbc:sqlite:resources/db.sqlite")
    user=> (def conn (ds/start {:schema schema :url db-url}))
    user=> (ds/transact! conn [{:db/id -1, :time (jt/local-date-time)}])
    user=> (ds/q '[:find ?time
                  :where
                  [_ :time ?time]]
                  @conn)
    #{[#time/date-time "2025-08-11T16:07:09.259732"]}
    user=> (ds/stop)
    user=> (ds/conn?)
    false
    user=> (def conn2 (ds/restore db-url))
    user=> (ds/q '[:find ?time
                  :where
                  [_ :time ?time]]
                  @conn2)
    #{[#time/date-time "2025-08-11T16:07:09.259732"]}
    user=>

* storage folder must exist before starting.
* latter `(start schema db-url)` acts as `(restore db-url)`.
  the schema is ignored.


| use case                       | choose                           | java-time |
| ------------------------------ | -------------------------------- | --------- |
| on-memory                      | datascript                       | OK |
| durable storage                | datascript + datascript-storage-sql        | NG |
| durable storage, use java-time | datascript-storage-javatime      | OK |


### Bugs

Maybe.

## License

Copyright © 2025 Hiroshi Kimura

Distributed under the Eclipse Public License version 1.0.
