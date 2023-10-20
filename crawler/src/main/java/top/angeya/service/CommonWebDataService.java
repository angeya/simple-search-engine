package top.angeya.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.angeya.dao.CommonWebDataMapper;
import top.angeya.entity.CommonWebData;

/**
 * @Author: wanganjie 5790
 * @Date: 2023/10/19 19:57
 * @Description:
 */
@Service
public class CommonWebDataService extends ServiceImpl<CommonWebDataMapper, CommonWebData> {

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
