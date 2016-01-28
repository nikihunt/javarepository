package com.util;


import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.*;

/**
 * Created by zl on 16/1/27.
 */


public class jackson {
    public static final ObjectMapper mapper = new ObjectMapper();


    public static void main(String[] args) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule mod = new SimpleModule("test", Version.unknownVersion());

        // defaults: LinkedHashMap, ArrayList
        mod.addAbstractTypeMapping(Map.class, HashMap.class);
        //mod.addAbstractTypeMapping(List.class, LinkedList.class);
        mapper.registerModule(mod);
        final String ss = "{\"documents\": " +
                "[{\"docid\":\"0CCaioMJ\",\"title\":\"世界 首次 ! 看 了 才 知道 , 中国 太 厉害 了\",\"date\":\"2016-01-28 08:00:00\",\"score\":100.0,\"tags\":[\"weibo_pop\"],\"iid\":180317130," +
                "\"detail\":{\"fid\":\"c1\",\"score\":\"100.0\",\"bkts\":\"chrnk-gbdt-val-srch\",\"feakey\":\"c1_4846498\",\"mashtype\":\"ch-rel\",\"factor\":\"stickie-ct\",\"point\":\"1407783865\"}}]}";
        final Map<String,Object> deser = mapper.readValue(ss, HashMap.class);
        final JsonNode node = mapper.readValue(ss,JsonNode.class);
        System.out.println("Class of deser's nested object is " + node.get("documents").getClass().getSimpleName());
    }
}
