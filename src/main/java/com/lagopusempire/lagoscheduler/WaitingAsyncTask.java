package com.lagopusempire.lagoscheduler;

final class WaitingAsyncTask extends Task implements Runnable
{
    private final Object lock = new Object();
    
    public WaitingAsyncTask(Runnable doneCallback, TaskBehaviorHandler handler)
    {
        super(doneCallback, handler);
    }
    
    @Override
    public void run()
    {
        started();
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
                    setDone();
                }
            }
            
            if(isDone())
            {
                break;
            }
            
            notifyHandlerMethods();
        }
        finished();
    }
    
    @Override
    public void stop()
    {
        super.stop();
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
