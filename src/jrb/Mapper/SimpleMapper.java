package jrb.Mapper;

import java.util.HashMap;
import java.util.Map;

import jrb.Builder.Builder;

public class SimpleMapper {

    private SimpleMapper() {}
    
    public final static Map<String, Entry> mapper = new HashMap<>() {{
        put("startsWith", 
            new jrb.Mapper.Entry("^", Builder.METHOD_TYPE_ANCHOR, 
                Builder.METHOD_TYPE_BEGIN
        ));
        put("mustEnd", 
            new jrb.Mapper.Entry("$", Builder.METHOD_TYPE_ANCHOR, 
                Builder.METHOD_TYPE_CHARACTER | Builder.METHOD_TYPE_GROUP | Builder.METHOD_TYPE_QUANTIFIER
        ));
        put("onceOrMore",
            new jrb.Mapper.Entry("+", Builder.METHOD_TYPE_QUANTIFIER, 
                Builder.METHOD_TYPE_CHARACTER | Builder.METHOD_TYPE_GROUP
        ));
        put("neverOrMore",
            new jrb.Mapper.Entry("*", Builder.METHOD_TYPE_QUANTIFIER, 
                Builder.METHOD_TYPE_CHARACTER | Builder.METHOD_TYPE_GROUP
        ));
        put("any",
            new jrb.Mapper.Entry(".", Builder.METHOD_TYPE_CHARACTER, 
                Builder.METHOD_TYPES_ALLOWED_FOR_CHARACTERS
        ));
        put("tab",
            new jrb.Mapper.Entry("\\t", Builder.METHOD_TYPE_CHARACTER, 
                Builder.METHOD_TYPES_ALLOWED_FOR_CHARACTERS
        ));
        put("newLine",
            new jrb.Mapper.Entry("\\n", Builder.METHOD_TYPE_CHARACTER, 
                Builder.METHOD_TYPES_ALLOWED_FOR_CHARACTERS
        ));
        put("whitespace",
            new jrb.Mapper.Entry("\\s", Builder.METHOD_TYPE_CHARACTER, 
                Builder.METHOD_TYPES_ALLOWED_FOR_CHARACTERS
        ));
        put("noWhitespace",
            new jrb.Mapper.Entry("\\S", Builder.METHOD_TYPE_CHARACTER, 
                Builder.METHOD_TYPES_ALLOWED_FOR_CHARACTERS
        ));
        put("anyCharacter",
            new jrb.Mapper.Entry("\\w", Builder.METHOD_TYPE_CHARACTER, 
                Builder.METHOD_TYPES_ALLOWED_FOR_CHARACTERS
        ));
        put("noCharacter",
            new jrb.Mapper.Entry("\\W", Builder.METHOD_TYPE_CHARACTER, 
                Builder.METHOD_TYPES_ALLOWED_FOR_CHARACTERS
        ));
        put("backslash",
            new jrb.Mapper.Entry("\\\\", Builder.METHOD_TYPE_CHARACTER, 
                Builder.METHOD_TYPES_ALLOWED_FOR_CHARACTERS
        ));
    }};

}
