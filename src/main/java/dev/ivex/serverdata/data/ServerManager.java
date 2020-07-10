package dev.ivex.serverdata.data;

import dev.ivex.serverdata.ServerData;
import dev.ivex.serverdata.jedis.JedisPublisher;
import dev.ivex.serverdata.utilites.Color;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerManager {

    @Getter public static Map<String, ServerManager> servers = new HashMap<>();

    private final String serverName = ServerData.getInstance().getConfig().getString("SERVER_NAME");

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

        Bukkit.getScheduler().scheduleSyncRepeatingTask(ServerData.getInstance(), () -> {
            JedisPublisher.handleWrite(
                    ServerData.getInstance().getConfig().getString("DATABASE.REDIS.CHANNEL") + ";",
                    "dataUpdate;"
                            + ServerData.getServerName() + ";"
                            + Bukkit.getServer().getMotd() + ";"
                            + Bukkit.getOnlinePlayers().size() + ";"
                            + Bukkit.getMaxPlayers() + ";" + Bukkit.spigot().getTPS()[0] + ";" + Bukkit.hasWhitelist());


        }, 40L, 20L);
    }

    public boolean isOnline() {
        return System.currentTimeMillis() - lastUpdate < 10000L;
    }

    public List<ServerManager> getAllServersData() {
        return new ArrayList<>(servers.values());
    }

    public ServerManager getByName(String name) {
        return getAllServersData().stream().filter(server -> server.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
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

    public void addServer(String name, ServerManager server) {
        servers.put(name, server);
    }

    public void removeServer(String name) {
        servers.remove(getByName(name).getName());
    }

    public void onClose() {
        JedisPublisher.handleWrite(ServerData.getInstance().getConfig().getString("DATABASE.REDIS.CHANNEL") + ";", "broadcast;" + Color.translate(ServerData.getInstance().getConfig().getString("MESSAGE.OFFLINE")).replace("%server%", String.valueOf(ServerData.getServerName())));
        JedisPublisher.handleWrite(ServerData.getInstance().getConfig().getString("DATABASE.REDIS.CHANNEL") + ";", "remove;" + serverName);
    }

}