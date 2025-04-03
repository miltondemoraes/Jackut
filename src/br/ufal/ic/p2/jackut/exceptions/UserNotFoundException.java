package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando dados de usuário são inválidos.
 */

public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException() {
        super("Usuário não cadastrado.");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}