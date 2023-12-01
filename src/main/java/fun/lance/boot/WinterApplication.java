package fun.lance.boot;

import fun.lance.context.ConfigurableApplicationContext;
import fun.lance.core.io.ResourceLoader;
import fun.lance.util.Assert;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class WinterApplication {

    private ResourceLoader resourceLoader;
    private WebApplicationType webApplicationType;
    private final Set<Class<?>> primarySources;

    public WinterApplication(Class<?>... primarySources) {
        this(null, primarySources);
    }

    public WinterApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
        this.resourceLoader = resourceLoader;
        Assert.notNull(primarySources, "PrimarySources must not be null");
        this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
        this.webApplicationType = WebApplicationType.deduceFromClasspath();
    }

    public static ConfigurableApplicationContext run(Class<?> primarySources, String... args) {
        return run(new Class<?>[]{primarySources}, args);
    }

    public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
        return new WinterApplication(primarySources).run(args);
    }

    public ConfigurableApplicationContext run(String... args) {
        return null;
    }
}
