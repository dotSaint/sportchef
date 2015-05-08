package ch.sportchef.server.utils;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public class UtilityClassVerifierShould {
    @Test
    public void beWellDefined() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        UtilityClassVerifier.assertUtilityClassWellDefined(UtilityClassVerifier.class);
    }
}
