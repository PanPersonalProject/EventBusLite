package pan.lib.eventbus;

import java.util.List;

/**
 * Author:         pan qi
 * CreateDate:     2020/5/18 10:50
 */
public class EventBusLite {
    private static EventBusLite eventBusLite;
    private SubscriberMethodFinder subscriberMethodFinder = new SubscriberMethodFinder();

    public static EventBusLite getInstance() {
        if (eventBusLite == null) {
            synchronized (EventBusLite.class) {
                eventBusLite = new EventBusLite();
            }
        }
        return eventBusLite;
    }

    public void register(Object subscriber) {
        Class<?> subscriberClass = subscriber.getClass();
        List<SubscriberMethod> subscriberMethods = subscriberMethodFinder.findSubscriberMethods(subscriberClass);
        synchronized (this) {
            for (SubscriberMethod subscriberMethod : subscriberMethods) {
                subscribe(subscriber, subscriberMethod);
            }
        }
    }

    private void subscribe(Object subscriber, SubscriberMethod subscriberMethod) {

    }

    public void post(Object object) {

    }

    public void unregister(Object subscriber) {

    }
}
