package com.lagopusempire.lagoscheduler;

class WaitingSyncTask extends Task
{
    private final Runnable task;
    private final TaskRepeatInstructions repeatInstructions;
    
    public WaitingSyncTask(Runnable doneCallback, TaskBehaviorHandler handler, Runnable task, TaskRepeatInstructions repeatInstructions)
    {
        super(doneCallback, handler);
        this.task = task;
        this.repeatInstructions = repeatInstructions;
    }
    
    @Override
    public void tick()
    {
        notifyHandlerMethods();
        
        repeatInstructions.cycle();
        if(repeatInstructions.shouldRun())
        {
            task.run();
        }
        
        if(!repeatInstructions.willRunAgain())
        {
            setDone();
        }
    }
}
