package fun.lance.winterboot.util;

import fun.lance.winterboot.lang.Nullable;

import java.io.Closeable;
import java.io.Externalizable;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.regex.Pattern;

public class ClassUtils {

    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(9);
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(9);
    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<>(32);
    private static final Map<String, Class<?>> commonClassCache = new HashMap<>(64);
    private static final Set<Class<?>> javaLanguageInterfaces;
    private static final Map<Method, Method> interfaceMethodCache = new ConcurrentReferenceHashMap<>(256);

    static {
        primitiveWrapperTypeMap.put(Boolean.class, Boolean.TYPE);
        primitiveWrapperTypeMap.put(Byte.class, Byte.TYPE);
        primitiveWrapperTypeMap.put(Character.class, Character.TYPE);
        primitiveWrapperTypeMap.put(Double.class, Double.TYPE);
        primitiveWrapperTypeMap.put(Float.class, Float.TYPE);
        primitiveWrapperTypeMap.put(Integer.class, Integer.TYPE);
        primitiveWrapperTypeMap.put(Long.class, Long.TYPE);
        primitiveWrapperTypeMap.put(Short.class, Short.TYPE);
        primitiveWrapperTypeMap.put(Void.class, Void.TYPE);
        Iterator var0 = primitiveWrapperTypeMap.entrySet().iterator();

        while(var0.hasNext()) {
            Map.Entry<Class<?>, Class<?>> entry = (Map.Entry)var0.next();
            primitiveTypeToWrapperMap.put((Class)entry.getValue(), (Class)entry.getKey());
            registerCommonClasses((Class)entry.getKey());
        }

        Set<Class<?>> primitiveTypes = new HashSet(32);
        primitiveTypes.addAll(primitiveWrapperTypeMap.values());
        Collections.addAll(primitiveTypes, boolean[].class, byte[].class, char[].class, double[].class, float[].class, int[].class, long[].class, short[].class);
        Iterator var4 = primitiveTypes.iterator();

        while(var4.hasNext()) {
            Class<?> primitiveType = (Class)var4.next();
            primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
        }

        registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class, Float[].class, Integer[].class, Long[].class, Short[].class);
        registerCommonClasses(Number.class, Number[].class, String.class, String[].class, Class.class, Class[].class, Object.class, Object[].class);
        registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class, Error.class, StackTraceElement.class, StackTraceElement[].class);
        registerCommonClasses(Enum.class, Iterable.class, Iterator.class, Enumeration.class, Collection.class, List.class, Set.class, Map.class, Map.Entry.class, Optional.class);
        Class<?>[] javaLanguageInterfaceArray = new Class[]{Serializable.class, Externalizable.class, Closeable.class, AutoCloseable.class, Cloneable.class, Comparable.class};
        registerCommonClasses(javaLanguageInterfaceArray);
        javaLanguageInterfaces = Set.of(javaLanguageInterfaceArray);
    }

    private static void registerCommonClasses(Class<?>... commonClasses) {
        Class[] var1 = commonClasses;
        int var2 = commonClasses.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Class<?> clazz = var1[var3];
            commonClassCache.put(clazz.getName(), clazz);
        }

    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;

        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable var3) {
        }

        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable var2) {
                }
            }
        }

        return cl;
    }

    public static boolean isPresent(String className, @Nullable ClassLoader classLoader) {
        try {
            forName(className, classLoader);
            return true;
        } catch (IllegalAccessError var3) {
            throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" + className + "]: " + var3.getMessage(), var3);
        } catch (Throwable var4) {
            return false;
        }
    }

    public static Class<?> forName(String name, @Nullable ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
        Assert.notNull(name, "Name must not be null");
        Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz == null) {
            clazz = (Class)commonClassCache.get(name);
        }

        if (clazz != null) {
            return clazz;
        } else {
            Class elementClass;
            String elementName;
            if (name.endsWith("[]")) {
                elementName = name.substring(0, name.length() - "[]".length());
                elementClass = forName(elementName, classLoader);
                return elementClass.arrayType();
            } else if (name.startsWith("[L") && name.endsWith(";")) {
                elementName = name.substring("[L".length(), name.length() - 1);
                elementClass = forName(elementName, classLoader);
                return elementClass.arrayType();
            } else if (name.startsWith("[")) {
                elementName = name.substring("[".length());
                elementClass = forName(elementName, classLoader);
                return elementClass.arrayType();
            } else {
                ClassLoader clToUse = classLoader;
                if (classLoader == null) {
                    clToUse = getDefaultClassLoader();
                }

                try {
                    return Class.forName(name, false, clToUse);
                } catch (ClassNotFoundException var10) {
                    int lastDotIndex = name.lastIndexOf(46);
                    int previousDotIndex = name.lastIndexOf(46, lastDotIndex - 1);
                    if (lastDotIndex != -1 && previousDotIndex != -1 && Character.isUpperCase(name.charAt(previousDotIndex + 1))) {
                        String var10000 = name.substring(0, lastDotIndex);
                        String nestedClassName = var10000 + "$" + name.substring(lastDotIndex + 1);

                        try {
                            return Class.forName(nestedClassName, false, clToUse);
                        } catch (ClassNotFoundException var9) {
                        }
                    }

                    throw var10;
                }
            }
        }
    }

    public static Class<?> resolvePrimitiveClassName(@Nullable String name) {
        Class<?> result = null;
        if (name != null && name.length() <= 7) {
            result = (Class)primitiveTypeNameMap.get(name);
        }

        return result;
    }

    public static Class<?> resolveClassName(String className, @Nullable ClassLoader classLoader) throws IllegalArgumentException {
        try {
            return forName(className, classLoader);
        } catch (IllegalAccessError var3) {
            throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" + className + "]: " + var3.getMessage(), var3);
        } catch (LinkageError var4) {
            throw new IllegalArgumentException("Unresolvable class definition for class [" + className + "]", var4);
        } catch (ClassNotFoundException var5) {
            throw new IllegalArgumentException("Could not find class [" + className + "]", var5);
        }
    }

    public static boolean isSimpleValueType(Class<?> type) {
        return Void.class != type && Void.TYPE != type && (isPrimitiveOrWrapper(type) || Enum.class.isAssignableFrom(type) || CharSequence.class.isAssignableFrom(type) || Number.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type) || Temporal.class.isAssignableFrom(type) || ZoneId.class.isAssignableFrom(type) || TimeZone.class.isAssignableFrom(type) || File.class.isAssignableFrom(type) || Path.class.isAssignableFrom(type) || Charset.class.isAssignableFrom(type) || Currency.class.isAssignableFrom(type) || InetAddress.class.isAssignableFrom(type) || URI.class == type || URL.class == type || UUID.class == type || Locale.class == type || Pattern.class == type || Class.class == type);
    }

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return clazz.isPrimitive() || isPrimitiveWrapper(clazz);
    }

    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        return primitiveWrapperTypeMap.containsKey(clazz);
    }
}
