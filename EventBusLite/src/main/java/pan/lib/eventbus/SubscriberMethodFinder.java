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
        for (Method method : subscriberClass.getMethods()) {
            Subscribe subscribe = method.getAnnotation(Subscribe.class);
            if (subscribe != null) {
                SubscriberMethod subscriberMethod = new SubscriberMethod(method);
                subscriberMethodList.add(subscriberMethod);
            }
        }

        METHOD_CACHE.put(subscriberClass, subscriberMethodList);
        return subscriberMethodList;
    }
}
