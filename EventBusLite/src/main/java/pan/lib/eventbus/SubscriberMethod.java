package pan.lib.eventbus;

import java.lang.reflect.Method;

/**
 * Author:         pan qi
 * CreateDate:     2020/5/27 15:21
 */
public class SubscriberMethod {
    Method method;

    public SubscriberMethod(Method method) {
        this.method = method;
    }
}
