package top.angeya.crawler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;

/**
 * @author: angeya
 * @date: 2023/11/7 23:32
 * @description:
 */
public class MysqlScheduler extends DuplicateRemovedScheduler {

    @Override
    public Request poll(Task task) {
        return null;
    }
}
