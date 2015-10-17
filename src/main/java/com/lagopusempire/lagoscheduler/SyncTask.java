package com.lagopusempire.lagoscheduler;

class SyncTask extends Task
{
    
    public SyncTask(Runnable doneCallback, TaskBehaviorHandler handler, Runnable task, TaskRepeatInstructions repeatInstructions)
    {
        super(doneCallback, handler, task, repeatInstructions);
        started();
    }
    
    @Override
    public void tick()
    {
        notifyHandlerMethods();
        
        super.tick();
    }
    
    @Override
    public void setDone()
    {
        super.setDone();
        finished();
    }
}
