package jrb.Builder;

public class Capture extends Builder{
    /**
     * Desired match group
     */
    String g = "(%s)";

    /**
     * Set name for capture group
     * 
     * @param name
     */
    public void setName(String name) {
        g = "(?<" + name + ">%s)";
    }

    /**
     * Get group
     */
    @Override
    protected String getGroup() {
        return g;
    }
}
