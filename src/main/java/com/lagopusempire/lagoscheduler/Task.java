package com.lagopusempire.lagoscheduler;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

abstract class Task
{
    private enum Types { INT, DOUBLE, STRING, BOOLEAN, VOID };
    
    private final AtomicBoolean done = new AtomicBoolean(false);
    
    private final AtomicInteger intBuffer = new AtomicInteger(0);
    private final AtomicLong doubleBuffer = new AtomicLong(0);
    private final AtomicReference<String> stringBuffer = new AtomicReference<>();
    private final AtomicBoolean booleanBuffer = new AtomicBoolean(false);
    
    private final CopyOnWriteArraySet<Types> typeUpdated = new CopyOnWriteArraySet<>();
    
    private final Runnable doneCallback;
    private final TaskBehaviorHandler handler;
    
    Task(Runnable doneCallback, TaskBehaviorHandler handler)
    {
        this.doneCallback = doneCallback;
        this.handler = handler;
    }
    
    public void stop()
    {
        setDone();
    }
    
    public void tick()
    {
    }
    
    protected void notifyHandlerMethods()
    {
        final Iterator<Types> it = typeUpdated.iterator();
        while(it.hasNext())
        {
            Types type = it.next();
            switch(type)
            {
                case INT:
                    handler.onReceive(intBuffer.get());
                    break;
                case DOUBLE:
                    double d = Double.longBitsToDouble(doubleBuffer.get());
                    handler.onReceive(d);
                    break;
                case STRING:
                    handler.onReceive(stringBuffer.get());
                    break;
                case BOOLEAN:
                    handler.onReceive(booleanBuffer.get());
                    break;
                case VOID:
                    handler.onReceive();
                    break;
            }
        }
        typeUpdated.clear();
    }
    
    protected void started()
    {
        handler.onStart();
    }
    
    protected void finished()
    {
        handler.onStop();
        doneCallback.run();
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
    
    public final void setDone()
    {
        done.set(true);
    }
    
    public final boolean isDone()
    {
        return done.get();
    }
}
