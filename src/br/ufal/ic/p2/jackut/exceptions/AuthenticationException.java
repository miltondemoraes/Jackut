package br.ufal.ic.p2.jackut.exceptions;

/**
 * Exceção lançada quando há problemas com credenciais de login.
 */
public class AuthenticationException extends JackutException {
    private static final long serialVersionUID = 1L;

    public AuthenticationException() {
        super("Login ou senha inválidos.");
    }

    public AuthenticationException(String message) {
        super(message);
    }
}