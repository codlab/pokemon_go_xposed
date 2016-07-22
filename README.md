# Pokemon GO XPosed

This is the Android implementation of an app AND a XPosed module.

## First things First

You must have an Android smartphone. Then, it also needs to be rooted. This step
is often not that hard to achieve but requires that you follow few rules :

    - do not follow everything without thinking about what you are doing
    - I will not be able to help everyone to root their devices
    - It is often stated (with reasons) that rooting the device will void the warranty
    - Rooting will prevent you from receiving updates of your device (no issue here :p)

Finally, you need to install XPosed. XPosed is quite easy to install in fact. It is unlikely that it will brick your device. But follow the different tutorials available online. The only thing important are to install the proper XPosed to your system.

With XPosed, you must take care not to install everything you can find... Modules have quite a lot of power and can mess easily with everything.

## The XPosed module

This module is 100% Open Source and will never be here to steal anything from you. You can review, comment and pull request to this project !!

The module gives you the ability to :

    - fix the screen off bug *
    - intercept the different position spawn and send them to the app's service


* It is very annoying to play with the game. And after few seconds, the screen goes off : no more, Pokémon spawn etc...

** Other functionnality based in these (locally) aggregated data will be possible in the companion app.

## The app

For now the app is mostly done around an empty activity and a remote service.

The service is here to store events from the module and to upload the data to go.codlab.eu live server. It could be great if in the near future, locally, a "dragon ball" GPS like could be used in overlay ; it would show the different spawn point directly from the Pokemon GO map

## Code quality and reviewing

For now, 0 tests have been written. Only sketches and the architecture.

# License

This project is released under the GPL License.

Fork It, improve it, pull request and have a beer. If you are near Bordeaux, let's have a drink :0)

```
Copyright (C) 2016 Kévin Le Perf

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```
