package fun.lance.boot;

import fun.lance.util.ClassUtils;

public enum WebApplicationType {
    NONE,

    SERVLET,

    REACTIVE;

    private static final String[] SERVLET_INDICATOR_CLASSES = {
            "jakarta.servlet.Servlet",
            "org.springframework.web.context.ConfigurableWebApplicationContext"
    };

    private static final String WEBMVC_INDICATOR_CLASS = "org.springframework.web.servlet.DispatcherServlet";

    private static final String WEBFLUX_INDICATOR_CLASS = "org.springframework.web.reactive.DispatcherHandler";

    private static final String JERSEY_INDICATOR_CLASS = "org.glassfish.jersey.servlet.ServletContainer";

    static WebApplicationType deduceFromClasspath() {
        if (ClassUtils.isPresent(WEBFLUX_INDICATOR_CLASS, null)
                && !ClassUtils.isPresent(WEBMVC_INDICATOR_CLASS, null)
                && !ClassUtils.isPresent(JERSEY_INDICATOR_CLASS, null)) {
            return WebApplicationType.REACTIVE;
        }
        for (String className : SERVLET_INDICATOR_CLASSES) {
            if (!ClassUtils.isPresent(className, null)) {
                return WebApplicationType.NONE;
            }
        }
        return WebApplicationType.SERVLET;
    }

//    static class WebApplicationTypeRuntimeHints implements RuntimeHintsRegistrar {
//
//        @Override
//        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
//            for (String servletIndicatorClass : SERVLET_INDICATOR_CLASSES) {
//                registerTypeIfPresent(servletIndicatorClass, classLoader, hints);
//            }
//            registerTypeIfPresent(JERSEY_INDICATOR_CLASS, classLoader, hints);
//            registerTypeIfPresent(WEBFLUX_INDICATOR_CLASS, classLoader, hints);
//            registerTypeIfPresent(WEBMVC_INDICATOR_CLASS, classLoader, hints);
//        }
//
//        private void registerTypeIfPresent(String typeName, ClassLoader classLoader, RuntimeHints hints) {
//            if (ClassUtils.isPresent(typeName, classLoader)) {
//                hints.reflection().registerType(TypeReference.of(typeName));
//            }
//        }
//
//    }
}
