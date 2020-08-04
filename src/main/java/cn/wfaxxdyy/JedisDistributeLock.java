package cn.wfaxxdyy;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;


//注意：redis指令可以实现分布式锁，操作如下：setnx成功返回1，失败返回0，其他指令expire（到时间就删除键），del
//这里采用jedis实现
public class JedisDistributeLock {

    public static void main(String[] args) {

        //连接池参数设置类，当然可以封装在一个方法里，这里就不搞了
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //设置最大连接数
        jedisPoolConfig.setMaxTotal(10);

        //创建连接池
        //参数timeout默认2000，port6379，数据库db0
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "localhost");

        //获取jedis连接
        Jedis jedis = jedisPool.getResource();

        //输出测试
        /*String result = jedis.set("法外狂徒", "张三");
        System.out.println(result);*/

        //jedis分布式锁测试,获取锁成功返回OK，失败返回null，因为设置过期时间2秒所以查询redis中不会有数据
        String result = jedis.set("1", "requestid1", "NX", "PX", 2000);
        String result1 = jedis.set("1", "requestid2", "NX", "PX", 2000);
        System.out.println("加锁："+result);
        System.out.println("加锁："+result1);
        System.out.println("获取锁："+jedis.get("1"));

        //释放锁,lua脚本保持原子性，解决误删锁的缺点，请参考博客https://www.cnblogs.com/moxiaotao/p/10829799.html
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result3 = jedis.eval(script, Collections.singletonList("1"), Collections.singletonList("requestid1"));
        System.out.println("释放锁："+result3);
        System.out.println("获取锁："+jedis.get("1"));
    }

}
