package fun.lance.winterboot.core.io;

import fun.lance.winterboot.boot.WinterApplication;
import fun.lance.winterboot.core.io.support.PropertiesLoaderUtils;
import fun.lance.winterboot.util.Assert;
import fun.lance.winterboot.util.ConcurrentReferenceHashMap;
import fun.lance.winterboot.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * 框架内部通用的工厂加载机制
 * SpringFactoriesLoader 从类路径中可能存在于多个 JAR 文件的 "META-INF/spring.factories" 文件中加载并实例化给定类型的工厂。
 * spring.factories 文件必须采用properties格式，其中key是接口或抽象类的完全限定名称，而value是逗号分隔的实现类名列表。例如：
 * example.MyService=example.MyServiceImpl1,example.MyServiceImpl2
 * 其中，example.MyService 是接口的名称，而 MyServiceImpl1 和 MyServiceImpl2 是两个实现类。
 * 实现类必须具有一个可解析的构造函数，该构造函数将用于创建实例，可以是：
 * 主构造函数或单一构造函数
 * 单一的公共构造函数
 * 默认构造函数
 * 如果可解析的构造函数有参数，则应提供适当的 ArgumentResolver。为了自定义如何处理实例化失败，请考虑提供一个 FailureHandler。
 */
public class WinterFactoriesLoader {

    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";

    static final Map<ClassLoader, Map<String, WinterFactoriesLoader>> cache = new ConcurrentReferenceHashMap<>();

    private final ClassLoader classLoader;
    private final Map<String, List<String>> factories;

    protected WinterFactoriesLoader(ClassLoader classLoader, Map<String, List<String>> factories) {
        this.classLoader = classLoader;
        this.factories = factories;
    }

    /**
     * 创建一个SpringFactoriesLoader实例，该实例将使用给定的类加载器从“META-INF/spring.factories”加载并实例化工厂实现。
     */
    public static WinterFactoriesLoader forDefaultResourceLocation(ClassLoader classLoader) {
        return forResourceLocation(FACTORIES_RESOURCE_LOCATION, classLoader);
    }

    public <T> List<T> load(Class<T> factoryType, WinterApplication.ArgumentResolver argumentResolver) {
        // todo factoryLoader加载实例
        return null;
    }

    public static WinterFactoriesLoader forResourceLocation(String resourceLocation, ClassLoader classLoader) {
        Assert.hasText(resourceLocation, "'resourceLocation' must not be empty");
        ClassLoader resourceClassLoader = (classLoader != null ? classLoader: WinterFactoriesLoader.class.getClassLoader());
        Map<String, WinterFactoriesLoader> loaders = cache.computeIfAbsent(resourceClassLoader, _ -> new ConcurrentReferenceHashMap<>());
        return loaders.computeIfAbsent(resourceLocation, _ -> new WinterFactoriesLoader(classLoader, loadFactoriesResource(resourceClassLoader, resourceLocation)));
    }

    protected static Map<String, List<String>> loadFactoriesResource(ClassLoader classLoader, String resourceLocation) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        try {
            Enumeration<URL> urls = classLoader.getResources(resourceLocation);
            while (urls.hasMoreElements()) {
                UrlResource resource = new UrlResource(urls.nextElement());
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                properties.forEach((name, value) -> {
                    String[] factoryImplNames = StringUtils.commaDelimitedListToStringArray((String) value);
                    List<String> implementations = result.computeIfAbsent(((String) name).trim(), _ -> new ArrayList<>(factoryImplNames.length));
                    Arrays.stream(factoryImplNames).map(String::trim).forEach(implementations::add);
                });
            }
            result.replaceAll(WinterFactoriesLoader::toDistinctUnmodifiableList);
        } catch (IOException ex) {
            throw new IllegalArgumentException(STR."Unable to load factories from location [\{resourceLocation}]", ex);
        }
        return Collections.unmodifiableMap(result);
    }

    private static List<String> toDistinctUnmodifiableList(String factoryType, List<String> implementations) {
        return implementations.stream().distinct().toList();
    }
}
