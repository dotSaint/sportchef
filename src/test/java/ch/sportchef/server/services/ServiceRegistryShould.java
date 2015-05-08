package ch.sportchef.server.services;

import ch.sportchef.server.utils.UtilityClassVerifier;
import org.junit.Assert;
import org.junit.Test;

import javax.management.ServiceNotFoundException;
import java.lang.reflect.InvocationTargetException;

public class ServiceRegistryShould {

    private static class TestService implements Service
    {
    }

    @Test
    public void beWellDefined() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        UtilityClassVerifier.assertUtilityClassWellDefined(ServiceRegistry.class);
    }

    @Test(expected = NullPointerException.class)
    public void registerShouldFail() {
        ServiceRegistry.register(null);
    }

    @Test(expected = ServiceNotFoundException.class)
    public void getServiceShouldFail() throws Exception {
        ServiceRegistry.getService(TestService.class);
    }

    @Test
    public void registerAndGetService() throws Exception {
        // create a new TestService and register
        final TestService serviceToRegister = new TestService();
        ServiceRegistry.register(serviceToRegister);

        // get TestService from registry and check
        final TestService registeredService = ServiceRegistry.getService(TestService.class);
        Assert.assertSame(serviceToRegister, registeredService);
    }
}