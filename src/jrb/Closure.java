package jrb;

import jrb.Builder.Builder;
import jrb.Exceptions.JRBException;

public interface Closure {
    public void execute(Builder query) throws JRBException; 
}
