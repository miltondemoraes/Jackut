package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando há problemas com os dados de usuário.
 */
public class InvalidUserDataException extends JackutException {
    private static final long serialVersionUID = 1L;

    public InvalidUserDataException(String message) {
        super(message);
    }
}