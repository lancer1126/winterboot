package fun.lance.winterboot.context;

public interface LifeCycle {

    void start();

    void stop();

    boolean isRunning();
}
