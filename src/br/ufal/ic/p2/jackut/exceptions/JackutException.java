package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção base para todas as exceções do sistema Jackut.
 */
public class JackutException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JackutException(String message) {
        super(message);
    }
}
