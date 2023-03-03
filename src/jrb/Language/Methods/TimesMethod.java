package jrb.Language.Methods;

import java.util.Arrays;

import jrb.Exceptions.SyntaxException;
import jrb.Language.Helpers.Method;

public class TimesMethod extends Method{

    public TimesMethod(String original, String methodName) {
        super(original, methodName);
    }

    @Override
    public Method setParameters(Object[] parameters) {
        
        Object[] params = Arrays.stream(parameters)
        .filter(item -> !(item instanceof String) ||
                !("times".equalsIgnoreCase((String)item) || "time".equalsIgnoreCase((String)item)))
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
