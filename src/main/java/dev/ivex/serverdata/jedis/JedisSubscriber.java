package dev.ivex.serverdata.jedis;

import dev.ivex.serverdata.ServerData;
import dev.ivex.serverdata.data.ServerManager;
import dev.ivex.serverdata.utilites.Color;
import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.permissions.ServerOperator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

@Getter
public class JedisSubscriber {

    private JedisPubSub jedisPubSub;
    private Jedis jedis;

    public JedisSubscriber() {

        jedis = new Jedis("127.0.0.1", 6379);

        Bukkit.getConsoleSender().sendMessage(Color.translate("&3[ServerData] &aConnection with Redis has been established."));

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
                            Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(player -> player.sendMessage(Color.translate("&e[S] &bServer &e&l" + ServerData.getServerName() + " &bis now &aonline.")));
                            Bukkit.getConsoleSender().sendMessage(Color.translate("&e[S] &bServer &e&l" + ServerData.getServerName() + " &bis now &aonline."));
                            break;
                        case "serverstop":
                            Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(player -> player.sendMessage(Color.translate("&e[S] &bServer &e&l" + ServerData.getServerName() + " &bis now &cofflinee.")));
                            Bukkit.getConsoleSender().sendMessage(Color.translate("&e[S] &bServer &e&l" + ServerData.getServerName() + " &bis now &coffline."));
                            break;
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