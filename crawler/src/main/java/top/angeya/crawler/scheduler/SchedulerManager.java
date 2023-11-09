package top.angeya.crawler.scheduler;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import top.angeya.enums.UrlQueueStorageType;
import us.codecraft.webmagic.scheduler.FileCacheQueueScheduler;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * 调度器管理器
 *
 * @Author: 5790
 * @Date: 2023/11/9 17:26
 * @Description:
 */
@Component
public class SchedulerManager {

    @Value("${crawler.url-file-path}")
    private String urlCacheFilePath;

    @Value("${crawler.url-queue-storage-type}")
    private String urlQueueStorageType;

    @Resource
    private RedisProperties redisProperties;


    /**
     * 获取调度器 默认使用文件调度器
     *
     * @return 调度器
     */
    public Scheduler getSchedule() {
        // 根据配置创建不同的调度器
        if (UrlQueueStorageType.MYSQL.getCode().equals(this.urlQueueStorageType)) {
            return new MysqlScheduler();
        } else if (UrlQueueStorageType.REDIS.getCode().equals(this.urlQueueStorageType)) {
            return this.getRedisScheduler();
        }
        return new FileCacheQueueScheduler(this.urlCacheFilePath);
    }

    /**
     * 获取redis调度器
     *
     * @return redis调度器
     */
    private Scheduler getRedisScheduler() {
        // 默认redis超时
        int timeout = 10000;
        Duration timeoutDuration = redisProperties.getTimeout();
        if (timeoutDuration != null) {
            timeout = (int) timeoutDuration.getSeconds();
        }
        // 创建redis连接池
        JedisPool jedisPool = new JedisPool(new GenericObjectPoolConfig<>(), redisProperties.getHost(),
                redisProperties.getPort(), timeout, redisProperties.getPassword());
        return new RedisScheduler(jedisPool);
    }

}
