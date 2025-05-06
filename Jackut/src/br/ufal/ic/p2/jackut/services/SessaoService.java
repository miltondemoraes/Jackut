package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.exceptions.AuthenticationException;
import br.ufal.ic.p2.jackut.exceptions.SessionNotFoundException;

import java.io.Serial;
import java.io.Serializable;

/**
 * Serviço responsável por gerenciar sessões de usuários.
 * Permite abrir e encerrar sessões, além de validar credenciais.
 */
public class SessaoService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Repositório central de dados */
    private final DataRepository repository;

    /** Serviço de usuários */
    private final UsuarioService usuarioService;

    /**
     * Construtor que inicializa o serviço com as dependências necessárias.
     *
     * @param repository Repositório central de dados
     * @param usuarioService Serviço de usuários
     */
    public SessaoService(DataRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }

    /**
     * Abre uma sessão para um usuário após validar suas credenciais.
     *
     * @param login Login do usuário
     * @param senha Senha do usuário
     * @return ID da sessão criada
     * @throws AuthenticationException Se as credenciais forem inválidas
     */
    public String abrirSessao(String login, String senha) {
        if (!usuarioService.existeUsuario(login)) {
            throw new AuthenticationException();
        }

        if (!usuarioService.validarSenha(login, senha)) {
            throw new AuthenticationException();
        }

        return repository.criarSessao(login);
    }

    /**
     * Encerra uma sessão específica.
     *
     * @param sessionId ID da sessão a ser encerrada
     * @return true se a sessão foi encerrada, false se não existia
     */
    public boolean encerrarSessao(String sessionId) {
        if (repository.existeSessao(sessionId)) {
            repository.removerSessao(sessionId);
            return true;
        }
        return false;
    }

    /**
     * Verifica se uma sessão existe.
     *
     * @param sessionId ID da sessão
     * @return true se a sessão existir, false caso contrário
     */
    public boolean existeSessao(String sessionId) {
        return repository.existeSessao(sessionId);
    }

    /**
     * Obtém o login do usuário associado a uma sessão.
     *
     * @param sessionId ID da sessão
     * @return Login do usuário ou null se a sessão não existir
     */
    public String getLoginDaSessao(String sessionId) {
        return repository.getLoginDaSessao(sessionId);
    }

    /**
     * Valida uma sessão e retorna o login associado.
     * Também suporta o caso em que o próprio sessionId é um login válido.
     *
     * @param sessionId ID da sessão ou login do usuário
     * @return Login do usuário
     * @throws SessionNotFoundException Se a sessão não existir e o sessionId não for um login válido
     */
    public String validarEObterLogin(String sessionId) {
        // Verifica se o sessionId é null ou vazio
        if (sessionId == null || sessionId.isEmpty()) {
            throw new SessionNotFoundException("Usuário não cadastrado.");
        }

        String login = getLoginDaSessao(sessionId);

        // Se não encontrou o login na sessão, verifica se o próprio sessionId é um login válido
        // (compatibilidade com o comportamento original)
        if (login == null) {
            if (usuarioService.existeUsuario(sessionId)) {
                return sessionId;
            }
            throw new SessionNotFoundException("Usuário não cadastrado.");
        }

        return login;
    }

    /**
     * Remove todas as sessões do sistema.
     */
    public void zerarSessoes() {
        repository.getSessoes().clear();
    }
}