package com.example;

import java.nio.IntBuffer;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        //创建一个缓冲区不能直接new，而是需要使用静态方法去生成，有两种方式：
        //1. 申请一个容量为10的int缓冲区
        IntBuffer buffer1 = IntBuffer.allocate(10);
        //2. 可以将现有的数组直接转换为缓冲区（包括数组中的数据）
        int[] arr = new int[]{1, 2, 3, 4, 5, 6};
        buffer1.put(arr,1,5);
        buffer1.flip();//写操作之后经过反转才能进行读操作，同时一个缓冲区put完，要将其反转后，再把次缓冲区放到另一个缓冲区
//        IntBuffer buffer1 = IntBuffer.wrap(arr);
        int[] array = buffer1.array();
        array[0]=99999;
        System.out.println(buffer1.get());
    }
    }
