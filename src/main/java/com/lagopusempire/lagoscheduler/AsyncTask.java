package com.lagopusempire.lagoscheduler;

final class AsyncTask extends Task
{
    public AsyncTask(Runnable doneCallback, TaskBehaviorHandler handler, Runnable task, TaskRepeatInstructions repeatInstructions)
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
