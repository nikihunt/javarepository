package test;

import com.log.LoggerFactory;
import org.apache.log4j.Logger;

/**
 * Created by zl on 16/1/12.
 */


public class LogTest {
    public static void main(String[] args){
        Logger logger = LoggerFactory.getInstance().getLogger(LogTest.class);
        logger.info("first log!!!");
    }
}
