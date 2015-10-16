package com.lagopusempire.lagoscheduler;

public class MainTest
{
    private static long getThreadId()
    {
        return Thread.currentThread().getId();
    }
    
    public static void main(String[] args)
    {
        LagoScheduler scheduler = LagoScheduler.getInstance();
        final TestAsyncTask task = new TestAsyncTask();
        final int tid = scheduler.spawnTaskAsync(task);
        Thread t = new Thread(() -> {
            try
            {
                System.out.println("sender thread sleeping for 1 second on thread " + getThreadId());
                Thread.sleep(1000);
                System.out.println("sending int 24 to test task on thread " + getThreadId());
                scheduler.sendInt(tid, 24);
                
                System.out.println("sender thread sleeping for 1 second on thread " + getThreadId());
                Thread.sleep(1000);
                System.out.println("sending double 3.141 to test task on thread " + getThreadId());
                scheduler.sendDouble(tid, 3.141);
                
                System.out.println("sender thread sleeping for 1 second on thread " + getThreadId());
                Thread.sleep(1000);
                System.out.println("sending string 'foomfah' to test task on thread " + getThreadId());
                scheduler.sendString(tid, "foomfah");
                
                System.out.println("sender thread sleeping for 1 second on thread " + getThreadId());
                Thread.sleep(1000);
                System.out.println("sending boolean false to test task on thread " + getThreadId());
                scheduler.sendBoolean(tid, false);
                
                System.out.println("sender thread sleeping for 1 second on thread " + getThreadId());
                Thread.sleep(1000);
                System.out.println("sending void to test task on thread " + getThreadId());
                scheduler.sendVoid(tid);
                
                System.out.println("sender thread sleeping for 1 second on thread " + getThreadId());
                Thread.sleep(1000);
                System.out.println("stopping test task on thread " + getThreadId());
                scheduler.stop(tid);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        });
        t.setDaemon(false);
        t.start();
    }
    
    static class TestAsyncTask implements AsyncTaskMessageHandler
    {
        @Override
        public void onStart()
        {
            System.out.println("Async task started on thread " + getThreadId());
        }
        
        @Override
        public void onStop()
        {
            System.out.println("Async task stopped on thread " + getThreadId());
        }
        
        @Override
        public void onIntReceive(int i)
        {
            System.out.println("Received int " + i + " on thread " + getThreadId());
        }
        
        @Override
        public void onDoubleReceive(double d)
        {
            System.out.println("Received double " + d + " on thread " + getThreadId());
        }
        
        @Override
        public void onStringReceive(String s)
        {
            System.out.println("Received string '" + s + "' on thread " + getThreadId());
        }
        
        @Override
        public void onBooleanReceive(boolean b)
        {
            System.out.println("Received boolean " + b + " on thread " + getThreadId());
        }
        
        @Override
        public void onVoidReceive()
        {
            System.out.println("Received void on thread " + getThreadId());
        }
    }
}
