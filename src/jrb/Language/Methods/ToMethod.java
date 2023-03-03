package jrb.Language.Methods;

import java.util.Arrays;

import jrb.Exceptions.SyntaxException;
import jrb.Language.Helpers.Method;

public class ToMethod extends Method {

    public ToMethod(String original, String methodName) {
        super(original, methodName);
    }

    @Override
    public Method setParameters(Object[] parameters) {
        Object[] params = Arrays.stream(parameters)
                .filter(item -> !(item instanceof String) || !((String) item).toLowerCase().equals("to"))
                .toArray();
        if (params.length > 1) {
            try {
                throw new SyntaxException("Invalid parameter.");
            } catch (SyntaxException e) {
                e.printStackTrace();
            }
        }
        return super.setParameters(params);

    }

}
