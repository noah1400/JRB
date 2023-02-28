package test;

import jrb.JRB;
import jrb.Builder.Builder;
import jrb.Exceptions.JRBException;

public class main {
    
    public static void main(String[] args) throws JRBException {
        String query = JRB.builder()
        .startsWith()
        .anyOf(
            (Builder builder) -> {
                builder
                    .digit()
                    .letter()
                    .oneOf("._%+-");
            }
        ).onceOrMore()
        .literally("@")
        .anyOf(
            (Builder builder) -> {
                builder
                .digit()
                .letter()
                .oneOf(".-");
            }
        ).onceOrMore()
        .literally(".")
        .letter().atLeast(2).mustEnd().caseInsensitive()
        .get();
        System.out.println(query);
    }

}
