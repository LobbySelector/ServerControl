package dev.ivex.serverdata.utilites;

import org.bukkit.Bukkit;

public class ServerUtils {

    public static void logConsole(String text) {
        Bukkit.getConsoleSender().sendMessage(Color.translate(text));
    }
}
