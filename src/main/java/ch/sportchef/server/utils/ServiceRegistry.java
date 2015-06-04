package ch.sportchef.server.utils;

import javax.management.ServiceNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * ...Documentation of an whiteboard pattern
 */
public final class ServiceRegistry {

    private static final Map<String, Service> services = new HashMap<>();

    private ServiceRegistry () {
        super();
    }

    public static <T extends Service> void register (final T service) {
        services.put(service.getClass().getName(), service);
    }

    public static <T extends Service> T getService(final Class<T> serviceClass)
            throws ServiceNotFoundException {
        final Service service = services.get(serviceClass.getName());
        if (service == null) {
            throw new ServiceNotFoundException(String.format(
                    "Can't find service for '%s'.",  serviceClass.getName()));
        }
        return serviceClass.cast(service);
    }
}