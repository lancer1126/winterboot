package fun.lance.winterboot.core.io;

import fun.lance.winterboot.lang.Nullable;

public interface ResourceLoader {

    String CLASSPATH_URL_PREFIX = "classpath:";

    Resource getResource(String location);

    @Nullable
    ClassLoader getClassLoader();
}
