# spifax

FIXME

## Prerequisites

* leiningen 2.5.0+
* [sugot](http://github.com/ujihisa/sugot)
* spigot 1.10.2 (see below)

## How to run the minecraft server

1. Install spifax
    1. Prepare `spigot-1.10.2.jar` https://www.spigotmc.org/wiki/buildtools/
    2. Install it with `lein deploy localrepo1 org.spigotmc/spigot 1.10.2 spigot-1.10.2.jar`
    3. `lein install` in spifax dir
2. `lein ring server-headless`

## Development Notes

* https://embed.gyazo.com/c77decf6af119756123d87ffbb4b4510.png
* `lein test`

## Licence

Copyright (c) 2016 Tatsuhiro Ujihisa

GPL version 3 or any later versions.
