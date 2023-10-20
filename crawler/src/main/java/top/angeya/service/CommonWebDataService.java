package top.angeya.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.angeya.dao.CommonWebDataMapper;
import top.angeya.entity.CommonWebData;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

/**
 * @Author: wanganjie 5790
 * @Date: 2023/10/19 19:57
 * @Description:
 */
@Slf4j
@Service
public class CommonWebDataService extends ServiceImpl<CommonWebDataMapper, CommonWebData> {

    private final BlockingQueue<CommonWebData> webDataQueue = new LinkedBlockingDeque<>(1024);

    /**
     * 搬运数据
     */
    public void startCarryWebData() {
        while(true) {
            try {
                int queueSize = webDataQueue.size();
                log.info("web data queue size is {}", queueSize);
                CommonWebData webData = webDataQueue.take();
                this.saveWebData(webData);
            } catch (InterruptedException e) {
                log.error("get web data and save error", e);
            }
        }
    }

    /**
     * 把网页数据添加到队列
     * @param webData 数据
     */
    public void addWebDataToQueue(CommonWebData webData) {
        try {
            this.webDataQueue.put(webData);
        } catch (InterruptedException e) {
            log.error("add web data to queue error", e);
        }
    }

    /**
     * 获取数据URL，避免重复
     * @return set
     */
    public Set<String> getUrlSetFromDb() {
        List<CommonWebData> webDataList = this.list(Wrappers.lambdaQuery(CommonWebData.class)
                .select(CommonWebData::getUrl));
        return webDataList.stream().map(CommonWebData::getUrl).collect(Collectors.toSet());
    }


    /**
     * 判断网页是否存在
     * @param url 网页地址
     * @return 是否存在
     */
    public boolean isWebExist(String url) {
        long count = this.count(Wrappers.lambdaQuery(CommonWebData.class).eq(CommonWebData::getUrl, url));
        return count > 0;
    }

    /**
     * 保存数据
     * @param webData 网页信息
     * @return 保存结果
     */
    public boolean saveWebData(CommonWebData webData) {
        this.save(webData);
        return true;
    }

}
