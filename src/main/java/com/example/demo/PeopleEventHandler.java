package com.example.demo;

import com.example.demo.disruptor.Event;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeopleEventHandler implements EventHandler<Event<People>> {
    private Logger logger = LoggerFactory.getLogger(PeopleEventHandler.class);

    @Override
    public void onEvent(Event<People> peopleEvent, long l, boolean b) throws Exception {
        People people = peopleEvent.get();

        String total = String.format("%s - %s - %s", people.getName(), people.getAge(), people.getGrade());
        logger.info("handler 1 - {}", total);
        people.setTotal(total);
    }
}
