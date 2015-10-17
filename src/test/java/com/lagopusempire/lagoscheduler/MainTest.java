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
        LagoScheduler scheduler = LagoScheduler.getInstance();
        TaskRepeatInstructions repeatInstructions = new TaskRepeatInstructions(0, 20, -1);//once every second forever
        scheduler.spawnAsyncTask(false, () -> {
            printWithId("Hello from other thread!");
            return true;
        }, repeatInstructions);
    }
    
    private class TaskBehaviorHandlerImpl
    {
        private final String name;
        
        public TaskBehaviorHandlerImpl(String name)
        {
            this.name = name;
        }
        
        private <T> void notifyReceive(T[] stuff)
        {
            for(int ii = 0; ii < stuff.length; ii++)
            {
                printWithId("recieved data! name=" + name + ", id=" + ii + ", data=" + stuff[ii].toString());
            }
        }
        
        void onReceive(Integer[] i)
        {
            notifyReceive(i);
        }
        
        void onReceive(Double[] d)
        {
            notifyReceive(d);
        }
        
        void onReceive(String[] s)
        {
            notifyReceive(s);
        }
        
        void onReceive(Boolean[] b)
        {
            notifyReceive(b);
        }
        
        void onReceive(Object[] o)
        {
            notifyReceive(o);
        }
        
        void onReceive(int voids)
        {
            printWithId(voids + " voids received.");
        }

        void onStart()
        {
            printWithId("onStart");
        }
        
        void onStop()
        {
            printWithId("onStop");
        }
    }
}
