package com.lagopusempire.lagoscheduler;

public class RepeatingSyncTask extends WaitingSyncTask
{
    private final TaskOperation toDo;
    private final TaskRepeatInstructions repeater;
    
    public RepeatingSyncTask(Runnable doneCallback, TaskBehaviorHandler handler, TaskRepeatInstructions repeater, TaskOperation toDo)
    {
        super(doneCallback, handler);
        this.toDo = toDo;
        this.repeater = repeater;
    }
    
    @Override
    public void tick()
    {
        super.tick();
        
        if(repeater.shouldRun())
        {
            boolean result = toDo.doTask();
            if(result)
            {
                setDone();
            }
        }
    }
}
