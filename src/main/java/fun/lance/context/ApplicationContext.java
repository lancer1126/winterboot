package fun.lance.context;

import fun.lance.beans.factory.AutowireCapableBeanFactory;
import fun.lance.beans.factory.ListableBeanFactory;
import fun.lance.core.env.EnvironmentCapable;
import fun.lance.lang.Nullable;

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
