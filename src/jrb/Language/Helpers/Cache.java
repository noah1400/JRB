package jrb.Language.Helpers;


import java.util.Map;
import java.util.HashMap;

import jrb.Builder.Builder;

public class Cache {
    
    protected static Map<String, Builder> cache = new HashMap<>();

    /*
     * Add Builder for JRL to cache
     * @param jrl The JRL to add
     * @param builder The Builder to add
     */
    public static void add(String jrl, Builder builder){
        cache.put(jrl, builder);
    }

    public static boolean has(String jrl){
        return cache.containsKey(jrl);
    }

    public static Builder get(String jrl){
        return cache.get(jrl) != null ? cache.get(jrl) : new Builder();
    }

}
