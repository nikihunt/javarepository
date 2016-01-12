package com.Log;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by zl on 16/1/12.
 */


public class LogFactory {
    private volatile static LogFactory instance;
    private static final Logger logger = null;

    public static LogFactory getInstance(){
        if(instance == null){
            synchronized (LogFactory.class){
                if(instance == null)
                    instance = new LogFactory();
            }
        }
        return instance;
    }

    private LogFactory(){
        PropertyConfigurator.configure(LogFactory.class.getResource("/log4j.properties"));
    }

    public <T> Logger getLogger(Class<T> cls){
        return Logger.getLogger(cls);
    }
}
