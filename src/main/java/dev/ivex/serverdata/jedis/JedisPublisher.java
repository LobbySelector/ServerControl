package dev.ivex.serverdata.jedis;

import dev.ivex.serverdata.ServerData;
import org.bukkit.Server;
import redis.clients.jedis.Jedis;

public class JedisPublisher {

    public static void handleWrite(String channel, String channelmessage) {
        Jedis jedis = null;
        try {
            jedis = ServerData.getInstance().getPool().getResource();
            jedis.publish(ServerData.getInstance().getConfig().getString("DATABASE.REDIS.CHANNEL"), channelmessage);
            if(ServerData.getInstance().getConfig().getBoolean("DATABASE.REDIS.AUTHENTICATION.ENABLED")) {
                jedis.auth(ServerData.getInstance().getConfig().getString("DATABASE.REDIS.AUTHENTICATION.PASSWORD"));
            }
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
