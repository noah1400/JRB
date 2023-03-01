package jrb.Language.Helpers;

public class Matcher {
    
    private static Matcher instance = null;

    private Matcher(){}

    public static Matcher getInstance(){
        if(instance == null){
            instance = new Matcher();
        }
        return instance;
    }

    public Method match(String query){
        return null;
    }
}
