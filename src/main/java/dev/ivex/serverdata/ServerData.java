package dev.ivex.serverdata;

import com.sun.corba.se.spi.activation.Server;
import dev.ivex.serverdata.commands.ServerDataCommand;
import dev.ivex.serverdata.data.ServerManager;
import dev.ivex.serverdata.jedis.JedisPublisher;
import dev.ivex.serverdata.jedis.JedisSubscriber;
import dev.ivex.serverdata.utilites.ConfigFile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import sun.security.krb5.Config;

import java.lang.reflect.Field;

public class ServerData extends JavaPlugin {

    @Getter public static ServerData instance;
    @Getter public static CommandMap commandMap;
    @Getter public JedisPool pool;
    @Getter public JedisSubscriber jedisSubscriber;
    @Getter public ServerManager serverManager;
    @Getter public ConfigFile configFile;

    public void onEnable() {
        instance = this;

        handleRedis();
        setupCommandMap();
        registerCommand();
        registerOther();
        Bukkit.getScheduler().runTaskLater(ServerData.getInstance(), () -> JedisPublisher.handleWrite("serverdata", "serverstart"), 20L * 3);
    }

    public void onDisable() {
        JedisPublisher.handleWrite("serverdata", "serverstop");
        jedisSubscriber.getJedisPubSub().unsubscribe();
        pool.destroy();
    }

    public static String getServerName() {
        return "Dev";
    }

    public void registerCommand() {
        new ServerDataCommand();
    }

    public void registerOther() {
        new ServerManager();
        new ConfigFile();
    }

    public void handleRedis() { ;
        pool = new JedisPool("127.0.0.1", 6379);
        jedisSubscriber = new JedisSubscriber();
    }


    public void setupCommandMap(){
        try {
            if (Bukkit.getServer() instanceof CraftServer) {
                Field field = CraftServer.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                commandMap = (CommandMap) field.get(Bukkit.getServer());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
