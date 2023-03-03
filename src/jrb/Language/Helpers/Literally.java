package jrb.Language.Helpers;

public class Literally {

    protected String string = "";

    public Literally(String raw) {
        this.string = string.replace("\\", "");
    }

    public String getString() {
        return this.string;
    }
    
}
