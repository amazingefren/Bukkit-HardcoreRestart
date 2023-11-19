package com.amazingefren.hardcorerestart;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.World;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.Listener;

/**
 * onPlayerDeath - the server will then
 * 1. Update Player Death Counts JSON
 * 2. Kick All Players From Server - with shameful message
 * 3. Delete the world files
 * 4. Crash the server -- preventing shutdown auto-save and triggering auto-restart if using game-panel (pterodactyl)
 */
public class DeathTrigger implements Listener{
    private final HardcoreRestart plugin;

    public DeathTrigger(final HardcoreRestart corePlugin) {
        this.plugin = corePlugin;
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        // System.out.println("container path: " + Bukkit.getWorldContainer().toString());
        final String kickMessage = event.getDeathMessage();
        final String playerName = event.getEntity().getName();
        // System.out.println(("playername: " + playerName));
        int playerDeathNumber = updatePlayerDeathCounter(playerName);
        for (final Player target : Bukkit.getServer().getOnlinePlayers()) {
            target.kickPlayer(
             ChatColor.BOLD + "" + ChatColor.RED + kickMessage +
                ChatColor.RESET + ChatColor.WHITE + "\n\nThis idiot has died " +
                ChatColor.GOLD + playerDeathNumber + " times..."
            );
        }

        // TODO: read server.properties for world name
        this.deleteWorld("world");
        this.deleteWorld("world_nether");
        this.deleteWorld("world_the_end");

        /**
        World delete1 = Bukkit.getWorld("world");
        Bukkit.unloadWorld(delete1, false);
        File deleteFolder1 = ((World) delete1).getWorldFolder();
        deleteWorld(deleteFolder1);

        // TODO: read server.properties for world name
        World delete2 = Bukkit.getWorld("world_nether");
        Bukkit.unloadWorld(delete2, false);
        File deleteFolder2 = ((World) delete2).getWorldFolder();
        deleteWorld(deleteFolder2);

        // TODO: read server.properties for world name
        World delete3 = Bukkit.getWorld("world_the_end");
        Bukkit.unloadWorld(delete3, false);
        File deleteFolder3 = ((World) delete3).getWorldFolder();
        deleteWorld(deleteFolder3);
         */

        // This triggers server auto-save which causes problems
        // Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "restart");

        // So have the server think it crashed instead
        // will auto restart on panel (pterodactyl)
        Runtime.getRuntime().halt(0);

    }

    private int updatePlayerDeathCounter(final String playerName) {
        // Path of player death file
        Path playerDeathFilePath = Path.of("./playerDeaths.json");

        // Create file if not exists
        try {
            if (!Files.exists(playerDeathFilePath)) {
                // System.out.println("Attempting To Create File");
                Files.createFile(playerDeathFilePath);
            }
        } catch (IOException e) {
            // System.out.println("Failed to create playerDeathFile" + playerDeathFilePath.toString());
        }

        try {
            // System.out.println("Creating Death File Reader");
            try (FileReader playerDeathFileReader = new FileReader(playerDeathFilePath.toString())) {
                // System.out.println("Reading JSON from File Reader");
                JsonObject playerDeathFileData;
                if (playerDeathFileReader.read() == -1) {
                    // System.out.println("File is empty...");
                    playerDeathFileData = new JsonObject();
                } else {
                    // System.out.println("File is NOT empty...");
                    String fileContent = Files.readString(playerDeathFilePath);
                    // System.out.println("Read File Content: " + fileContent);
                    playerDeathFileData = JsonParser.parseString(fileContent).getAsJsonObject();
                }
                // Parse the JSON file
                @SuppressWarnings("ReassignedVariable") int playerCurrentDeathCount = 1;
                if (playerDeathFileData.has(playerName)) {
                    // System.out.println("Reading Player Death Count for Existing Player");
                    playerCurrentDeathCount = playerDeathFileData.get(playerName).getAsInt() + 1;
                }
                // System.out.println("Adding Property To PlayerDeathFileData");
                playerDeathFileData.addProperty(playerName, playerCurrentDeathCount);
                // System.out.println("Creating FileWriter");
                try (FileWriter playerDeathFileWriter = new FileWriter(playerDeathFilePath.toString())){
                    Gson gson = new Gson();
                    // System.out.println("GSON to JSON");
                    gson.toJson(playerDeathFileData, playerDeathFileWriter);
                    // System.out.println("Successfully Updated Player Death File");
                    return playerCurrentDeathCount;
                } catch (IOException e) {
                    // System.out.println("Failed to write to playerDeathFile" + playerDeathFilePath.toString());
                    return playerCurrentDeathCount;
                }
            }
        } catch (IOException e) {
            // System.out.println("Failed to read to playerDeathFile" + playerDeathFilePath.toString());
        }
        return 1;
    }

    private Boolean deleteWorld(final String worldName) {
        World world = Bukkit.getWorld(worldName);
        // unload and false to not save
        if (world != null) {
            Bukkit.unloadWorld(world, false);
            File worldFolder = ((World) world).getWorldFolder();
            return this.deleteWorldFiles(worldFolder);
        }
        return false;
    }

    private boolean deleteWorldFiles(final File worldPath) {
        if (worldPath.exists()) {
            final File[] worldFiles = worldPath.listFiles();
            for (int i = 0; i < worldFiles.length; ++i) {
                try {
                    System.out.println("Deleted" + worldFiles[i].getName());
                    if (worldFiles[i].isDirectory()) {
                        this.deleteWorldFiles(worldFiles[i]);
                    }
                    else {
                        worldFiles[i].delete();
                    }
                }
                catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        return worldPath.delete();
    }
}
