package com.memorymeasurer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import objectexplorer.MemoryMeasurer;

import java.util.List;
import java.util.Map;

import static com.util.Print.*;

/**
 * Created by zl on 16/1/15.
 * 查看对象占用内存大小
 */


public class ObjMemoryMeasurer {
    public static void main(String[] args){
        Map<String,String> map = Maps.newHashMap();
        List<String> lst = Lists.newArrayList();
        Long a = 0L;
        long memory = MemoryMeasurer.measureBytes(a);
        print(Long.toString(memory));
    }
}
