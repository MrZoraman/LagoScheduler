package com.lagopusempire.lagoscheduler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskOperationRepeater implements Runnable
{
    private final long expectedDelta;
    private final TaskOperation toDo;
    
    public TaskOperationRepeater(TaskOperation toDo, int ticksPerSecond)
    {
        this.toDo = toDo;
        this.expectedDelta = (long) ((1.0D / ticksPerSecond) * 1e9);
    }
    
    @Override
    public void run()
    {
        boolean done;
        
        long start;
        long end;
        long delta;
        
        long sleep;
        int sleepInt;
        int sleepMillis;
        int sleepNanos;
        
        do
        {
            start = System.nanoTime();
            
            done = toDo.doTask();
            
            end = System.nanoTime();
            
            delta = end - start;
            if(delta < expectedDelta)
            {
                sleep = expectedDelta - delta;
                sleepInt = (int) sleep;
                sleepMillis = sleepInt / 1000000;
                sleepNanos = sleepInt - (sleepMillis * 1000000);
                
                try
                {
                    Thread.sleep(sleepMillis, sleepNanos);
                }
                catch (InterruptedException ex)
                {
                    done = true;
                }
            }
        } while(!done);
    }
}
