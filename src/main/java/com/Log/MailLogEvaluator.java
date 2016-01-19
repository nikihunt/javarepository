package com.log;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;

/**
 * Created by zl on 16/1/12.
 */


public class MailLogEvaluator implements TriggeringEventEvaluator {
    public boolean isTriggeringEvent(LoggingEvent loggingEvent) {
        return loggingEvent.getLevel().isGreaterOrEqual(Level.DEBUG);
    }
}
