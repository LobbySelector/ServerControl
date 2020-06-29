package dev.ivex.serverdata.data;

import dev.ivex.serverdata.ServerData;
import dev.ivex.serverdata.jedis.JedisPublisher;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class ServerManager {

    @Getter public static Map<String, ServerManager> servers = new HashMap<>();

    @Getter @Setter
    private String name, motd;
    @Getter @Setter
    private int onlinePlayers, maxPlayers;
    @Getter @Setter
    private long lastUpdate;
    @Getter @Setter
    private boolean whitelisted;
    @Getter @Setter
    private double tps;

    public ServerManager() {
        this.name = ServerData.getServerName();

        Bukkit.getScheduler().runTaskTimerAsynchronously(ServerData.getInstance(), () -> {
            JedisPublisher.handleWrite(
                    "serverdata",
                    "dataUpdate;"
                            + this.name + ";"
                            + Bukkit.getServer().getMotd() + ";"
                            + Bukkit.getOnlinePlayers().size() + ";"
                            + Bukkit.getMaxPlayers() + ";" + Bukkit.spigot().getTPS()[0] + ";" + Bukkit.hasWhitelist());


        }, 40L, 20L);
    }

    public boolean isOnline() {
        return System.currentTimeMillis() - this.lastUpdate < 20000L;
    }

    public static ServerManager getByName(String name) {
        return servers.values().stream().filter(serverManager -> serverManager.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public String getTranslatedStatus() {
        String status;
        if (isOnline() && !isWhitelisted()) {
            status = ChatColor.GREEN + ("Online");
        } else if (isOnline() && isWhitelisted()) {
            status = ChatColor.YELLOW + ("Whitelisted");
        } else if (!isOnline()) {
            status = ChatColor.RED + ("Offline");
        } else {
            status = ChatColor.RED + ("Offline");
        }
        return status;
    }
}