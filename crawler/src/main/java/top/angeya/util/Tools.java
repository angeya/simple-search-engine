package top.angeya.util;

import top.angeya.constant.CommonConstant;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author: angeya
 * @date: 2023/10/25 22:49
 * @description: 工具类
 */
public class Tools {

    /**
     * 私有构造器
     */
    private Tools() {
    }

    private static final Random RANDOM = new Random();


    /**
     * URL匹配模板
     */
    private static final Pattern WEB_URL_PATTERN = Pattern.compile(CommonConstant.WEB_URL_REG);

    /**
     * 判断url是否正确
     *
     * @param url 地址
     * @return 是否有效
     */
    public static boolean isUrlValid(String url) {
        return WEB_URL_PATTERN.matcher(url).matches();
    }

    /**
     * 请求休眠 随机休眠时间
     *
     * @throws InterruptedException 中断异常
     */
    public static void requestSleep() throws InterruptedException {
        Thread.sleep(RANDOM.nextInt(1000));
    }

}
