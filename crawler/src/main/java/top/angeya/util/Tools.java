package top.angeya.util;

import top.angeya.constant.CommonConstant;

import java.util.regex.Pattern;

/**
 * @author: angeya
 * @date: 2023/10/25 22:49
 * @description: 工具类
 */
public class Tools {

    /**
     * URL匹配模板
     */
    private static final Pattern WEB_URL_PATTERN = Pattern.compile(CommonConstant.WEB_URL_REG);

    /**
     * 私有构造器
     */
    private Tools(){}

    /**
     * 判断url是否正确
     * @param url 地址
     * @return 是否有效
     */
    public static boolean isUrlValid(String url) {
        return WEB_URL_PATTERN.matcher(url).matches();
    }

}
