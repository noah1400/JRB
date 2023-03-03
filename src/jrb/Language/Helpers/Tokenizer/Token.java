package jrb.Language.Helpers.Tokenizer;

public class Token {
    public final static int T_WORD = 0;
    public final static int T_STRING = 1;
    public final static int T_NUMBER = 2;
    public final static int T_LPAREN = 3;
    public final static int T_RPAREN = 4;

    public String raw = "";
    int type = -1;
    public int position = -1;

    public Token(String raw, int type, int position) {
        this.raw = raw;
        this.type = type;
    }

    public boolean matches(String raw) {
        return this.raw.equalsIgnoreCase(raw);
    }

    public boolean matches(int type) {
        return this.type == type;
    }

    public boolean character() {
        return this.type == T_WORD && this.raw.length() == 1;
    }

    public boolean number() {
        return this.type == T_NUMBER;
    }

    private String typeString(int type) {
        switch (type) {
        case T_WORD:
            return "T_WORD";
        case T_STRING:
            return "T_STRING";
        case T_NUMBER:
            return "T_NUMBER";
        case T_LPAREN:
            return "T_LPAREN";
        case T_RPAREN:
            return "T_RPAREN";
        default:
            return "T_UNKNOWN";
        }
    }

    @Override
    public String toString() {
        return "Token [raw=" + raw + ", type=" + typeString(type) + "]";
    }
}
