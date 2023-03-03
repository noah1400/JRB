package jrb;

import jrb.Builder.Builder;
import jrb.Exceptions.InterpreterException;
import jrb.Exceptions.SyntaxException;
import jrb.Language.Interpreter;

public class JRB {

    protected Interpreter language;

    public JRB(String query) throws SyntaxException, InterpreterException {
        this.language = new Interpreter(query);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Interpreter language() {
        return this.language;
    }
}