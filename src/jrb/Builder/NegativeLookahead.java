package jrb.Builder;

public class NegativeLookahead extends Builder{
    /**
     * Desired match group
     */
    String g = "(?!%s)";

    /**
     * Get group
     */
    @Override
    protected String getGroup() {
        return g;
    }
}
