package com.lagopusempire.lagoscheduler;

class SyncTask extends Task
{
    private final Runnable task;
    private final TaskRepeatInstructions repeatInstructions;
    
    public SyncTask(Runnable doneCallback, TaskBehaviorHandler handler, Runnable task, TaskRepeatInstructions repeatInstructions)
    {
        super(doneCallback, handler);
        if(task == null)
        {
            this.task = () -> { };
        }
        else
        {
            this.task = task;
        }
        
        if(repeatInstructions == null)
        {
            this.repeatInstructions = new TaskRepeatInstructions(0, 1, 1);
        }
        else
        {
            this.repeatInstructions = repeatInstructions;
        }
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
