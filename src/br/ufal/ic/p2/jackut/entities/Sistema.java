package br.ufal.ic.p2.jackut.entities;

import br.ufal.ic.p2.jackut.exceptions.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * A classe Sistema representa o sistema principal do Jackut.
 * Implementa a interface Serializable para permitir a serialização dos objetos.
 */
public class Sistema implements Serializable {
    private Map<String, Usuario> usuarios;
    private Map<String, String> sessoes;
    private int nextSessionId;
    private static final long serialVersionUID = 1L;

    /**
     * Construtor da classe Sistema.
     * Inicializa as coleções de usuários e sessões, e o ID da próxima sessão.
     */
    public Sistema() {
        this.sessoes = new HashMap<>();
        this.nextSessionId = 1;
        this.usuarios = new HashMap<>();
    }

    /**
     * Reseta o sistema, limpando as coleções de usuários e sessões.
     */
    public void zerarSistema() {
        this.sessoes = new HashMap<>();
        this.nextSessionId = 1;
        this.usuarios = new HashMap<>();
    }

    /**
     * Verifica se um usuário existe no sistema.
     *
     * @param login O login do usuário.
     * @return O objeto Usuario correspondente.
     * @throws UserNotFoundException Se o usuário não estiver cadastrado.
     */
    private Usuario verificarUsuarioExiste(String login) {
        if (!this.usuarios.containsKey(login)) {
            throw new UserNotFoundException();
        }
        return this.usuarios.get(login);
    }

    /**
     * Obtém o valor de um atributo de um usuário.
     *
     * @param login O login do usuário.
     * @param atributo O nome do atributo.
     * @return O valor do atributo.
     * @throws UserNotFoundException Se o usuário não estiver cadastrado.
     */
    public String getAtributoUsuario(String login, String atributo) {
        Usuario usuario = verificarUsuarioExiste(login);

        switch (atributo) {
            case "nome":
                return usuario.getNome();
            case "login":
                return usuario.getLogin();
            default:
                return usuario.getAtributo(atributo);
        }
    }

    /**
     * Cria um novo usuário no sistema.
     *
     * @param login O login do usuário.
     * @param senha A senha do usuário.
     * @param nome O nome do usuário.
     * @throws InvalidUserDataException Se o login ou a senha forem inválidos, ou se já existir um usuário com o mesmo login.
     */
    public void criarUsuario(String login, String senha, String nome) {
        if (login == null) {
            throw new InvalidUserDataException("Login inválido.");
        }

        if (senha == null) {
            throw new InvalidUserDataException("Senha inválida.");
        }

        if (this.usuarios.containsKey(login)) {
            throw new InvalidUserDataException("Conta com esse nome já existe.");
        }

        Usuario usuario = new Usuario(login, senha, nome);
        this.usuarios.put(login, usuario);
    }

    /**
     * Abre uma sessão para um usuário.
     *
     * @param login O login do usuário.
     * @param senha A senha do usuário.
     * @return O login do usuário.
     * @throws AuthenticationException Se o login ou a senha forem inválidos.
     */
    public String abrirSessao(String login, String senha) {
        if (!this.usuarios.containsKey(login)) {
            throw new AuthenticationException();
        }

        Usuario usuario = this.usuarios.get(login);

        if (!usuario.isPasswordValid(senha)) {
            throw new AuthenticationException();
        }

        String sessionId = String.valueOf(this.nextSessionId);
        this.sessoes.put(sessionId, login);
        this.nextSessionId++;

        return login;
    }

    /**
     * Edita o perfil de um usuário.
     *
     * @param id O ID do usuário.
     * @param atributo O nome do atributo a ser editado.
     * @param valor O novo valor do atributo.
     * @throws UserNotFoundException Se o usuário não estiver cadastrado.
     */
    public void editarPerfil(String id, String atributo, String valor) {
        Usuario usuario = verificarUsuarioExiste(id);
        usuario.setAtributo(atributo, valor);
    }

    /**
     * Verifica se dois usuários são amigos.
     *
     * @param login O login do usuário.
     * @param amigo O login do amigo.
     * @return true se os usuários são amigos, false caso contrário.
     */
    public boolean ehAmigo(String login, String amigo) {
        Usuario usuario = verificarUsuarioExiste(login);
        return usuario.getAmigos().contains(amigo);
    }

    /**
     * Adiciona um amigo para um usuário.
     *
     * @param login O login do usuário.
     * @param amigo O login do amigo a ser adicionado.
     * @throws FriendshipException Se o usuário tentar adicionar a si mesmo, ou se o convite já existir.
     * @throws UserNotFoundException Se o usuário ou o amigo não estiverem cadastrados.
     */
    public void adicionarAmigo(String login, String amigo) {
        if (login.equals(amigo)) {
            throw new FriendshipException("Usuário não pode adicionar a si mesmo como amigo.");
        }

        Usuario usuarioRecebeConvite = verificarUsuarioExiste(amigo);
        Usuario usuarioEnviaConvite = verificarUsuarioExiste(login);

        if (usuarioEnviaConvite.getConvitesAmizade().contains(amigo)) {
            // Aceitar convite pendente (ambos já enviaram convites)
            usuarioRecebeConvite.adicionarAmigo(login);
            usuarioEnviaConvite.adicionarAmigo(amigo);

            usuarioRecebeConvite.removerConviteAmizade(login);
            usuarioEnviaConvite.removerConviteAmizade(amigo);
            return;
        }

        if (usuarioRecebeConvite.getConvitesAmizade().contains(login)) {
            throw new FriendshipException("Usuário já está adicionado como amigo, esperando aceitação do convite.");
        }

        if (usuarioRecebeConvite.getAmigos().contains(login)) {
            throw new FriendshipException("Usuário já está adicionado como amigo.");
        }

        usuarioRecebeConvite.adicionarConviteAmizade(login);
    }

    /**
     * Obtém a lista de amigos de um usuário.
     *
     * @param login O login do usuário.
     * @return Uma string contendo os logins dos amigos do usuário.
     */
    public String getAmigos(String login) {
        Usuario usuario = verificarUsuarioExiste(login);
        String amigos = String.join(",", usuario.getAmigos());
        return "{" + amigos + "}";
    }

    /**
     * Envia um recado para um usuário.
     *
     * @param id O ID do usuário que envia o recado.
     * @param destinatario O ID do usuário que recebe o recado.
     * @param recado O conteúdo do recado.
     * @throws MessageException Se o usuário tentar enviar um recado para si mesmo.
     * @throws UserNotFoundException Se o usuário ou o destinatário não estiverem cadastrados.
     */
    public void enviarRecado(String id, String destinatario, String recado) {
        if (id.equals(destinatario)) {
            throw new MessageException("Usuário não pode enviar recado para si mesmo.");
        }

        Usuario enviaRecado = verificarUsuarioExiste(id);
        Usuario recebeRecado = verificarUsuarioExiste(destinatario);

        recebeRecado.adicionarRecado(recado);
    }

    /**
     * Lê um recado de um usuário.
     *
     * @param id O ID do usuário.
     * @return O conteúdo do recado.
     * @throws MessageException Se não houver recados.
     */
    public String lerRecado(String id) {
        Usuario usuario = verificarUsuarioExiste(id);

        if (usuario.getRecados().isEmpty()) {
            throw new MessageException("Não há recados.");
        }

        return usuario.getRecados().poll();
    }
}