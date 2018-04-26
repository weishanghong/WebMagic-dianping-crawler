package com.weiresearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author WeekiXu
 */
@Component
public class JedisClientSingleton {

    @Autowired
    JedisPool jedisPool;

    /**
     * 获取key的value值
     */
    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        String str = "";
        try {
            str = jedis.get(key);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public String set(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        String str = "";
        try {
            str = jedis.set(key, value);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public String setex(String key, int seconds, String value) {
        Jedis jedis = jedisPool.getResource();
        String str = "";
        try {
            str = jedis.setex(key, seconds, value);
        } finally {
            try {
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }
}
