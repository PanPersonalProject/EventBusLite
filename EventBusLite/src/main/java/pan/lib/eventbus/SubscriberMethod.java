package pan.lib.eventbus;

import java.lang.reflect.Method;

/**
 * Author:         pan qi
 * CreateDate:     2020/5/27 15:21
 */
public class SubscriberMethod {
    Method method;
    Class<?> eventTypes;
    ThreadMode threadMode;
    String methodString;

    public SubscriberMethod(Method method, Class<?> eventTypes, ThreadMode threadMode) {
        this.method = method;
        this.eventTypes = eventTypes;
        this.threadMode = threadMode;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else if (other instanceof SubscriberMethod) {
            checkMethodString();
            SubscriberMethod otherSubscriberMethod = (SubscriberMethod) other;
            otherSubscriberMethod.checkMethodString();
            return methodString.equals(otherSubscriberMethod.methodString);
        } else {
            return false;
        }
    }

    private synchronized void checkMethodString() {
        if (methodString == null) {
            methodString = method.getDeclaringClass().getName() +
                    '#' + method.getName() +
                    '(' + eventTypes.getName();
        }
    }

    @Override
    public int hashCode() {
        return method.hashCode();
    }

}
