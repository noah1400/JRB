package jrb.Language.Helpers;

import java.beans.BeanProperty;

public class MatcherMapperEntry {
    
    public String clazz = null;

    public String method = null;

    public MatcherMapperEntry(String clazz, String method) {
        this.clazz = clazz.trim();
        this.method = method.trim();
    }

}
