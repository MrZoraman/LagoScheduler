package com.lagopusempire.lagoscheduler;

import java.util.concurrent.atomic.*;

public class AsyncTask implements Runnable
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
    private final AsyncTaskMessageHandler handler;
    
    AsyncTask(Runnable doneCallback, AsyncTaskMessageHandler handler)
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
                catch (InterruptedException ex)
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
                    handler.onIntReceive(intBuffer.get());
                    break;
                case DOUBLE:
                    double d = Double.longBitsToDouble(doubleBuffer.get());
                    handler.onDoubleReceive(d);
                    break;
                case STRING:
                    handler.onStringReceive(stringBuffer.get());
                    break;
                case BOOLEAN:
                    handler.onBooleanReceive(booleanBuffer.get());
                    break;
                case VOID:
                    handler.onVoidReceive();
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
    
    void sendInt(int i)
    {
        intBuffer.set(i);
        typeUpdated.set(Types.INT);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    void sendDouble(double d)
    {
        long bits = Double.doubleToLongBits(d);
        doubleBuffer.set(bits);
        typeUpdated.set(Types.DOUBLE);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    void sendString(String s)
    {
        stringBuffer.set(s);
        typeUpdated.set(Types.STRING);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    void sendBoolean(Boolean b)
    {
        booleanBuffer.set(b);
        typeUpdated.set(Types.BOOLEAN);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    void sendVoid()
    {
        typeUpdated.set(Types.VOID);
        synchronized(lock)
        {
            lock.notify();
        }
    }
}
