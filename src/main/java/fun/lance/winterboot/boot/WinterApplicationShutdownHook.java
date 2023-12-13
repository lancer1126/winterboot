package fun.lance.winterboot.boot;

/**
 * 一个Runnable，用于关闭hook，来让Springboot程序优雅的关闭。
 * 这个hook通过SpringApplication.getShutdownHandlers()追踪注册的程序上下文
 */
public class WinterApplicationShutdownHook implements Runnable {

    private volatile boolean shutdownHookAdditionEnabled = false;

    void enableShutdownHookAddition() {
        this.shutdownHookAdditionEnabled = true;
    }

    @Override
    public void run() {

    }
}
