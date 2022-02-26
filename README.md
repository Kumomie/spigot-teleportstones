# Hi
Now one year later I upload this spigot plugin. I stopped working on it, because writing documentation is so boring. The plugin works tho. I will now clean it up, add more features and ... write documentation. :/ Lets go.


# SpigotMC Plugin: **TeleportStone** 1.0 (Beta)
TeleportStones can be build in the world and used as quick travel system. Players can just walk up to a TeleportStone, click it, select a destination (another TeleportStone) and teleport. But before you can teleport to or from a TeleportStone, you need to discover it first. (Similar to quick travel in other games, e.g. Zelda:BotW, DarkSouls, etc.) That means, they need to use their feet and search for or randomly discover one. If a player finds a TeleportStone, he/she just needs to click it and it will be added to the players list of personal discovered TeleportStones.

**Compatible SpigotMC Version: 1.17**

## Features
- Quick travel between TeleportStones
- Discover mechanic
- Destruction protection
- Permissions
- GUI
- Different default TeleportStones (can be disabled)
- Custom TeleportStones
## Backstory

TLDR
- We had an old Bukkit server with a plugin called [MemoryStone by CmdrDats](https://github.com/CmdrDats/MemoryStone)
- Plugin was good
- Was not updated for current Minecraft versions.
- Made this similar plugin.

Some many years back (2012/2013) we had a private Bukkit server for friends and co. Installed on the server was a plugin called [MemoryStone by CmdrDats](https://github.com/CmdrDats/MemoryStone). It provided similar features to this plugin. You could build Structures called MemoryStones and teleport between them and there was a compass mechanic (I forgot what this did). Then there came a time we stopped playing and the server went offline. Some time went by, new Minecraft versions were released and we wanted to play again. But MemoryStones was not updated. So it was not useable anymore back then.

Now 9 years later Minecraft is calling again. I saw that the maker of MemoryStones made the last update 13 month back for Spigot 1.15.2, so it was kinda updated in the mean time. Me now can program a little more, just wanted to build this plugin myself. THE END

## Table of Content
- [SpigotMC Plugin: **TeleportStone** 1.0 (Beta)](#spigotmc-plugin-teleportstone-10-beta)
  - [Features](#features)
  - [Backstory](#backstory)
  - [Table of Content](#table-of-content)
- [Guide](#guide)
- [Commands](#commands)
- [Permissions](#permissions)
- [Custom TeleportStones (Blueprints and Scopes)](#custom-teleportstones-blueprints-and-scopes)
  - [Scopes](#scopes)
    - [Default Scopes](#default-scopes)
  - [Blueprints](#blueprints)
    - [Block Offsets](#block-offsets)

# Guide

# Commands
- **/tpst help** - Help for help.
- **/tpst list** - Lists all existing TeleportStones.
- **/tpst players** - Lists all players, that have used the plugin.
- **/tpst discovered** - Lists all your discovered TeleportStones.
- **/tpst nearest** - Shows the nearest TeleportStone.
- **/tpst distance <name\>** - Calculates distance to a TeleportStone.
- **/tpst tp <name\>** - Teleport yourself to specified TeleportStone. (You have to be close to another TeleportStone)
- **/tpst events** - Shows noteworthy events of the plugin. (Destroy, Discover, Request, Create, Delete, Teleport, Info, Error)
- **/tpst version** - Version of TeleportStone Plugin.
 **/tpst delete <name\>** - Delete TeleportStone.
- **/tpst destroy <name\>** -Delete TeleportStone and destroy all of its blocks.
- **/tpst unregister <username\>** - Removes a player and his discovered TeleportStones from the register.
- **/tpst undiscover <username\> <nameTeleportStone\>** - Removes specified discovered TeleportStone from a the list of discovered TeleportStones of a player.
- **/tpst manualdiscover <username\> <nameTeleportStone\>** - Add specified TeleportStone
- 
- Fügt den angegebenen TeleportStone der Liste entdeckter TeleportStones des angegebenen Nutzers hinzu.
- **/tpst blueprints** Gibt alle geladenen Baupläne aus.
- **/tpst scopes** Gibt alle geladenen Scopes aus.
- **/tpst favorite <nameTeleportStone\>** Entfernt TeleportStone von Favoriten bzw. fügt TeleportStone zu Favoriten hinzu.
- **/tpst home <nameTeleportStone\>** Bestimmt einen TeleportStone als Home bzw. entfernt einen TeleportStone als Home.
- **/tpst request accept [requestTeleportStoneName]** 
  - Akzeptiert nächste TeleportStone-Anfrage bzw. akzeptiert TeleportStone-Anfrage mit angegebenem Namen.
- **/tpst request deny [requestTeleportStoneName]** 
  - Verweigert nächste TeleportStone-Anfrage bzw. verweigert TeleportStone-Anfrage mit angegebenem Namen.
  - Entfernt Schrift auf dem Schild
- **/tpst request show [requestTeleportStoneName]**
  - Zeigt eine Übersicht über nächste TeleportStone-Anfrage bzw. zeigt eine Übersicht über TeleportStone-Anfrage mit angegebenem Namen.
  - Übersicht:
    - Datum
    - Anfragesteller (Erbauer)
    - Name des TeleportStones
    - Koordinaten
    - Welt
    - Bauplan
    - Nächster TeleportStone
# Permissions

# Custom TeleportStones (Blueprints and Scopes)

## Scopes
Scopes make it possible to put limits to what TeleportStone can reach another TeleportStone. They describe reachability. A TeleportStone has **exactly 1** scope assigned to itself. For this scope is configured, to which other scopes you can teleport from this TeleportStone.

***Example*** 
- There exist two scopes: ***ScopeA***, ***ScopeB***
  - ***ScopeA*** can teleport to TeleportStones with scopes ***ScopeA*** or ***ScopeB***.
  - ***ScopeB*** can only teleport to TeleportStones with scope ***ScopeB***.
  - Here is the table representation:
  
<center>

| Scope Name | Possible Destinations                       |
| ---------- | ------------------------------------------- |
| ScopeA     | ScopeA, ScopeB                              |
| ScopeB     | ScopeB                                      |

</center>

- Furthermore there are 3 different **types** of TeleportStones. They are called ***T1***, ***T2***, ***T3***
  - They could all use different structures and materials. Etc.
  - See next Section: **TODO**
- Now every type of TeleportStone gets assigned **exactly 1** scope.
  - ***T1*** &rarr; ***ScopeA***
  - ***T2*** &rarr; ***ScopeB***
  - ***T3*** &rarr; ***ScopeB***
- With such a setup TeleportStones of type ***T2*** and ***T3*** can teleport among themselves, but are unable to teleport to TeleportStones of type ***T1***. On the other hand TeleportStones of type ***T1*** can teleport to all other types.

For more examples take a look at the included default TeleportStones. See section **TODO**

### Default Scopes
Default scopes offer a generell solution for TeleportStones.
- Scope **All** is reachable from all scopes and can teleport to all scopes.
- Scope **Reachable** is reachable from all scopes.

## Blueprints
A blueprint is ... a blueprint. Blueprints describe how a TeleportStone has to be build. What materials to use, where to place blocks of the structure, scopes and more.

A blueprint has the following entries:
- **Blueprint ID** is a unique identifier for a blueprint. An ID could be a name, some text or simply a number.
- **Scope Name** is a reference to a scope. (What is a scope? See section: **TODO**)
- **BlockOffsets** is a list of list of blocks, which describe the structure of TeleportStone.
- **SignMaterials** is a list of possible materials that the sign can be made from.
- **isWorldBound** is a boolean value (true/false). If this is set true, a TeleportStone can only teleport to TeleportStones in the same world.
- **Range** is a numerical value, which indicates how far a TeleportStone can teleport. 
  - A blueprint can't have a range, if it isn't world bound.


### Block Offsets
