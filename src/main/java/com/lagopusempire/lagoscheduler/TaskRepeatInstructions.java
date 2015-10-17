package com.lagopusempire.lagoscheduler;

public class TaskRepeatInstructions
{
    private final int delay;
    private final int interval;
    private final int repeats;
    
    private int tick = 0;
    private int timesRun = 0;
    private boolean willRunAgain = true;
    private boolean delayOver;
    
    private boolean shouldRun = false;
    
    public static final TaskRepeatInstructions FOREVER()
    {
        return new TaskRepeatInstructions(0, 0, -1);
    }
    
    public static final TaskRepeatInstructions ONCE()
    {
        return new TaskRepeatInstructions(0, 1, 1);
    }
    
    public TaskRepeatInstructions(int delay, int interval, int repeats)
    {
        this.delay = delay;
        this.delayOver = delay <= 0;
        this.interval = interval == 0 ? 1 : interval;
        this.repeats = repeats == 0 ? 1 : repeats;
    }
    
    public TaskRepeatInstructions dupe(TaskRepeatInstructions instructions)
    {
        return new TaskRepeatInstructions(instructions.delay, instructions.interval, instructions.repeats);
    }
    
    public void cycle()
    {
        tick++;
        
        if(!delayOver)
        {
            if(tick < delay)
            {
                shouldRun = false;
            }
            else
            {
                delayOver = true;
                tick = 0;
            }
            return;
        }
        
        if(repeats > 0 && timesRun >= repeats)
        {
            shouldRun = false;
        }
        else if(tick % interval == 0)
        {
            shouldRun = true;
            timesRun++;
            if(repeats > 0 && timesRun >= repeats)
            {
                willRunAgain = false;
            }
        }
        else
        {
            shouldRun = false;
        }
    }
    
    public boolean shouldRun()
    {
        return shouldRun;
    }
    
    public boolean willRunAgain()
    {
        return willRunAgain;
    }
}
