package dev.ivex.serverdata;

import dev.ivex.serverdata.commands.RunCmdCommand;
import dev.ivex.serverdata.commands.ServerDataCommand;
import dev.ivex.serverdata.data.ServerManager;
import dev.ivex.serverdata.jedis.JedisPublisher;
import dev.ivex.serverdata.jedis.JedisSubscriber;
import dev.ivex.serverdata.utilites.Color;
import dev.ivex.serverdata.utilites.ConfigFile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Field;

public class ServerData extends JavaPlugin {

    @Getter public static ServerData instance;
    @Getter public static CommandMap commandMap;
    @Getter public JedisPool pool;
    @Getter public JedisSubscriber jedisSubscriber;
    @Getter public ServerManager serverManager;
    @Getter public ConfigFile config;
    private String address;
    private int port;

    public void onEnable() {
        instance = this;

        config = new ConfigFile(this,"config.yml");

        handleRedis();
        registerServer();
        setupCommandMap();
        registerCommand();
        JedisPublisher.handleWrite(ServerData.getInstance().getConfig().getString("DATABASE.REDIS.CHANNEL") + ";", "broadcast;" + Color.translate(ServerData.getInstance().getConfig().getString("MESSAGE.ONLINE")).replace("%server%", String.valueOf(getServerName())));
    }

    public void onDisable() {
        JedisPublisher.handleWrite(ServerData.getInstance().getConfig().getString("DATABASE.REDIS.CHANNEL") + ";", "broadcast;" + Color.translate(ServerData.getInstance().getConfig().getString("MESSAGE.OFFLINE")).replace("%server%", String.valueOf(ServerData.getServerName())));
        JedisPublisher.handleWrite(ServerData.getInstance().getConfig().getString("DATABASE.REDIS.CHANNEL") + ";", "remove;" + ServerData.getServerName());
        jedisSubscriber.getJedis().close();
    }

    public static String getServerName() {
        return getInstance().getConfig().getString("SERVER_NAME");
    }

    public void registerCommand() {
        new ServerDataCommand();
        new RunCmdCommand();
    }

    public void registerServer() {
        serverManager = new ServerManager();
    }

    public void handleRedis() {
        address = ServerData.getInstance().getConfig().getString("DATABASE.REDIS.ADRESS");
        port = ServerData.getInstance().getConfig().getInt("DATABASE.REDIS.PORT");
        pool = new JedisPool(address, port);
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
