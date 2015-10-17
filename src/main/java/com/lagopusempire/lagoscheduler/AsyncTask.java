package com.lagopusempire.lagoscheduler;

final class AsyncTask extends Task implements Runnable
{
    private final Object lock = new Object();
    
    public AsyncTask(Runnable doneCallback, TaskBehaviorHandler handler)
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
        }// while true
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
    protected void onSend()
    {
        synchronized(lock)
        {
            lock.notify();
        }
    }
}
