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
    
    @Override
    public void run()
    {
        onStart();
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
                onStop();
                break;
            }
            
            final Types type = typeUpdated.get();
            switch(type)
            {
                case INT:
                    onIntReceive(intBuffer.get());
                    break;
                case DOUBLE:
                    double d = Double.longBitsToDouble(doubleBuffer.get());
                    onDoubleReceive(d);
                    break;
                case STRING:
                    onStringReceive(stringBuffer.get());
                    break;
                case BOOLEAN:
                    onBooleanReceive(booleanBuffer.get());
                    break;
                case VOID:
                    onVoidReceive();
                    break;
            }
        }
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
    
    void setDouble(double d)
    {
        long bits = Double.doubleToLongBits(d);
        doubleBuffer.set(bits);
        typeUpdated.set(Types.DOUBLE);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    void setString(String s)
    {
        stringBuffer.set(s);
        typeUpdated.set(Types.STRING);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    void setBoolean(Boolean b)
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
    
    protected void onIntReceive(int i) { }
    protected void onDoubleReceive(double d) { }
    protected void onStringReceive(String s) { }
    protected void onBooleanReceive(boolean b) { }
    protected void onVoidReceive() { }
    
    protected void onStart() { }
    protected void onStop() { }
}
