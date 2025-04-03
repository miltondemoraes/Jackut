package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando há problemas com atributos de perfil.
 */
public class ProfileAttributeException extends JackutException {
    private static final long serialVersionUID = 1L;

    public ProfileAttributeException(String message) {
        super(message);
    }
}