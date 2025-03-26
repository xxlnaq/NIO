package com.example.Reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Handler implements Runnable{
    private static final ExecutorService POOL = Executors.newFixedThreadPool(10);

    private final SocketChannel channel;

    public Handler(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
           if(channel.read(buffer) < 0) {
               System.out.println("客户端已断开连接"+channel.getRemoteAddress());
               channel.close();
               return;//添加return后直接退出了run方法
               /**
                * 不会的，在Java中，return语句会立即从当前方法返回，终止方法的执行。
                * 因此，如果return语句被执行了，那么它后面的代码（包括buffer.flip()和提交到线程池的任务）将不会被执行。
                */
           }
            buffer.flip();
            POOL.submit(() -> {
                try {
                    System.out.println("接收到客户端数据："+new String(buffer.array(), 0, buffer.remaining()));
                    channel.write(ByteBuffer.wrap("已收到！".getBytes()));
                }catch (IOException e){
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}