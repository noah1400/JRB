package jrb.Language.Helpers;

public class StringPosition {

    public final static StringPosition EMPTY_POSITION = new StringPosition(-4000, -4000);
    public final static int EMPTY = -4000;
    
    public int start = -4000;
    public int end = -4000;

    public StringPosition(int start, int end) {
        this.start = start;
        this.end = end;
    }

}
