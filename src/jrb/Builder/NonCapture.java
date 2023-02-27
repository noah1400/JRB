package jrb.Builder;

public class NonCapture extends Builder {
    /**
     * Desired match group
     */
    String g = "(?:%s)";

    /**
     * Get group
     */
    @Override
    protected String getGroup() {
        return g;
    }
}
