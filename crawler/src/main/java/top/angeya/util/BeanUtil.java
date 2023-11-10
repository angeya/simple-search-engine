package top.angeya.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Bean工具类
 *
 * @author: angeya
 * @date: 2023/11/10 21:43
 * @description:
 */
@Component
public class BeanUtil implements ApplicationContextAware {

    /**
     * spring上下文
     */
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 根据类型获取bean
     *
     * @param clazz 类型
     * @param <T>   类型
     * @return bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    /**
     * 根据类型获取bean
     *
     * @param name  bean名称
     * @param clazz 类型
     * @param <T>   类型
     * @return bean实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }
}
