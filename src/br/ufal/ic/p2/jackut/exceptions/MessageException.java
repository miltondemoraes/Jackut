package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando há problemas com recados.
 */
public class MessageException extends JackutException {
    private static final long serialVersionUID = 1L;

    public MessageException(String message) {
        super(message);
    }
}