set dotenv-load

help:
  just --list

plus:
  clj -X:dev clojure+.core.server/start-server

nrepl:
  clojure -M:dev -m nrepl.cmdline

container-nrepl:
  clojure -M:dev -m nrepl.cmdline -b 0.0.0.0 -p 7777

up:
  docker compose up -d

down:
  docker compise down

run:
  clojure -M:run-m

format_check:
  clojure -M:format -m cljfmt.main check src dev test

format:
  clojure -M:format -m cljfmt.main fix src dev test

lint:
  clojure -M:lint -m clj-kondo.main --lint .

test:
  clojure -M:test --watch

upgrade: update

update:
  clojure -Tantq outdated :upgrade true :force true

clean:
  rm -rf target
