package com.lagopusempire.lagoscheduler;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

final class WaitingAsyncTask extends Task
{
    private final Object lock = new Object();
    private final AtomicBoolean done = new AtomicBoolean(false);
    
    public WaitingAsyncTask(Runnable doneCallback, TaskBehaviorHandler handler)
    {
        super(doneCallback, handler);
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
            
            resetTypeUpdated();
        }
        doneCallback.run();
    }
    
    @Override
    public void stop()
    {
        done.set(true);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    @Override
    public void send(int i)
    {
        super.send(i);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    @Override
    void send(double d)
    {
        super.send(d);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    @Override
    void send(String s)
    {
        super.send(s);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    @Override
    void send(Boolean b)
    {
        super.send(b);
        synchronized(lock)
        {
            lock.notify();
        }
    }
    
    @Override
    void send()
    {
        super.send();
        synchronized(lock)
        {
            lock.notify();
        }
    }
}
