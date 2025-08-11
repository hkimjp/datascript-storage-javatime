# datascript-storage

Datascript + Sqlite backend.

I realy love tonsky's `Datascript` and `datascript-sql-stroage`.
However, I could not store java-time's objects onto it.
Henryw374's `com.widdindustries/time-literals` solved the situation.
I much thank you two.

## Installation

Download from https://github.com/hkimjp/datascript-storage

## Usage

I'm using tonsky's clojure+ with Sublime.
Of course, powered by Clojure Sublimed.

    $ clj -X:dev clojure+.core.server/start-server

## Options

FIXME: listing of options this app accepts.

## Examples

    user=> (require '[datascript.core :as d])
    nil
    user=> (require '[hkimjp.datascript :as ds])
    nil
    user=> (require '[java-time.api :as jt])
    nil
    user=> (def conn (ds/start "jdbc:sqlite:data/db.sqlite"))
    #'user/conn
    user=> (d/transact! conn [{:db/id -1, :time (jt/local-date-time)}])
    ...
    user=> (d/q '[:find ?time
                  :where
                  [_ :time ?time]]
                @conn)
    #{[#time/date-time "2025-08-11T16:07:09.259732"]}
    user=> (ds/stop)
    nil
    user=> (ds/start "jdbc:sqlite:data/db.sqlite")
    #'user/conn
    user=> (d/q '[:find ?time
                  :where
                  [_ :time ?time]]
                @conn)
    #{[#time/date-time "2025-08-11T16:07:09.259732"]}
    user=>

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2025 Hiroshi Kimura

Distributed under the Eclipse Public License version 1.0.
