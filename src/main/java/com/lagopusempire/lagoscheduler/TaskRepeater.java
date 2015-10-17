package com.lagopusempire.lagoscheduler;

public class TaskRepeater
{
    private final int delay;
    private final int interval;
    private final int repeats;
    
    private int tick = 0;
    private int timesRun = 0;
    private boolean willRunAgain = true;
    private boolean delayOver;
    
    private boolean shouldRun = false;
    
    public TaskRepeater(int delay, int interval, int repeats)
    {
        this.delay = delay;
        this.delayOver = delay <= 0;
        this.interval = interval;
        this.repeats = repeats;
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
        
        if(timesRun >= repeats)
        {
            shouldRun = false;
        }
        else if(tick % interval == 0)
        {
            shouldRun = true;
            timesRun++;
            if(timesRun >= repeats)
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
