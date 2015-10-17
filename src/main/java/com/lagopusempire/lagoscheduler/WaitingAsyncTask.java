package com.lagopusempire.lagoscheduler;

import java.util.concurrent.atomic.*;

public final class WaitingAsyncTask implements Runnable
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
    private final DataReceiveHandler dataHandler;
    private final TaskLifeHandler lifeHandler;
    
    WaitingAsyncTask(Runnable doneCallback, DataReceiveHandler dataHandler, TaskLifeHandler lifeHandler)
    {
        this.doneCallback = doneCallback;
        this.dataHandler = dataHandler;
        this.lifeHandler = lifeHandler;
    }
    
    @Override
    public void run()
    {
        lifeHandler.onStart();
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
                lifeHandler.onStop();
                break;
            }
            
            final Types type = typeUpdated.get();
            switch(type)
            {
                case INT:
                    dataHandler.onReceive(intBuffer.get());
                    break;
                case DOUBLE:
                    double d = Double.longBitsToDouble(doubleBuffer.get());
                    dataHandler.onReceive(d);
                    break;
                case STRING:
                    dataHandler.onReceive(stringBuffer.get());
                    break;
                case BOOLEAN:
                    dataHandler.onReceive(booleanBuffer.get());
                    break;
                case VOID:
                    dataHandler.onReceive();
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
