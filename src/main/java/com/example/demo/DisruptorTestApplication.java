package com.example.demo;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.SplittableRandom;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@SpringBootApplication
public class DisruptorTestApplication implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(DisruptorTestApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DisruptorTestApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PeopleEventHandler handler = new PeopleEventHandler();
        PeopleEventHandler2 handler2 = new PeopleEventHandler2();

        Disruptor<Event<People>> disruptor = DisruptorFacotry.createSingle(1024);

        disruptor.handleEventsWith(handler).then(handler2);

        RingBuffer<Event<People>> ringBuffer = disruptor.start();

        EventProducer<Event<People>, People> producer = new EventProducer(ringBuffer);

        People people = new People();
        people.setName("test");
        people.setAge("10");
        people.setGrade("4");
        producer.onDate(people);
    }
}
