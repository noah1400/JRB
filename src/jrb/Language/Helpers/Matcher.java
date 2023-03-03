package jrb.Language.Helpers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jrb.Exceptions.SyntaxException;

public class Matcher {
    
    private static Matcher instance = null;

    protected Map<String, MatcherMapperEntry> mapper = new HashMap<String, MatcherMapperEntry>(){{
        //
    }};

    private Matcher(){
        mapper.put("any character", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "anyCharacter"));
        mapper.put("no character", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "noCharacter"));
        mapper.put("multi line", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "multiLine"));
        mapper.put("single line", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "singleLine"));
        mapper.put("case insensitive", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "caseInsensitive"));
        mapper.put("all lazy", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "allLazy"));
        mapper.put("starts with", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "startsWith"));
        mapper.put("begin with", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "startsWith"));
        mapper.put("must end", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "mustEnd"));
        mapper.put("once or more", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "onceOrMore"));
        mapper.put("never or more", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "neverOrMore"));
        mapper.put("new line", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "newLine"));
        mapper.put("whitespace", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "whitespace"));
        mapper.put("no whitespace", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "noWhitespace"));
        mapper.put("all", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "all"));
        mapper.put("anything", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "any"));
        mapper.put("tab", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "tab"));
        mapper.put("backslash", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "backslash"));
        mapper.put("unicode", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "unicode"));
        mapper.put("digit", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "digit"));
        mapper.put("not digit", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "notDigit"));
        mapper.put("number", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "digit"));
        mapper.put("not number", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "notDigit"));
        mapper.put("letter", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "letter"));
        mapper.put("not letter", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "notLetter"));
        mapper.put("uppercase letter", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "uppercaseLetter"));
        mapper.put("not uppercase letter", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "notUppercaseLetter"));
        mapper.put("once", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "once"));
        mapper.put("twice", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "twice"));
        mapper.put("first match", new MatcherMapperEntry(jrb.Language.Methods.SimpleMethod.class.getName(), "firstMatch"));

        mapper.put("literally", new MatcherMapperEntry(jrb.Language.Methods.DefaultMethod.class.getName(), "literally"));
        mapper.put("either of", new MatcherMapperEntry(jrb.Language.Methods.DefaultMethod.class.getName(), "anyOf"));
        mapper.put("any of", new MatcherMapperEntry(jrb.Language.Methods.DefaultMethod.class.getName(), "anyOf"));
        mapper.put("if already had", new MatcherMapperEntry(jrb.Language.Methods.DefaultMethod.class.getName(), "ifAlreadyHad"));
        mapper.put("if not already had", new MatcherMapperEntry(jrb.Language.Methods.DefaultMethod.class.getName(), "ifNotAlreadyHad"));
        mapper.put("if followed by", new MatcherMapperEntry(jrb.Language.Methods.DefaultMethod.class.getName(), "ifFollowedBy"));
        mapper.put("if not followed by", new MatcherMapperEntry(jrb.Language.Methods.DefaultMethod.class.getName(), "ifNotFollowedBy"));
        mapper.put("optional", new MatcherMapperEntry(jrb.Language.Methods.DefaultMethod.class.getName(), "optional"));
        mapper.put("until", new MatcherMapperEntry(jrb.Language.Methods.DefaultMethod.class.getName(), "until"));
        mapper.put("raw", new MatcherMapperEntry(jrb.Language.Methods.DefaultMethod.class.getName(), "raw"));
        mapper.put("one of", new MatcherMapperEntry(jrb.Language.Methods.DefaultMethod.class.getName(), "oneOf"));
        mapper.put("not one of", new MatcherMapperEntry(jrb.Language.Methods.DefaultMethod.class.getName(), "notOneOf"));

        mapper.put("digit from", new MatcherMapperEntry(jrb.Language.Methods.ToMethod.class.getName(), "digit"));
        mapper.put("not digit from", new MatcherMapperEntry(jrb.Language.Methods.ToMethod.class.getName(), "notDigit"));
        mapper.put("number from", new MatcherMapperEntry(jrb.Language.Methods.ToMethod.class.getName(), "digit"));
        mapper.put("not number from", new MatcherMapperEntry(jrb.Language.Methods.ToMethod.class.getName(), "notDigit"));
        mapper.put("letter from", new MatcherMapperEntry(jrb.Language.Methods.ToMethod.class.getName(), "letter"));
        mapper.put("not letter from", new MatcherMapperEntry(jrb.Language.Methods.ToMethod.class.getName(), "notLetter"));
        mapper.put("uppercase letter from", new MatcherMapperEntry(jrb.Language.Methods.ToMethod.class.getName(), "uppercaseLetter"));
        mapper.put("not uppercase letter from", new MatcherMapperEntry(jrb.Language.Methods.ToMethod.class.getName(), "notUppercaseLetter"));

        mapper.put("exactly", new MatcherMapperEntry(jrb.Language.Methods.TimesMethod.class.getName(), "exacty"));
        mapper.put("at least", new MatcherMapperEntry(jrb.Language.Methods.TimesMethod.class.getName(), "atLeast"));

        mapper.put("between", new MatcherMapperEntry(jrb.Language.Methods.AndMethod.class.getName(), "between"));
        mapper.put("capture", new MatcherMapperEntry(jrb.Language.Methods.AndMethod.class.getName(), "capture"));
    }

    public static Matcher getInstance(){
        if(instance == null){
            instance = new Matcher();
        }
        return instance;
    }

    public Method match(String part) throws SyntaxException{
        part = part.trim();
        System.out.println("Trying to match: " + part);
        int maxMatchCount = 0;
        String maxMatch = null;

        for (Map.Entry<String, MatcherMapperEntry> entry : this.mapper.entrySet()) {
            //php
            // $matches = [];
            // preg_match_all('/^(' . str_replace(' ', ') (', $key) . ')/i', $part, $matches, PREG_SET_ORDER);
            // $count = empty($matches) ? 0 : count($matches[0]);

            //java
            String key = entry.getKey();
            String regex = "^(" + key.replaceAll(" ", ") (") + ")";
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher matcher = pattern.matcher(part);
            List<String> matches = new ArrayList<>();
            while (matcher.find()) {
                matches.add(matcher.group());
            }
            int count = matches.size();
            if (count > maxMatchCount) {
                maxMatchCount = count;
                maxMatch = key;
            }
        }

        if (maxMatch != null) {
            MatcherMapperEntry entry = this.mapper.get(maxMatch);

            // create new instance of the class with the given name
            // php: $class = new $entry->class($part, $entry->method);
            // java:

            Method m = null;
            try {
                m = (Method)Class.forName(entry.clazz).getConstructor(String.class, String.class).newInstance(part, entry.method);
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return m;

        }
        throw new SyntaxException("Invalid method: " + part);
    }
}
