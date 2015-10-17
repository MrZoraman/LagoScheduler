package com.lagopusempire.lagoscheduler;

public class MainTest
{
    private static long getThreadId()
    {
        return Thread.currentThread().getId();
    }
    
    private static void printWithId(String message)
    {
        System.out.println("-[" + getThreadId() + "]- " + message + " -[" + getThreadId() + "]-");
    }
    
    public static void main(String[] args)
    {
        printWithId("Hello from main thread!");
        
        TaskBehaviorHandler handler = new TaskBehaviorHandlerImpl("async");
        
        final LagoScheduler scheduler = LagoScheduler.getInstance();
        TaskRepeatInstructions repeatInstructions = new TaskRepeatInstructions(0, 20, -1);//once every second forever
        final int asyncTid = scheduler.spawnAsyncTask(false, handler, () -> {
            printWithId("Hello from other thread!");
            return false;
        }, repeatInstructions);
        
        scheduler.spawnAsyncTask(false, () -> {
            printWithId("sending 42 to other async thread!");
            boolean received = scheduler.send(asyncTid, 42);
            printWithId("Was my number received? " + received);
            return false;
        }, repeatInstructions.dupe());
        
        scheduler.run();
    }
    
    private static class TaskBehaviorHandlerImpl implements TaskBehaviorHandler
    {
        private final String name;
        
        public TaskBehaviorHandlerImpl(String name)
        {
            this.name = name;
        }
        
        private void notifyReceive(Object[] stuff)
        {
            for(int ii = 0; ii < stuff.length; ii++)
            {
                printWithId("recieved data! name=" + name + ", id=" + ii + ", data=" + stuff[ii]);
            }
        }
        
        @Override
        public void onReceive(Object[] stuff)
        {
            notifyReceive(stuff);
        }

        @Override
        public void onStart()
        {
            printWithId("onStart");
        }
        
        @Override
        public void onStop()
        {
            printWithId("onStop");
        }
    }
}
