package fun.lance.core.io;

import fun.lance.lang.Nullable;

public interface ResourceLoader {

    String CLASSPATH_URL_PREFIX = "classpath:";

    Resource getResource(String location);

    @Nullable
    ClassLoader getClassLoader();
}
