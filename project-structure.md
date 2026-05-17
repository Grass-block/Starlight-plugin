# Project Structure

> this document can be feed to LLM.

## Java modules
- starlight-core: bukkit plugin engine and core components.
- starlight-bundler: bukkit bundler module.
- starlight-velocity: velocity plugin bundler.
- other: extension packs.

## Chained dependency

- me.gb2022.gluon: Gluon application framework.

## Content location

Each module is listed in the java module's source base,
usually under org.atcraftmc.starlight.(package)
All bukkit modules inherits BukkitAbstractModule.