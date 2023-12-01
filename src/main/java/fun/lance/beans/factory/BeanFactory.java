package fun.lance.beans.factory;

import fun.lance.lang.Nullable;

public interface BeanFactory {

    String FACTORY_BEAN_PREFIX = "&";

    Object getBean(String name) throws Exception;

    <T> T getBean(String name, Class<T> requiredType) throws Exception;

    Object getBean(String name, Object... args) throws Exception;

    <T> T getBean(Class<T> requiredType) throws Exception;

    <T> T getBean(Class<T> requiredType, Object... args) throws Exception;

//    <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

//    <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);

    boolean containsBean(String name);

    boolean isSingleton(String name) throws Exception;

    boolean isPrototype(String name) throws Exception;

//    boolean isTypeMatch(String name, ResolvableType typeToMatch) throws Exception;

    boolean isTypeMatch(String name, Class<?> typeToMatch) throws Exception;

    @Nullable
    Class<?> getType(String name) throws Exception;

    @Nullable
    Class<?> getType(String name, boolean allowFactoryBeanInit) throws Exception;

    String[] getAliases(String name);
}
