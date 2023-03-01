package jrb.Interfaces;

public abstract class TestMethodProvider {
    
    /**
     * Build and return the resulting regular expression. This will apply the given delimiter and all modifiers.
     *
     * @param String delimiter The delimiter to use. Defaults to '/'. If left empty, avoid using modifiers,
     *                          since they then will be ignored.
     * @param boolean ignoreInvalid Ignore invalid regular expressions.
     * @return string The resulting regular expression.
     */
    abstract public String get(String delimiter, boolean ignoreInvalid);

    /**
     * Test if regular expression matches given string.
     * @param string
     * @param flags
     * @param offset
     * @return
     */
    public boolean isMatching(String string, int  flags, int offset) {
        // TODO: Implement flags and offset
        boolean result = string.matches(this.get("/", false));
        return result;
    }

    public String replace(String string, String replacement, int flags, int offset) {
        return string.replaceAll(this.get("/", false), replacement);
    }

    public String[] split(String string, int flags, int offset) {
        return string.split(this.get("/", false));
    }

    public String[] split(String string, int limit, int flags, int offset) {
        return string.split(this.get("/", false), limit);
    }

    public boolean isValid(String regEx) {
        try {
            java.util.regex.Pattern.compile((regEx == null) ? this.get("/", false) : regEx);
            return true;
        } catch (java.util.regex.PatternSyntaxException e) {
            return false;
        }
    }

    //TODO: Implement following methods:
    // filter
    // getMatches
    // getMatch
    // getMatchGroups

}
