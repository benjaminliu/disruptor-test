package com.example.demo.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class DisruptorProducer<T> {
    private static Logger logger = LoggerFactory.getLogger(DisruptorProducer.class);

    private static final int SLEEP_MILLIS_BETWEEN_DRAIN_ATTEMPTS = 50;
    private static final int MAX_DRAIN_ATTEMPTS_BEFORE_SHUTDOWN = 200;

    private Disruptor<Event<T>> disruptor;

    public Disruptor<Event<T>> getDisruptor() {
        return disruptor;
    }

    public DisruptorProducer(Disruptor<Event<T>> disruptor) {
        if (null == disruptor)
            throw new IllegalArgumentException("disruptor为空");
        this.disruptor = disruptor;
    }

    /**
     * 多线程线程池用这个，每个线程独占的处理一个消息，就是说一个处理了，其他就不处理了，
     * 更复杂的，就获取disruptor然后自己设。
     **/
    public EventHandlerGroup handleEventsWithWorkerPool(WorkHandler... workHandlers) throws Exception {
        check();
        return this.disruptor.handleEventsWithWorkerPool(workHandlers);
    }

    /**
     * 多个handler都会处理同一个消息，
     * 更复杂的，就获取disruptor然后自己设。
     **/
    public EventHandlerGroup handleEventsWith(EventHandler... handlers) throws Exception {
        check();
        return this.disruptor.handleEventsWith(handlers);
    }

    public void start() throws Exception {
        check();
        this.disruptor.start();
    }

    private static boolean hasBacklog(final Disruptor<?> theDisruptor) {
        final RingBuffer<?> ringBuffer = theDisruptor.getRingBuffer();
        return !ringBuffer.hasAvailableCapacity(ringBuffer.getBufferSize());
    }

    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        final Disruptor<Event<T>> temp = getDisruptor();
        if (temp == null) {
            logger.trace("disruptor already shut down.");
            return true; // disruptor was already shut down by another thread
        }

        // We must guarantee that publishing to the RingBuffer has stopped before we call disruptor.shutdown().
        disruptor = null;

        // Calling Disruptor.shutdown() will wait until all enqueued events are fully processed,
        // but this waiting happens in a busy-spin. To avoid (postpone) wasting CPU,
        // we sleep in short chunks, up to 10 seconds, waiting for the ringbuffer to drain.
        for (int i = 0; hasBacklog(temp) && i < MAX_DRAIN_ATTEMPTS_BEFORE_SHUTDOWN; i++) {
            try {
                Thread.sleep(SLEEP_MILLIS_BETWEEN_DRAIN_ATTEMPTS); // give up the CPU for a while
            } catch (final InterruptedException e) { // ignored
            }
        }
        try {
            // busy-spins until all events currently in the disruptor have been processed, or timeout
            temp.shutdown(timeout, timeUnit);
        } catch (final TimeoutException e) {
            logger.warn("shutdown timed out after {} {}", timeout, timeUnit);
            temp.halt(); // give up on remaining log events, if any
        }

        logger.trace("disruptor has been shut down.");
        return true;
    }

    private void check() throws Exception {
        if (null == this.disruptor)
            throw new Exception("Please init Disruptor first");
    }

    public void publish(T data) throws Exception {
        check();

        long sequence = this.disruptor.getRingBuffer().next();
        try {
            Event<T> event = this.disruptor.getRingBuffer().get(sequence);
            event.set(data);
        } finally {
            this.disruptor.getRingBuffer().publish(sequence);
        }
    }
}