package com.example.demo;

import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


public class DisruptorFacotry {

    public static <T> Disruptor<Event<T>> createSingle(int bufferSize) {
        return create(bufferSize, ProducerType.SINGLE);
    }

    public static <T> Disruptor<Event<T>> createSingle(int bufferSize, WaitStrategy waitStrategy) {
        return create(bufferSize, ProducerType.SINGLE, waitStrategy);
    }

    public static <T> Disruptor<Event<T>> createMULTI(int bufferSize) {
        return create(bufferSize, ProducerType.MULTI);
    }

    public static <T> Disruptor<Event<T>> createMULTI(int bufferSize, WaitStrategy waitStrategy) {
        return create(bufferSize, ProducerType.MULTI, waitStrategy);
    }

    public static <T> Disruptor<Event<T>> create(int bufferSize, ProducerType producerType) {
        return create(bufferSize, Executors.defaultThreadFactory(), producerType, new YieldingWaitStrategy());
    }

    public static <T> Disruptor<Event<T>> create(int bufferSize, ProducerType producerType, WaitStrategy waitStrategy) {
        return create(bufferSize, Executors.defaultThreadFactory(), producerType, waitStrategy);
    }

    public static <T> Disruptor<Event<T>> create(int bufferSize, ThreadFactory threadFactory, ProducerType producerType, WaitStrategy waitStrategy) throws IllegalArgumentException {
        if (bufferSize < 16) {
            throw new IllegalArgumentException("bufferSize最小16， 且为2的N次方");
        }

        if (null == threadFactory) {
            throw new IllegalArgumentException("threadFactory为空");
        }

        if (null == waitStrategy) {
            throw new IllegalArgumentException("waitStrategy为空");
        }

        Disruptor<Event<T>> disruptor = new Disruptor<>(
                Event::new,
                bufferSize,
                threadFactory,
                producerType,
                waitStrategy);
        return disruptor;
    }
}
