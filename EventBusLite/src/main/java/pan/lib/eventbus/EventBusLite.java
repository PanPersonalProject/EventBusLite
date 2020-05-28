package pan.lib.eventbus;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author:         pan qi
 * CreateDate:     2020/5/18 10:50
 */
public class EventBusLite {
    private static EventBusLite eventBusLite;
    private SubscriberMethodFinder subscriberMethodFinder;
    private Handler mainHandler;
    private ExecutorService executorService;//线程池

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
        mainHandler = new Handler(Looper.getMainLooper());
        executorService = Executors.newCachedThreadPool();
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

    public void post(final Object object) {
        CopyOnWriteArrayList<Subscription> subscriptionList = subscriptionsByEventType.get(object.getClass());
        if (subscriptionList == null) return;
        for (final Subscription subscription : subscriptionList) {
            switch (subscription.subscriberMethod.threadMode) {
                case MAIN:
                    if (Looper.getMainLooper() == Looper.myLooper()) {
                        invokeSubscribeMethod(subscription, object);
                    } else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                invokeSubscribeMethod(subscription, object);
                            }
                        });
                    }

                    break;
                case POSTING:
                    invokeSubscribeMethod(subscription, object);
                    break;
                case ASYNC:
                    if (Looper.getMainLooper() == Looper.myLooper()) {
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                invokeSubscribeMethod(subscription, object);
                            }
                        });
                    } else {
                        invokeSubscribeMethod(subscription, object);
                    }
                    break;
            }

        }

    }

    private void invokeSubscribeMethod(Subscription subscription, Object event) {
        try {
            subscription.subscriberMethod.method.invoke(subscription.subscriber, event);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException().getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
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
