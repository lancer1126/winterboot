package fun.lance.beans.factory;

public interface AutowireCapableBeanFactory extends BeanFactory {
    int AUTOWIRE_NO = 0;
    int AUTOWIRE_BY_NAME = 1;
    int AUTOWIRE_BY_TYPE = 2;
    int AUTOWIRE_CONSTRUCTOR = 3;

    @Deprecated
    int AUTOWIRE_AUTODETECT = 4;

    String ORIGINAL_INSTANCE_SUFFIX = ".ORIGINAL";

    <T> T createBean(Class<T> beanClass) throws Exception;

    void autowireBean(Object existingBean) throws Exception;

    Object configureBean(Object existingBean, String beanName) throws Exception;

    @Deprecated(
            since = "6.1"
    )
    Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws Exception;

    Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws Exception;

    void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws Exception;

    void applyBeanPropertyValues(Object existingBean, String beanName) throws Exception;

    Object initializeBean(Object existingBean, String beanName) throws Exception;

    /** @deprecated */
    @Deprecated(
            since = "6.1"
    )
    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws Exception;

    /** @deprecated */
    @Deprecated(
            since = "6.1"
    )
    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws Exception;

    void destroyBean(Object existingBean);

//    <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws Exception;

//    Object resolveBeanByName(String name, DependencyDescriptor descriptor) throws Exception;

//    @Nullable
//    Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws Exception;

//    @Nullable
//    Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws Exception;
}
