package com.example.demo;

import com.example.demo.disruptor.DisruptorProducer;
import com.example.demo.disruptor.DisruptorProducerFactory;
import com.example.demo.disruptor.Event;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

        //init disruptor
        DisruptorProducer<People> producer = DisruptorProducerFactory.createSingle(1024);
        producer.handleEventsWith(handler).then(handler2);
        producer.start();

        People people = new People();
        people.setName("test");
        people.setAge("10");
        people.setGrade("4");

        logger.info("publish event");
        producer.publish(people);

        Thread.sleep(3000);
    }
}
