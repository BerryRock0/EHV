package EtherHack.utils;

import EtherHack.annotations.SubscribeLuaEvent;
import EtherHack.utils.Logger;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSubscriber {
    private static Map<String, List<AbstractMap.SimpleEntry<Object, Method>>> subscribers = new HashMap<String, List<AbstractMap.SimpleEntry<Object, Method>>>();

    public static void register(Object object) {
        Logger.printLog("Registering a class object and subscribing to Lua events: " + object);
        for (Method method : object.getClass().getMethods()) {
            SubscribeLuaEvent[] annotations;
            for (SubscribeLuaEvent annotation : annotations = (SubscribeLuaEvent[])method.getAnnotationsByType(SubscribeLuaEvent.class)) {
                String eventName = annotation.eventName();
                List eventSubscribers = subscribers.computeIfAbsent(eventName, k -> new ArrayList());
                eventSubscribers.add(new AbstractMap.SimpleEntry<Object, Method>(object, method));
            }
        }
    }

    public static void invokeSubscriber(String eventName) {
        List<AbstractMap.SimpleEntry<Object, Method>> eventSubscribers = subscribers.get(eventName);
        if (eventSubscribers == null) {
            return;
        }
        for (AbstractMap.SimpleEntry<Object, Method> subscriber : eventSubscribers) {
            try {
                Method method = subscriber.getValue();
                method.invoke(subscriber.getKey(), (Object[])null);
            }
            catch (Exception e) {
                Logger.printLog(String.format("Exception when calling a method '%s' for an event '%s': %s", subscriber.getValue(), eventName, e));
            }
        }
    }
}
