package fun.lance.winterboot.boot;

/**
 * 回调接口，可以用于在BootstrapRegistry使用前初始化它
 */
@FunctionalInterface
public interface BootstrapRegistryInitializer {

    /**
     * 将给定的BootstrapRegistry进行初始化
     */
    void initialize(BootstrapRegistry registry);

}
