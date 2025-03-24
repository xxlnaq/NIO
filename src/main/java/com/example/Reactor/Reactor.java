package com.example.Reactor;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Reactor implements Closeable, Runnable{

    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    public Reactor() throws IOException{
        serverChannel = ServerSocketChannel.open();
        selector = Selector.open();
    }

    @Override
    public void run() {
        try {
            serverChannel.bind(new InetSocketAddress(8080));
            serverChannel.configureBlocking(false);
            //注册时，将Acceptor作为附加对象存放，当选择器选择后也可以获取到
            serverChannel.register(selector, SelectionKey.OP_ACCEPT, new Acceptor(serverChannel));

            /**
             * 在获取到new Acceptor(serverChannel, selector)并且run时候，会自动注册
             *  channel.register(selector, SelectionKey.OP_READ, new Handler(channel));
             *  并产生了OP_READ事物和对应对象x`
             */
            while (true) {
                int count = selector.select();
                System.out.println("监听到 "+count+" 个事件");
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    this.dispatch(iterator.next());   //通过dispatch方法进行分发
                    //初始状态下，Iterator 的游标指向第一个元素之前，还没有访问任何元素。
                    iterator.remove();
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    //通过此方法进行分发
    private void dispatch(SelectionKey key){
        Object att = key.attachment();   //ey.attachmen会自动获取搜索到SelectionKey对应事物的new对象，如果搜索到OP_ACCEPT
       // 就选择new Acceptor(serverChannel, selector)，如果搜索到OP_READ, 就是new Handler(channel)
        if(att instanceof Runnable) {
            ((Runnable) att).run();
        }   //这里是run的对应事物的对象
    }

    //用了记得关，保持好习惯，就像看完视频要三连一样
    @Override
    public void close() throws IOException {
        serverChannel.close();
        selector.close();
    }
}