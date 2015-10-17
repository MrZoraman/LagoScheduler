package com.lagopusempire.lagoscheduler;

public class TaskOperationRepeaterTest implements TaskOperation
{
    public static void main(String[] args)
    {
        TaskOperationRepeater repeater = new TaskOperationRepeater(new TaskOperationRepeaterTest(), 1);
        repeater.run();
    }
    
    int counter = 0;
    
    long lastTimeHere;
    
    TaskOperationRepeaterTest()
    {
        lastTimeHere = System.nanoTime();
    }
    
    @Override
    public boolean doTask()
    {
        counter++;
        
        long now = System.nanoTime();
        
        long delta = now - lastTimeHere;
        System.out.printf("Delta: %d. (%d)%n", delta, counter);
        lastTimeHere = now;
        
        
        return counter == 20;
    }
}
