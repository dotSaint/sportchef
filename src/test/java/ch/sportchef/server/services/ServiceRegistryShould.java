package ch.sportchef.server.services;

import org.junit.Assert;
import org.junit.Test;

import javax.management.ServiceNotFoundException;

public class ServiceRegistryShould {

    private static class TestService implements Service
    {
    }

    @Test(expected = NullPointerException.class)
    public void registerShouldFail () {
        ServiceRegistry.register(null);
    }

    @Test(expected = ServiceNotFoundException.class)
    public void getServiceShouldFail () throws Exception {
        ServiceRegistry.getService(TestService.class);
    }

    @Test
    public void registerAndGetService () throws Exception {
        // create a new TestService and register
        TestService serviceToRegister = new TestService();
        ServiceRegistry.register(serviceToRegister);

        // get TestService from registry and check
        TestService registeredService = ServiceRegistry.getService(TestService.class);
        Assert.assertSame(serviceToRegister, registeredService);
    }
}