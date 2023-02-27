package jrb.Exceptions;

public abstract class JRBException extends Exception {
    private static final long serialVersionUID = 1L;
    public JRBException(String message) {
        super(message);
    }
}
