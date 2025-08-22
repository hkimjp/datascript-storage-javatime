# datascript-storage-javatime

Datascript + Sqlite backend with java-time support.

I realy love tonsky's `Datascript` and `datascript-sql-stroage`.
However, I could not store java-time objects onto it.
Henryw374's `com.widdindustries/time-literals` solved this issue.
I much thank you two.

I added some simple convenience functions for my own use.

## Installation

deps.edn:
```
io.github.hkimjp/datascript-storage-javatime {:git/tag "0.5.83" :git/sha "cc558ef"}
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
    user=> (def db-url "jdbc:sqlite:storage/db.sqlite")
    user=> (ds/start {:schema schema :url db-url})
    user=> (ds/puts! [{:db/id -1, :time (jt/local-date-time)}])
    user=> (ds/q '[:find ?time
                  :where
                  [_ :time ?time]])
    #{[#time/date-time "2025-08-11T16:07:09.259732"]}
    user=> (ds/stop)
    user=> (ds/conn?)
    false
    user=> (ds/start schema db-url)
    user=> (ds/q '[:find ?time
                  :where
                  [_ :time ?time]])
    #{[#time/date-time "2025-08-11T16:07:09.259732"]}
    user=>

* storage folder must exist before starting
* `schema` is ignored in latter `(start schema db-url)` call.
  should define other function like `(restore db-url)`?


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
