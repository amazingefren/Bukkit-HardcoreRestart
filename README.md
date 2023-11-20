# Bukkit-HardcoreRestart

Quick and dirty bukkit plugin i wrote, might work for you, code is trash not really gonna maintain it
 
World must be named "world"

add to plugins folder

best if used on panel with auto restart (or have server auto restart on crash) as this plugin will intentionally crash the server on player death to prevent minecraft jar from auto saving world

 onPlayerDeath - the server will then
 1. Update Player Death Counts JSON
2. Kick All Players From Server - with shameful message
3. Delete the world files
4. Crash the server -- preventing shutdown auto-save and triggering auto-restart if using game-panel (pterodactyl)

`/deaths` command will send a global message to list all player death counts, if any
