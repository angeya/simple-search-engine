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
        if (UrlQueueStorageType.MYSQL.getCode().equals(this.urlQueueStorageType)) {
            return new MysqlScheduler();
        } else if (UrlQueueStorageType.REDIS.getCode().equals(this.urlQueueStorageType)) {
            JedisPool jedisPool = new JedisPool(new GenericObjectPoolConfig<>(), redisProperties.getHost(),
                    redisProperties.getPort(), (int) redisProperties.getTimeout().getSeconds(),
                    redisProperties.getPassword());
            return new RedisScheduler(jedisPool);
        }
        return new FileCacheQueueScheduler(this.urlCacheFilePath);
    }

}
