package com.example;

import java.nio.IntBuffer;

public class equals {
    public static void main(String[] args) {
            IntBuffer buffer1 = IntBuffer.wrap(new int[]{1, 2, 3, 4, 5, 6, 7,8, 9});
            IntBuffer buffer2 = IntBuffer.wrap(new int[]{ 1,5, 4, 3, 2, 6, 7, 8, 9, 4});
            System.out.println(buffer1.equals(buffer2));   //直接比较

            buffer1.position(6);
            buffer2.position(6);
            System.out.println(buffer1.compareTo(buffer2));   //比较从下标6开始的剩余内容
    }
//    如果 buffer1 的某个元素小于 buffer2 的对应元素，compareTo 方法返回负数（例如 -1）。
//    如果 buffer1 的某个元素大于 buffer2 的对应元素，compareTo 方法返回正数（例如 1）。
//    如果所有比较的元素都相等，但 buffer1 的剩余长度小于 buffer2，compareTo 方法返回负数。
//    如果所有比较的元素都相等，但 buffer1 的剩余长度大于 buffer2，compareTo 方法返回正数。
}
