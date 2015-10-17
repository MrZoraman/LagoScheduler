package com.lagopusempire.lagoscheduler;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class TaskBehaviorHandler
{
    private AtomicInteger tid = new AtomicInteger();
    
    void setTid(int id)
    {
        tid.set(id);
    }
    
    protected final int getTid()
    {
        return tid.get();
    }
    
    public final void stop()
    {
        LagoScheduler.getInstance().stop(getTid());
    }
    
    protected void onReceive(int i) { }
    protected void onReceive(double d) { }
    protected void onReceive(String s) { }
    protected void onReceive(boolean b) { }
    protected void onReceive(Object o) { }
    protected void onReceive() { }
    
    protected void onStart() { }
    protected void onStop() { }
}
