package dev.ivex.serverdata;

import dev.ivex.serverdata.jedis.JedisSubscriber;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;

public class ServerData extends JavaPlugin {

    @Getter public static ServerData instance;
    @Getter public JedisPool pool;
    @Getter public JedisSubscriber jedisSubscriber;

    public void onEnable() {
        instance = this;

        handleRedis();
    }

    public void onDisable() {

    }

    public static String getServerName() {
        return ServerData.getInstance().getConfig().getString("SERVERNAME");
    }

    private void handleRedis() {
        pool = new JedisPool(ServerData.getInstance().getConfig().getString("REDIS.ADDRES"), ServerData.getInstance().getConfig().getInt("REDIS.PORT"));
        jedisSubscriber = new JedisSubscriber();
    }
}
