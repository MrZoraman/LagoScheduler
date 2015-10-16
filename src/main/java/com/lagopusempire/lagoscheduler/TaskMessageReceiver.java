package com.lagopusempire.lagoscheduler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TaskMessageReceiver
{
    public Class<?> type();
}
