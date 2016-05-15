# spifax

FIXME

## Prerequisites

* leiningen 2.5.0+
* [sugot](http://github.com/ujihisa/sugot)
* spigot 1.9.4 (see below)

## How to run the minecraft server

1. Install sugot
    1. Prepare `spigot-1.9.2.jar` https://www.spigotmc.org/wiki/buildtools/
    2. Install it with `lein deploy localrepo1 org.spigotmc/spigot 1.9.4 spigot-1.9.4.jar`
    3. `lein install` in sugot dir
2. `lein ring server-headless`

## Development Notes

* https://embed.gyazo.com/c77decf6af119756123d87ffbb4b4510.png

## Licence

Copyright (c) 2016 Tatsuhiro Ujihisa

GPL version 3 or any later versions.
