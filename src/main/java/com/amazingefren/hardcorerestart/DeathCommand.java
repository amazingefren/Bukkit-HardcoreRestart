package com.amazingefren.hardcorerestart;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * `/death` command will send a global message to list all player death counts, if any
 */
public class DeathCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        JsonObject allPlayerDeathJson = this.getAllPlayerDeathCounts();

        StringBuilder message = new StringBuilder();

        if (allPlayerDeathJson != null) {
            // Create json attrs map for iteration
            Map<String, Object> attrs = new HashMap<String, Object>();
            Set<Entry<String, JsonElement>> entrySet = allPlayerDeathJson.entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                attrs.put(entry.getKey(), allPlayerDeathJson.get(entry.getKey()));
            }

            // Create Border Styling
            message.append(ChatColor.GRAY).append("====================\n");
            // Iter through JSON key value pairs ("playerName": 2 -- death count)
            for (Map.Entry<String, Object> attr : attrs.entrySet()) {
                // Append to output message
                message
                        .append(ChatColor.GREEN)
                        .append(attr.getKey())
                        .append(ChatColor.WHITE)
                        .append(": ")
                        .append(ChatColor.RED).append(attr.getValue())
                        .append(" deaths\n");
            }
            message.append(ChatColor.GRAY).append("====================");
        }

        // Send to global chat
        Bukkit.broadcastMessage(message.toString());
        return true;
    }

    private JsonObject getAllPlayerDeathCounts() {
        // Path of player death file
        Path playerDeathFilePath = Path.of("./playerDeaths.json");

        // Create file if not exists
        if (!Files.exists(playerDeathFilePath)) {
            Bukkit.broadcastMessage("No one has died.");
            return null;
        }

        try {
            // System.out.println("Creating Death File Reader");
            try (FileReader playerDeathFileReader = new FileReader(playerDeathFilePath.toString())) {
                // System.out.println("Reading JSON from File Reader");
                if (playerDeathFileReader.read() == -1) {
                    Bukkit.broadcastMessage("No one has died.");
                    return null;
                } else {
                    String fileContent = Files.readString(playerDeathFilePath);
                    return JsonParser.parseString(fileContent).getAsJsonObject();
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to read to playerDeathFile" + playerDeathFilePath.toString());
            return null;
        }
    }
}
