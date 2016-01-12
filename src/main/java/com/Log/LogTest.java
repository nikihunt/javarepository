package com.Log;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
/**
 * Created by zl on 16/1/12.
 */


public class LogTest {
    private static final Logger logger = Logger.getLogger(LogTest.class);

    public static void main(String[] args){
        PropertyConfigurator.configure(LogTest.class.getResource("/log4j.properties"));
        logger.info("first log!!!");
    }
}
