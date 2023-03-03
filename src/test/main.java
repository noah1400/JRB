package test;

import jrb.JRB;
import jrb.Builder.Builder;
import jrb.Exceptions.JRBException;
import jrb.Exceptions.SyntaxException;

public class Main {
    
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
        String reg = null;
        try {
            reg = new JRB("begin with any of (digit, letter, one of '._%+-') once or more, literally '@', any of (digit, letter, one of '.-') once or more, literally '.', letter at least 2, must end, case insensitive")
            .language().get();
        } catch (SyntaxException e) {
            e.printStackTrace();
        }

        System.out.println(reg);
    }

}
