package com.example.demo.disruptor;

public class Event<T> {
    private T data;

    public void set(T t) {
        this.data = t;
    }

    public T get() {
        return this.data;
    }
}
