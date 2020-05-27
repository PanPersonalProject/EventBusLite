package pan.lib.eventbus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author:         pan qi
 * CreateDate:     2020/5/27 15:31
 */
public class SubscriberMethodFinder {

    private static final Map<Class<?>, List<SubscriberMethod>> METHOD_CACHE = new ConcurrentHashMap<>();

    public List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {

        List<SubscriberMethod> cacheSubscriberMethods = METHOD_CACHE.get(subscriberClass);
        if (cacheSubscriberMethods != null) {
            return cacheSubscriberMethods;
        }

        List<SubscriberMethod> subscriberMethodList = new ArrayList<>();
        while (subscriberClass != null) {
            for (Method method : subscriberClass.getDeclaredMethods()) {
                Subscribe subscribe = method.getAnnotation(Subscribe.class);
                if (subscribe == null) continue;

                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new RuntimeException("EventBus订阅的函数必需是一个参数");
                }
                SubscriberMethod subscriberMethod = new SubscriberMethod(method);
                subscriberMethodList.add(subscriberMethod);
            }

            METHOD_CACHE.put(subscriberClass, subscriberMethodList);
            subscriberClass = subscriberClass.getSuperclass();
        }

        return subscriberMethodList;
    }
}
