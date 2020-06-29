package dev.ivex.serverdata.jedis;

import dev.ivex.serverdata.ServerData;
import dev.ivex.serverdata.data.ServerManager;
import dev.ivex.serverdata.utilites.Color;
import dev.ivex.serverdata.utilites.ServerUtils;
import lombok.Getter;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.UUID;

@Getter
public class JedisSubscriber {

    private JedisPubSub jedisPubSub;
    private Jedis jedis;

    public JedisSubscriber() {
        jedis = new Jedis(ServerData.getInstance().getConfig().getString("REDIS.ADDRES"), ServerData.getInstance().getConfig().getInt("REDIS.PORT"));

        if(ServerData.getInstance().getConfig().getBoolean("REDIS.AUTH")) {
            jedis.auth(ServerData.getInstance().getConfig().getString("REDIS.AUTH.PASSWORD"));
        }

        handleSubscribe();
    }

    private void handleSubscribe() {
        jedisPubSub = handlePubSub();
        new Thread(() -> jedis.subscribe(jedisPubSub, "serverdata")).start();
    }

    private JedisPubSub handlePubSub() {
        return new JedisPubSub() {
            public void onMessage(String channel, String message) {
                if (channel.equalsIgnoreCase("serverdata")) {
                    String[] args = message.split(";");
                    String command = args[0];

                    switch (command) {
                        case "serverstart":
                            Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(player -> player.sendMessage(Color.translate("&5[S] &dServer &d&l" + ServerData.getInstance().getConfig().getString("SERVERNAME") + " &dis now &aonline.")));
                            ServerUtils.logConsole("&5[S] &dServer &d&l" + ServerData.getInstance().getConfig().getString("SERVERNAME") + " &dis now &aonline.");
                            break;
                        case "serverstop":
                            Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(player -> player.sendMessage(Color.translate("&5[S] &dServer &d&l" + ServerData.getInstance().getConfig().getString("SERVERNAME") + " &dis now &cofflinee.")));
                            ServerUtils.logConsole("&5[S] &dServer &d&l" + ServerData.getInstance().getConfig().getString("SERVERNAME") + " &dis now &coffline.");
                        case "dataUpdate":
                            ServerManager data = ServerManager.getByName(args[1]);
                            if (data == null) {
                                ServerManager.getServers().put(args[1], data = new ServerManager());
                            }
                            data.setLastUpdate(System.currentTimeMillis());
                            data.setMotd(args[2]);
                            data.setOnlinePlayers(Integer.parseInt(args[3]));
                            data.setMaxPlayers(Integer.parseInt(args[4]));
                            data.setTps(Double.parseDouble(args[5]));
                            data.setWhitelisted(Boolean.parseBoolean(args[6]));
                            break;
                        case "dataRemove":
                            ServerManager.getServers().remove(args[2]);
                    }
                }
            }
        };
    }
}