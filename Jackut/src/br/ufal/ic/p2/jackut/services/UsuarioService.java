package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.entities.Usuario;
import br.ufal.ic.p2.jackut.exceptions.InvalidUserDataException;
import br.ufal.ic.p2.jackut.exceptions.ProfileAttributeException;
import br.ufal.ic.p2.jackut.exceptions.UserNotFoundException;

import java.io.Serial;
import java.io.Serializable;

/**
 * Serviço responsável por gerenciar os usuários do sistema Jackut.
 * Fornece operações para criar, consultar, editar e remover usuários,
 * além de gerenciar seus atributos de perfil.
 */
public class UsuarioService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Repositório central de dados do sistema */
    private final DataRepository repository;

    /**
     * Construtor que inicializa o serviço com o repositório de dados.
     *
     * @param repository Repositório central de dados
     */
    public UsuarioService(DataRepository repository) {
        this.repository = repository;
    }

    /**
     * Cria um novo usuário no sistema após validar seus dados.
     *
     * @param login Login único do usuário
     * @param senha Senha para autenticação
     * @param nome Nome completo do usuário
     * @throws InvalidUserDataException Se o login já existir ou se os dados forem inválidos
     */
    public void criarUsuario(String login, String senha, String nome) {
        validarDadosUsuario(login, senha);

        if (repository.existeUsuario(login)) {
            throw new InvalidUserDataException("Conta com esse nome já existe.");
        }

        Usuario novoUsuario = new Usuario(login, senha, nome);
        repository.adicionarUsuario(novoUsuario);
    }

    /**
     * Valida os dados básicos de um usuário.
     *
     * @param login Login do usuário
     * @param senha Senha do usuário
     * @throws InvalidUserDataException Se o login ou senha forem nulos ou vazios
     */
    private void validarDadosUsuario(String login, String senha) {
        if (login == null || login.isEmpty()) {
            throw new InvalidUserDataException("Login inválido.");
        }

        if (senha == null || senha.isEmpty()) {
            throw new InvalidUserDataException("Senha inválida.");
        }
    }

    /**
     * Obtém um usuário pelo login.
     *
     * @param login Login do usuário
     * @return Objeto Usuario correspondente
     * @throws UserNotFoundException Se o usuário não existir
     */
    public Usuario getUsuario(String login) {
        Usuario usuario = repository.getUsuario(login);

        if (usuario == null) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        return usuario;
    }

    /**
     * Verifica se existe um usuário com o login especificado.
     *
     * @param login Login do usuário
     * @return true se o usuário existir, false caso contrário
     */
    public boolean existeUsuario(String login) {
        return repository.existeUsuario(login);
    }

    /**
     * Obtém o valor de um atributo de um usuário.
     * Suporta atributos especiais (nome, login) e atributos personalizados.
     *
     * @param login Login do usuário
     * @param atributo Nome do atributo
     * @return Valor do atributo
     * @throws UserNotFoundException Se o usuário não existir
     * @throws ProfileAttributeException Se o atributo personalizado não estiver preenchido
     */
    public String getAtributoUsuario(String login, String atributo) {
        Usuario usuario = getUsuario(login);

        switch (atributo) {
            case "nome":
                return usuario.getNome();
            case "login":
                return usuario.getLogin();
            default:
                if (!usuario.getAtributos().containsKey(atributo)) {
                    throw new ProfileAttributeException("Atributo não preenchido.");
                }
                return usuario.getAtributos().get(atributo);
        }
    }

    /**
     * Edita um atributo personalizado do perfil de um usuário.
     *
     * @param login Login do usuário
     * @param atributo Nome do atributo
     * @param valor Novo valor do atributo
     * @throws UserNotFoundException Se o usuário não existir
     */
    public void editarPerfil(String login, String atributo, String valor) {
        Usuario usuario = getUsuario(login);
        usuario.getAtributos().put(atributo, valor);
    }

    /**
     * Remove um usuário do sistema.
     *
     * @param login Login do usuário a ser removido
     * @throws UserNotFoundException Se o usuário não existir
     */
    public void removerUsuario(String login) {
        if (!repository.existeUsuario(login)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        repository.removerUsuario(login);
    }

    /**
     * Valida a senha de um usuário.
     *
     * @param login Login do usuário
     * @param senha Senha a ser validada
     * @return true se a senha for válida, false caso contrário
     * @throws UserNotFoundException Se o usuário não existir
     */
    public boolean validarSenha(String login, String senha) {
        Usuario usuario = getUsuario(login);
        return usuario.getSenha().equals(senha);
    }

    /**
     * Remove todos os usuários do sistema.
     */
    public void zerarUsuarios() {
        repository.getUsuarios().clear();
    }
}