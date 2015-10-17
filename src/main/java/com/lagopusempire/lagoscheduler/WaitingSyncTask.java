package com.lagopusempire.lagoscheduler;

class WaitingSyncTask extends Task
{
    public WaitingSyncTask(Runnable doneCallback, TaskBehaviorHandler handler)
    {
        super(doneCallback, handler);
    }
    
    @Override
    public void tick()
    {
        notifyHandlerMethods();
    }
}
