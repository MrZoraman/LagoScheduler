package com.lagopusempire.lagoscheduler;

import java.util.concurrent.atomic.*;

final class WaitingAsyncTask implements Runnable
{
    private enum Types {INT, DOUBLE, STRING, BOOLEAN, VOID};
    
    private final AtomicBoolean done = new AtomicBoolean(false);
    
    private final AtomicInteger intBuffer = new AtomicInteger(0);
    private final AtomicLong doubleBuffer = new AtomicLong(0);
    private final AtomicReference<String> stringBuffer = new AtomicReference<>();
    private final AtomicBoolean booleanBuffer = new AtomicBoolean(false);
    
    private final AtomicReference<Types> typeUpdated = new AtomicReference<>();
    
    private final Object lock = new Object();
    
    private final Runnable doneCallback;
    private final TaskBehaviorHandler handler;
    
    WaitingAsyncTask(Runnable doneCallback, TaskBehaviorHandler handler)
    {
        this.doneCallback = doneCallback;
        this.handler = handler;
    }
    
    @Override
    public void run()
    {
        handler.onStart();
        while(true)
        {
            synchronized(lock)
            {
                try
                {
                    lock.wait();
                }
                catch (InterruptedException ignored)
                {
                    done.set(true);
                }
            }
            if(done.get())
            {
                handler.onStop();
                break;
            }
            
            final Types type = typeUpdated.get();
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
        doneCallback.run();
    }
    
    void stop()
    {
        done.set(true);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    void send(int i)
    {
        intBuffer.set(i);
        typeUpdated.set(Types.INT);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    void send(double d)
    {
        long bits = Double.doubleToLongBits(d);
        doubleBuffer.set(bits);
        typeUpdated.set(Types.DOUBLE);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    void send(String s)
    {
        stringBuffer.set(s);
        typeUpdated.set(Types.STRING);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    void send(Boolean b)
    {
        booleanBuffer.set(b);
        typeUpdated.set(Types.BOOLEAN);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    void send()
    {
        typeUpdated.set(Types.VOID);
        synchronized(lock)
        {
            lock.notify();
        }
    }
}
