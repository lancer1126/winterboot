package fun.lance.winterboot.context;

import fun.lance.winterboot.beans.factory.AutowireCapableBeanFactory;
import fun.lance.winterboot.beans.factory.ListableBeanFactory;
import fun.lance.winterboot.core.env.EnvironmentCapable;
import fun.lance.winterboot.lang.Nullable;

public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory {

    @Nullable
    String getId();

    String getApplicationName();

    String getDisplayName();

    long getStartupDate();

    @Nullable
    ApplicationContext getParent();

    AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;
}
