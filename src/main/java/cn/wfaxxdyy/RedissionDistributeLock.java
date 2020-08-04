package cn.wfaxxdyy;

import redis.clients.jedis.Jedis;

public class RedissionDistributeLock {
    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        String s = jedis.get("1");
        System.out.println(s);
    }
}
