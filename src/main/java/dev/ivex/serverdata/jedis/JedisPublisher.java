package dev.ivex.serverdata.jedis;

import dev.ivex.serverdata.ServerData;
import redis.clients.jedis.Jedis;

public class JedisPublisher {

    public static void handleWrite(String channel, String message) {
        Jedis jedis = null;
        try {
            jedis = ServerData.getInstance().getPool().getResource();
            jedis.publish("serverdata", message);
        }
        finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
