package test;

import jrb.Closure;
import jrb.JRB;
import jrb.Builder.Builder;
import jrb.Exceptions.JRBException;

public class main {
    
    public static void main(String[] args) throws JRBException {
        String query = JRB.builder()
        .startsWith()
        .anyOf(
            new Closure() {
                @Override
                public void execute(Builder builder) throws JRBException {
                    builder
                    .digit()
                    .letter()
                    .oneOf("._%+-");
                }
            }
        ).onceOrMore()
        .literally("@")
        .anyOf(
            new Closure() {
                @Override
                public void execute(Builder builder) throws JRBException {
                    builder
                    .digit()
                    .letter()
                    .oneOf(".-");
                }
            }
        ).onceOrMore()
        .literally(".")
        .letter().atLeast(2).mustEnd().caseInsensitive()
        .get("", false);
        System.out.println(query);
    }

}
