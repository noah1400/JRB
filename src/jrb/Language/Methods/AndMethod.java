package jrb.Language.Methods;

import java.util.Arrays;

import jrb.Language.Helpers.Method;

public class AndMethod extends Method{

    public AndMethod(String original, String methodName) {
        super(original, methodName);
    }

    @Override
    public Method setParameters(Object[] parameters) {

        Object[] params = Arrays.stream(parameters)
                .filter(item -> !(item instanceof String))
                .filter(item -> !Arrays.asList("and", "times", "time").contains(((String)item).toLowerCase()))
                .toArray();

        return super.setParameters(params);
    }

}
