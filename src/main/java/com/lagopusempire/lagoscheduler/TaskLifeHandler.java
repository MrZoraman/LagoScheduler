package com.lagopusempire.lagoscheduler;

public interface TaskLifeHandler
{
    default void onStart() { }
    default void onStop() { }
}
