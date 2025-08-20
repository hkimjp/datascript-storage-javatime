# datascript-storage-javatime

Datascript + Sqlite backend with java-time support.

I realy love tonsky's `Datascript` and `datascript-sql-stroage`.
However, I could not store java-time objects onto it.
Henryw374's `com.widdindustries/time-literals` solved this issue.
I much thank you two.

## Installation

deps.edn:
```
io.github.hkimjp/datascript-storage {:git/tag "0.4.66" :git/sha "846ed5a"}
```

## Usage

Start REPL as;

    ❯ clj -M:nrepl

Then connect your REPL client.

    ❯ trench -p `cat .nrepl-port`
    user=> (require '[hkimjp.datascript :as ds])
    nil

In my case, using tonsky's clojure+ with Sublime.
Of course, powered by Clojure Sublimed.

## Examples

    user=> (require '[java-time.api :as jt])
    nil
    user=> (ds/puts! [{:db/id -1, :time (jt/local-date-time)}])
    user=> (ds/q '[:find ?time
                  :where
                  [_ :time ?time]])
    #{[#time/date-time "2025-08-11T16:07:09.259732"]}
    user=> (ds/stop)
    nil
    user=> (ds/start "jdbc:sqlite:data/db.sqlite")
    user=> (ds/q '[:find ?time
                  :where
                  [_ :time ?time]])
    #{[#time/date-time "2025-08-11T16:07:09.259732"]}
    user=>

### Bugs

Maybe.

## License

Copyright © 2025 Hiroshi Kimura

Distributed under the Eclipse Public License version 1.0.
