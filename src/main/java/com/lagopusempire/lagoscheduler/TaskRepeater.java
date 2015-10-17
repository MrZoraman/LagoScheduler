package com.lagopusempire.lagoscheduler;

public class TaskRepeater
{
    private final int delay;
    private final int interval;
    private final int repeats;
    
    private int tick = 0;
    private int timesRun = 0;
    private boolean willRunAgain = true;
    
    private boolean shouldRun = false;
    
    public TaskRepeater(int delay, int interval, int repeats)
    {
        this.delay = delay;
        this.interval = interval;
        this.repeats = repeats;
    }
    
    public void cycle()
    {
        if(tick < delay)
        {
            shouldRun = false;
            return;
        }
        
        tick++;
        
        if(repeats > 0 && repeats > timesRun)
        {
            shouldRun = false;
            willRunAgain = false;
        }
        else if(tick % interval == 0)
        {
            shouldRun = true;
            timesRun++;
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
