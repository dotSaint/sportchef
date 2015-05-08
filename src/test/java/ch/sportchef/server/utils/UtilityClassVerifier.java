package ch.sportchef.server.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public final class UtilityClassVerifier {

    private UtilityClassVerifier() {
        super();
    }

    /**
     * Verifies that a utility class is well defined.
     *
     * @param clazz
     *            utility class to verify.
     */
    public static void assertUtilityClassWellDefined(final Class<?> clazz)
            throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {

        checkConstructorIsFinal(clazz);
        checkConstructorCount(clazz);
        checkConstructorAccessibility(clazz);
        checkForNonStaticMethods(clazz);
    }

    private static void checkConstructorIsFinal(Class<?> clazz) {
        assertTrue("class must be final", Modifier.isFinal(clazz.getModifiers()));
    }

    private static void checkConstructorCount(Class<?> clazz) {
        assertEquals("There must be only one constructor", 1, clazz.getDeclaredConstructors().length);
    }

    private static void checkConstructorAccessibility(Class<?> clazz) throws NoSuchMethodException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        final Constructor<?> constructor = clazz.getDeclaredConstructor();
        if (constructor.isAccessible() ||  !Modifier.isPrivate(constructor.getModifiers())) {
            fail("constructor is not private");
        }

        // reduce noise in coverage report
        constructor.setAccessible(true);
        constructor.newInstance();
        constructor.setAccessible(false);
    }

    private static void checkForNonStaticMethods(Class<?> clazz) {
        for (final Method method : clazz.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass().equals(clazz)) {
                fail("there exists a non-static method:" + method);
            }
        }
    }
}
