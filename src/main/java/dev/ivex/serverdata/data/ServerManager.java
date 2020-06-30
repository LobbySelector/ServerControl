package dev.ivex.serverdata.data;

import dev.ivex.serverdata.ServerData;
import dev.ivex.serverdata.jedis.JedisPublisher;
import dev.ivex.serverdata.utilites.Color;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerManager {

    @Getter public static Map<String, ServerManager> servers = new HashMap<>();

    @Getter @Setter
    public String name, motd;
    @Getter @Setter
    public int onlinePlayers, maxPlayers;
    @Getter @Setter
    public long lastUpdate;
    @Getter @Setter
    public boolean whitelisted;
    @Getter @Setter
    public double tps;
    @Getter
    public long uptime = System.currentTimeMillis();

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

    public long getUpTime() {
        return System.currentTimeMillis() - this.uptime;
    }

    public String getStatus() {
        String status = "&cOffline";

        if (isOnline()) status = "&aOnline";
        if (isWhitelisted()) status = "&cWhitelisted";

        return Color.translate(status);
    }
}