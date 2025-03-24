package com.example.Reactor;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//SubReactor作为从Reactor
    public class SubReactor implements Runnable, Closeable {
    //每个从Reactor也有一个Selector
    private final Selector selector;

    //创建一个4线程的线程池，也就是四个从Reactor工作
    private static final ExecutorService POOL = Executors.newFixedThreadPool(4);
    private static final SubReactor[] reactors = new SubReactor[4];
    private static int selectedIndex = 0;  //采用轮询机制，每接受一个新的连接，就轮询分配给四个从Reactor

    /**
     * 在你的代码中，当程序第一次使用 SubReactor 类（例如调用 SubReactor.nextSelector()
     * 或创建 SubReactor 实例）时，静态代码块会执行一次。
     * 之后，无论你如何调用 SubReactor 的静态方法，静态代码块都不会再次执行。
     */
    static {   //在一开始的时候就让4个从Reactor跑起来
        for (int i = 0; i < 4; i++) {
            try {
                reactors[i] = new SubReactor();
                POOL.submit(reactors[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //轮询获取下一个Selector（Acceptor用）
    public static Selector nextSelector(){
        Selector selector = reactors[selectedIndex].selector;
        selectedIndex = (selectedIndex + 1) % 4;
        return selector;
    }

    private SubReactor() throws IOException {
        selector = Selector.open();
    }
/**
 *在 static 块中，SubReactor 的四个实例被创建，并且每个实例都被提交到了 POOL 线程池中
 * POOL.submit(reactor) 会将 Runnable 对象（即当前 SubReactor 实例）提交给线程池管理。线程池会为每个任务分配一个线程来执行其 run() 方法。
 * 因此，当程序运行时，SubReactor 的 run() 方法会在独立的线程中自动执行，而不需要显式调用。
 * 总结：通过 ExecutorService.submit() 提交任务时，线程池会自动调度并执行 Runnable 的 run() 方法。这就是为什么你没有显式调用 run() 方法，但它的逻辑依然会被执行的原因。
*POOL.submit(reactor) 将 reactor 的 run() 方法作为任务提交到线程池中。
 * 线程池会根据需要调度任务，并在线程上执行 run() 方法。
 * 不需要显式调用 run()，因为线程池会自动管理任务的执行。
 * 每个 SubReactor 的 run() 方法会在独立的线程中并发执行，从而实现多路复用的功能。
 */
    /**
     * SubReactor 的 run 方法之所以能够自动运行，是因为：
     * 它实现了 Runnable 接口。
     * 它被提交到了一个线程池中。
     * 线程池会自动为每个任务分配线程并调用其 run 方法。
     *
     */
    @Override
    public void run() {
        try {   //启动后直接等待selector监听到对应的事件即可，其他的操作逻辑和Reactor一致
            while (true) {
                int count = selector.select();
                System.out.println(Thread.currentThread().getName()+" >> 监听到 "+count+" 个事件");
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    this.dispatch(iterator.next());
                    iterator.remove();
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void dispatch(SelectionKey key){
        Object att = key.attachment();
        if(att instanceof Runnable) {
            ((Runnable) att).run();
        }
    }

    @Override
    public void close() throws IOException {
        selector.close();
    }
}
