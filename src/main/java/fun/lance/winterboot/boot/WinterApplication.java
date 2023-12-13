package fun.lance.winterboot.boot;

import fun.lance.winterboot.context.ConfigurableApplicationContext;
import fun.lance.winterboot.core.io.ResourceLoader;
import fun.lance.winterboot.core.io.WinterFactoriesLoader;
import fun.lance.winterboot.util.Assert;
import fun.lance.winterboot.util.ClassUtils;

import java.util.*;

public class WinterApplication {

    static final WinterApplicationShutdownHook shutdownHook = new WinterApplicationShutdownHook();

    private final Set<Class<?>> primarySources;
    private final List<BootstrapRegistryInitializer> bootstrapRegistryInitializers;

    private boolean registerShutdownHook = true;
    private ResourceLoader resourceLoader;
    private WebApplicationType webApplicationType;

    public WinterApplication(Class<?>... primarySources) {
        this(null, primarySources);
    }

    public WinterApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
        this.resourceLoader = resourceLoader;
        Assert.notNull(primarySources, "PrimarySources must not be null");
        this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
        this.webApplicationType = WebApplicationType.deduceFromClasspath();
        this.bootstrapRegistryInitializers = new ArrayList<>(getSpringFactoriesInstance(BootstrapRegistryInitializer.class));
    }

    public static ConfigurableApplicationContext run(Class<?> primarySources, String... args) {
        return run(new Class<?>[]{primarySources}, args);
    }

    public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
        return new WinterApplication(primarySources).run(args);
    }

    public ConfigurableApplicationContext run(String... args) {
        if (this.registerShutdownHook) {
            WinterApplication.shutdownHook.enableShutdownHookAddition();
        }
        long startTime = System.nanoTime();
        DefaultBootstrapContext bootstrapContext = createBootstrapContext();
        return null;
    }

    public ClassLoader getClassLoader() {
        if (this.resourceLoader != null) {
            return this.resourceLoader.getClassLoader();
        }
        return ClassUtils.getDefaultClassLoader();
    }

    private <T> List<T> getSpringFactoriesInstance(Class<T> type) {
        return WinterFactoriesLoader.forDefaultResourceLocation(getClassLoader()).load(type, null);
    }

    private DefaultBootstrapContext createBootstrapContext() {
        DefaultBootstrapContext bootstrapContext = new DefaultBootstrapContext();
        this.bootstrapRegistryInitializers.forEach((initializer) -> initializer.initialize(bootstrapContext));
        return bootstrapContext;
    }

    @FunctionalInterface
    public interface ArgumentResolver {
        <T> T resolve(Class<T> type);
    }
}
