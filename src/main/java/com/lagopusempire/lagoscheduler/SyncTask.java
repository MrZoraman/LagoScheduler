package com.lagopusempire.lagoscheduler;

class SyncTask implements Runnable
{
    private final TaskBehaviorHandler handler;
    
    SyncTask(TaskBehaviorHandler handler)
    {
        this.handler = handler;
    }
    
    @Override
    public void run()
    {
        handler.run();
    }
}
