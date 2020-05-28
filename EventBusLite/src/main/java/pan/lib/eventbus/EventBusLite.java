package pan.lib.eventbus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Author:         pan qi
 * CreateDate:     2020/5/18 10:50
 */
public class EventBusLite {
    private static EventBusLite eventBusLite;
    private SubscriberMethodFinder subscriberMethodFinder;

    //订阅该参数类型--对象列表
    private final Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType;


    public static EventBusLite getInstance() {
        if (eventBusLite == null) {
            synchronized (EventBusLite.class) {
                eventBusLite = new EventBusLite();
            }
        }
        return eventBusLite;
    }

    public EventBusLite() {
        subscriberMethodFinder = new SubscriberMethodFinder();
        subscriptionsByEventType = new HashMap<>();
    }

    public void register(Object subscriber) {
        if (subscriber == null) return;
        Class<?> subscriberClass = subscriber.getClass();
        List<SubscriberMethod> subscriberMethods = subscriberMethodFinder.findSubscriberMethods(subscriberClass);
        synchronized (this) {
            for (SubscriberMethod subscriberMethod : subscriberMethods) {
                subscribe(subscriber, subscriberMethod);
            }
        }
    }

    private void subscribe(Object subscriber, SubscriberMethod subscriberMethod) {
        Class<?> parameterTypes = subscriberMethod.eventTypes;
        CopyOnWriteArrayList<Subscription> subscriptionList = subscriptionsByEventType.get(parameterTypes);
        if (subscriptionList == null) {
            subscriptionList = new CopyOnWriteArrayList<>();
            subscriptionsByEventType.put(parameterTypes, subscriptionList);
        }

        Subscription subscription = new Subscription(subscriber, subscriberMethod);
        if (subscriptionList.contains(subscription)) {
            throw new RuntimeException("Subscriber " + subscriber.getClass() + " already registered to event "
                    + parameterTypes);
        }

        subscriptionList.add(subscription);

    }

    private void unsubscribe(Object subscriber, SubscriberMethod subscriberMethod) {
        CopyOnWriteArrayList<Subscription> subscriptions = subscriptionsByEventType.get(subscriberMethod.eventTypes);
        if (subscriptions == null) return;

        Subscription tempSubscription = new Subscription(subscriber, subscriberMethod);

        Iterator<Subscription> iterator = subscriptions.iterator();
        while (iterator.hasNext()) {
            Subscription subscription = iterator.next();
            if (subscription.equals(tempSubscription)) {
                iterator.remove();
            }
        }
    }

    public void post(Object object) {
        CopyOnWriteArrayList<Subscription> subscriptionList = subscriptionsByEventType.get(object.getClass());
        for (Subscription subscription : subscriptionList) {
            invokeSubscribeMethod(subscription, object);
        }

    }

    private void invokeSubscribeMethod(Subscription subscription, Object event) {
        try {
            subscription.subscriberMethod.method.invoke(subscription.subscriber, event);
        } catch (Exception e) {
            throw new RuntimeException("ERROR " + e.getMessage());

        }
    }

    public void unregister(Object subscriber) {
        if (subscriber == null) return;
        List<SubscriberMethod> subscriberMethods = subscriberMethodFinder.findSubscriberMethods(subscriber.getClass());
        synchronized (this) {
            for (SubscriberMethod subscriberMethod : subscriberMethods) {
                unsubscribe(subscriber, subscriberMethod);
            }
        }
    }
}
