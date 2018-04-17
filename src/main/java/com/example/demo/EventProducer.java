package com.example.demo;

import com.lmax.disruptor.RingBuffer;

public class EventProducer<T extends Event<K>, K> {

    private RingBuffer<T> ringBuffer;

    public EventProducer(RingBuffer<T> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onDate(K data) {
        long sequence = ringBuffer.next();
        try {
            T event = ringBuffer.get(sequence);
            event.set(data);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}
