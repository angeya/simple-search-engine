package top.angeya.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.angeya.dao.WebPageInfoMapper;
import top.angeya.entity.WebPageInfo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: angeya
 * @date: 2023/11/1 23:12
 * @description:
 */
@Service
public class WebPageInfoService extends ServiceImpl<WebPageInfoMapper, WebPageInfo> {

    /**
     * 获取数据URL，避免重复
     *
     * @return set
     */
    public Set<String> getUrlSetFromDb() {
        List<WebPageInfo> webDataList = this.list(Wrappers.lambdaQuery(WebPageInfo.class)
                .select(WebPageInfo::getUrl));
        return webDataList.stream().map(WebPageInfo::getUrl).collect(Collectors.toSet());
    }
}
