package cn.itcast.base.nio.c1;

import java.nio.ByteBuffer;

public class TestByteBufferAllocate {

    public static void main(String[] args) {

        // class java.nio.HeapByteBuffer    - java 堆内存，读写效率低，受 GC 影响
        System.out.println(ByteBuffer.allocate(16).getClass());
        // class java.nio.DirectByteBuffer  - 直接内存，读写效率高（少一次拷贝），不会受 GC 影响，分配效率低
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
    }
}
