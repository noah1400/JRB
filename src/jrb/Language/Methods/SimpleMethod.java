package jrb.Language.Methods;

import jrb.Exceptions.SyntaxException;
import jrb.Language.Helpers.Method;

public class SimpleMethod extends Method{

    public SimpleMethod(String original, String methodName) {
        super(original, methodName);
    }

    @Override
    public Method setParameters(Object[] parameters) {
        
        if (parameters.length > 0) {
            try {
                throw new SyntaxException("Invalid parameter.");
            } catch (SyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return this;

    }

}
