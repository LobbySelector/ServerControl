package dev.ivex.serverdata.jedis;

import dev.ivex.serverdata.ServerData;
import dev.ivex.serverdata.data.ServerManager;
import dev.ivex.serverdata.utilites.Color;
import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.ServerOperator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.List;

@Getter
public class JedisSubscriber {

    private JedisPubSub jedisPubSub;
    private Jedis jedis;

    private String address;
    private int port;

    public JedisSubscriber() {
        address = ServerData.getInstance().getConfig().getString("DATABASE.REDIS.ADRESS");
        port = ServerData.getInstance().getConfig().getInt("DATABASE.REDIS.PORT");

        jedis = new Jedis(address, port);
        if(ServerData.getInstance().getConfig().getBoolean("DATABASE.REDIS.AUTHENTICATION.ENABLED")) {
            jedis.auth(ServerData.getInstance().getConfig().getString("DATABASE.REDIS.AUTHENTICATION.PASSWORD"));
        }

        Bukkit.getConsoleSender().sendMessage(Color.translate("&6[ServerControl] &aConnection with Redis has been established."));

        handleSubscribe();
    }

    private void handleSubscribe() {
        jedisPubSub = handlePubSub();
        new Thread(() -> jedis.subscribe(jedisPubSub, ServerData.getInstance().getConfig().getString("DATABASE.REDIS.CHANNEL"))).start();
    }

    private JedisPubSub handlePubSub() {
        return new JedisPubSub() {
            public void onMessage(String channel, String channelmessage) {
                if (channel.equalsIgnoreCase(ServerData.getInstance().getConfig().getString("DATABASE.REDIS.CHANNEL"))) {
                    String[] args = channelmessage.split(";");
                    String command = args[0];

                     switch (command) {
                         case "remove":
                             ServerData.getInstance().getServerManager().removeServer(args[1]);
                             break;
                            case "broadcast":
                                String message = args[1];
                                Bukkit.getConsoleSender().sendMessage(Color.translate(message));
                                Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(ServerData.getInstance().getConfig().getString("MESSAGE.PERMISSION"))).forEach(player -> player.sendMessage(Color.translate(message)));
                            break;
                            case "dataUpdate":
                                ServerManager data = ServerData.getInstance().getServerManager().getByName(args[1]);
                                if (data == null) {
                                    data = new ServerManager();
                                    ServerData.getInstance().getServerManager().addServer(args[1], data);
                                }

                                data.setName(args[1]);
                                data.setLastUpdate(System.currentTimeMillis());
                                data.setMotd(args[2]);
                                data.setOnlinePlayers(Integer.parseInt(args[3]));
                                data.setMaxPlayers(Integer.parseInt(args[4]));
                                data.setTps(Double.parseDouble(args[5]));
                                data.setWhitelisted(Boolean.parseBoolean(args[6]));
                                break;
                            case "command": {
                                if (args[1].equalsIgnoreCase("all")) {
                                    Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), args[2]);
                                    return;
                                }
                                if (ServerData.getServerName().equalsIgnoreCase(args[1])) {
                                    Bukkit.dispatchCommand((CommandSender) Bukkit.getConsoleSender(), args[2]);
                                    return;
                                }
                            }
                            break;
                        }
                }
            }
        };
    }
}