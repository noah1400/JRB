package test;

import jrb.JRB;
import jrb.Builder.Builder;
import jrb.Exceptions.JRBException;

public class Main {
    
    public static void main(String[] args) throws JRBException {
        String query = JRB.builder()
        .startsWith()
        .anyOf(
            JRB.builder().digit().letter().oneOf("._%+-").get()
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
        System.out.println(JRB.builder().digit().letter().oneOf("._%+-").get());
    }

}
