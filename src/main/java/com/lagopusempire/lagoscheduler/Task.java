package com.lagopusempire.lagoscheduler;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

abstract class Task implements Runnable
{
    protected final AtomicInteger intBuffer = new AtomicInteger(0);
    protected final AtomicLong doubleBuffer = new AtomicLong(0);
    protected final AtomicReference<String> stringBuffer = new AtomicReference<>();
    protected final AtomicBoolean booleanBuffer = new AtomicBoolean(false);
    
    protected final CopyOnWriteArraySet<Types> typeUpdated = new CopyOnWriteArraySet<>();
    
    protected final Runnable doneCallback;
    protected final TaskBehaviorHandler handler;
    
    Task(Runnable doneCallback, TaskBehaviorHandler handler)
    {
        this.doneCallback = doneCallback;
        this.handler = handler;
    }
    
    public abstract void stop();
    
    protected void resetTypeUpdated()
    {
        typeUpdated.clear();
    }
    
    void send(int i)
    {
        intBuffer.set(i);
        typeUpdated.add(Types.INT);
    }
    
    void send(double d)
    {
        long bits = Double.doubleToLongBits(d);
        doubleBuffer.set(bits);
        typeUpdated.add(Types.DOUBLE);
    }
    
    void send(String s)
    {
        stringBuffer.set(s);
        typeUpdated.add(Types.STRING);
    }
    
    void send(Boolean b)
    {
        booleanBuffer.set(b);
        typeUpdated.add(Types.BOOLEAN);
    }
    
    void send()
    {
        typeUpdated.add(Types.VOID);
    }
}
