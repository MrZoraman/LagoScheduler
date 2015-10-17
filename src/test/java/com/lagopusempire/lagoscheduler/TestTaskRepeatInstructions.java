package com.lagopusempire.lagoscheduler;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestTaskRepeatInstructions
{
    @Test
    public void testRunOnce()
    {
        TaskRepeatInstructions repeater = new TaskRepeatInstructions(0, 1, 1);
        repeater.cycle();
        assertEquals(true, repeater.shouldRun());
        repeater.cycle();
        assertEquals(false, repeater.shouldRun());
        assertEquals(false, repeater.willRunAgain());
    }
    
    @Test
    public void testDelay()
    {
        TaskRepeatInstructions repeater = new TaskRepeatInstructions(4, 1, 1);
        for(int ii = 0; ii < 4; ii++)
        {
            repeater.cycle();
        }
        assertEquals(false, repeater.shouldRun());
        repeater.cycle();
        assertEquals(true, repeater.shouldRun());
        repeater.cycle();
        assertEquals(false, repeater.shouldRun());
        assertEquals(false, repeater.willRunAgain());
    }
    
    @Test
    public void testDelay_alt_args()
    {
        TaskRepeatInstructions repeater = new TaskRepeatInstructions(4, 0, 0);
        for(int ii = 0; ii < 4; ii++)
        {
            repeater.cycle();
        }
        assertEquals(false, repeater.shouldRun());
        repeater.cycle();
        assertEquals(true, repeater.shouldRun());
        repeater.cycle();
        assertEquals(false, repeater.shouldRun());
        assertEquals(false, repeater.willRunAgain());
    }
    
    @Test
    public void testInfiniteRepeat()
    {
        TaskRepeatInstructions repeater = new TaskRepeatInstructions(0, 10, -1);
        for(int runs = 0; runs < 5; runs++)
        {
            for(int ii = 0; ii < 9; ii++)
            {
                repeater.cycle();
            }
            assertEquals(false, repeater.shouldRun());
            repeater.cycle();
            assertEquals(true, repeater.shouldRun());
            assertEquals(true, repeater.willRunAgain());
        }
    }
    
    @Test
    public void testRepeats()
    {
        TaskRepeatInstructions repeater = new TaskRepeatInstructions(0, 5, 3);
        for(int ii = 0; ii < 4; ii++)
        {
            repeater.cycle();
        }
        assertEquals(false, repeater.shouldRun());
        repeater.cycle();
        assertEquals(true, repeater.shouldRun());
        assertEquals(true, repeater.willRunAgain());
        
        for(int ii = 0; ii < 4; ii++)
        {
            repeater.cycle();
        }
        assertEquals(false, repeater.shouldRun());
        repeater.cycle();
        assertEquals(true, repeater.shouldRun());
        assertEquals(true, repeater.willRunAgain());
        
        for(int ii = 0; ii < 4; ii++)
        {
            repeater.cycle();
        }
        assertEquals(false, repeater.shouldRun());
        repeater.cycle();
        assertEquals(true, repeater.shouldRun());
        
        assertEquals(false, repeater.willRunAgain());
        
        for(int ii = 0; ii < 4; ii++)
        {
            repeater.cycle();
        }
        assertEquals(false, repeater.shouldRun());
        repeater.cycle();
        assertEquals(false, repeater.shouldRun());
        assertEquals(false, repeater.willRunAgain());
    }
    
    @Test
    public void testEverything()
    {
        TaskRepeatInstructions repeater = new TaskRepeatInstructions(5, 8, 3);
        for(int ii = 0; ii < 5; ii++)
        {
            repeater.cycle();
            assertEquals(false, repeater.shouldRun());
            assertEquals(true, repeater.willRunAgain());
        }
        
        for(int ii = 0; ii < 7; ii++)
        {
            repeater.cycle();
            assertEquals(false, repeater.shouldRun());
            assertEquals(true, repeater.willRunAgain());
        }
        
        repeater.cycle();
        assertEquals(true, repeater.shouldRun());
        assertEquals(true, repeater.willRunAgain());
        
        for(int ii = 0; ii < 7; ii++)
        {
            repeater.cycle();
            assertEquals(false, repeater.shouldRun());
            assertEquals(true, repeater.willRunAgain());
        }
        
        repeater.cycle();
        assertEquals(true, repeater.shouldRun());
        assertEquals(true, repeater.willRunAgain());
        
        for(int ii = 0; ii < 7; ii++)
        {
            repeater.cycle();
            assertEquals(false, repeater.shouldRun());
            assertEquals(true, repeater.willRunAgain());
        }
        
        repeater.cycle();
        assertEquals(true, repeater.shouldRun());
        assertEquals(false, repeater.willRunAgain());
        
        for(int ii = 0; ii < 7; ii++)
        {
            repeater.cycle();
            assertEquals(false, repeater.shouldRun());
            assertEquals(false, repeater.willRunAgain());
        }
        
        repeater.cycle();
        assertEquals(false, repeater.shouldRun());
        assertEquals(false, repeater.willRunAgain());
    }
}
