package com.lagopusempire.lagoscheduler;

public class TaskOperationWrapper implements TaskOperation
{
    private final Runnable runnable;
    
    public TaskOperationWrapper(Runnable runnable)
    {
        this.runnable = runnable;
    }
    
    @Override
    public boolean doTask()
    {
        runnable.run();
        return false;
    }
}
