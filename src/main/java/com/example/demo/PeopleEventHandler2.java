package com.example.demo;

import com.example.demo.disruptor.Event;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PeopleEventHandler2 implements EventHandler<Event<People>> {

    private Logger logger = LoggerFactory.getLogger(PeopleEventHandler2.class);

    @Override
    public void onEvent(Event<People> peopleEvent, long l, boolean b) throws Exception {
        logger.info("handler 2 - {}", peopleEvent.get().getTotal());
    }
}
