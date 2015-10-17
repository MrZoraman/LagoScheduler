package com.lagopusempire.lagoscheduler;


class SyncTask extends Task
{
    public SyncTask(Runnable doneCallback, TaskBehaviorHandler handler)
    {
        super(doneCallback, handler);
    }
    
    @Override
    public void tick()
    {
        notifyHandlerMethods();
    }
}
