package com.example.demo.disruptor;


import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ThreadFactory;

public class DisruptorProducerFactory {
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    public static <T> DisruptorProducer<T> createSingle(int bufferSize) {
        return create(bufferSize, ProducerType.SINGLE);
    }

    public static <T> DisruptorProducer<T> createSingle(int bufferSize, WaitStrategy waitStrategy) {
        return create(bufferSize, ProducerType.SINGLE, waitStrategy);
    }

    public static <T> DisruptorProducer<T> createMULTI(int bufferSize) {
        return create(bufferSize, ProducerType.MULTI);
    }

    public static <T> DisruptorProducer<T> createMULTI(int bufferSize, WaitStrategy waitStrategy) {
        return create(bufferSize, ProducerType.MULTI, waitStrategy);
    }

    public static <T> DisruptorProducer<T> create(int bufferSize, ProducerType producerType) {
        return create(bufferSize, getDefaultThreadFactory(), producerType, new YieldingWaitStrategy());
    }

    public static <T> DisruptorProducer<T> create(int bufferSize, ProducerType producerType, WaitStrategy waitStrategy) {
        return create(bufferSize, getDefaultThreadFactory(), producerType, waitStrategy);
    }

    public static <T> DisruptorProducer<T> create(int bufferSize, ThreadFactory threadFactory, ProducerType producerType, WaitStrategy waitStrategy) throws IllegalArgumentException {
        if (bufferSize < 16) {
            throw new IllegalArgumentException("bufferSize min size 16， must be the nth power of 2");
        }

        if (null == threadFactory) {
            throw new IllegalArgumentException("threadFactory is null");
        }

        if (null == waitStrategy) {
            throw new IllegalArgumentException("waitStrategy is null");
        }

        //转换成2的n次方，大于等于原来的值,如果超过最大值，就设置成最大值
        bufferSize = tableSizeFor(bufferSize);

        Disruptor<Event<T>> disruptor = new Disruptor<>(
                Event::new,
                bufferSize,
                threadFactory,
                producerType,
                waitStrategy);

        return new DisruptorProducer<>(disruptor);
    }

    private static ThreadFactory getDefaultThreadFactory() {
        return new DisruptorThreadFactory();
    }

    /**
     * 返回比cap大的最小的2的N次方
     **/
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
}
