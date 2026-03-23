set dotenv-load

help:
  just --list

plus:
  clj -X:dev:plus

nrepl:
  clj -M:dev:nrepl

dev: nrepl

run:
  clojure -M:run-m

test:
  clojure -M:test --watch

clean:
  rm -rf target
  fd -I \.bak$ --exec rm
