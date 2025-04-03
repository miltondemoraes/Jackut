
package br.ufal.ic.p2.jackut.entities;

import br.ufal.ic.p2.jackut.exceptions.ProfileAttributeException;

import java.io.Serializable;
import java.util.*;

/**
 * A classe Usuario representa um usuário no sistema Jackut.
 * Implementa a interface Serializable para permitir a serialização dos objetos.
 */
public class Usuario implements Serializable {
    private String login;
    private String senha;
    private String nome;
    private Map<String, String> atributos;
    private Set<String> convitesAmizade;
    private Set<String> amigos;
    private Queue<String> recados;
    private static final long serialVersionUID = 1L;

    /**
     * Construtor da classe Usuario.
     *
     * @param login O login do usuário.
     * @param senha A senha do usuário.
     * @param nome O nome do usuário.
     */
    public Usuario(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.atributos = new HashMap<>();
        this.amigos = new LinkedHashSet<>();
        this.convitesAmizade = new LinkedHashSet<>();
        this.recados = new LinkedList<>();
    }

    /**
     * Obtém o login do usuário.
     *
     * @return O login do usuário.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Obtém o nome do usuário.
     *
     * @return O nome do usuário.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define um atributo do usuário.
     *
     * @param atributo O nome do atributo.
     * @param valor O valor do atributo.
     */
    public void setAtributo(String atributo, String valor) {
        this.atributos.put(atributo, valor);
    }

    /**
     * Obtém o valor de um atributo do usuário.
     *
     * @param atributo O nome do atributo.
     * @return O valor do atributo.
     * @throws ProfileAttributeException Se o atributo não estiver preenchido.
     */
    public String getAtributo(String atributo) {
        if (!this.hasAtributo(atributo)) {
            throw new ProfileAttributeException("Atributo não preenchido.");
        }

        return this.atributos.get(atributo);
    }

    /**
     * Define o login do usuário.
     *
     * @param login O novo login do usuário.
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Define a senha do usuário.
     *
     * @param senha A nova senha do usuário.
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }

    /**
     * Define o nome do usuário.
     *
     * @param nome O novo nome do usuário.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Obtém os convites de amizade do usuário.
     *
     * @return Um conjunto de IDs de convites de amizade.
     */
    public Set<String> getConvitesAmizade() {
        return convitesAmizade;
    }

    /**
     * Remove um convite de amizade.
     *
     * @param id O ID do convite de amizade a ser removido.
     */
    public void removerConviteAmizade(String id) {
        this.convitesAmizade.remove(id);
    }

    /**
     * Adiciona um convite de amizade.
     *
     * @param id O ID do convite de amizade a ser adicionado.
     */
    public void adicionarConviteAmizade(String id) {
        this.convitesAmizade.add(id);
    }

    /**
     * Obtém a lista de amigos do usuário.
     *
     * @return Um conjunto de IDs de amigos.
     */
    public Set<String> getAmigos() {
        return amigos;
    }

    /**
     * Verifica se a senha fornecida é válida.
     *
     * @param password A senha a ser verificada.
     * @return true se a senha for válida, false caso contrário.
     */
    public boolean isPasswordValid(String password) {
        return this.senha.equals(password);
    }

    /**
     * Verifica se o usuário possui um determinado atributo.
     *
     * @param atributo O nome do atributo.
     * @return true se o atributo existir, false caso contrário.
     */
    public boolean hasAtributo(String atributo) {
        return this.atributos.containsKey(atributo);
    }

    /**
     * Adiciona um amigo ao usuário.
     *
     * @param amigo O ID do amigo a ser adicionado.
     */
    public void adicionarAmigo(String amigo) {
        this.amigos.add(amigo);
    }

    /**
     * Adiciona um recado ao usuário.
     *
     * @param recado O recado a ser adicionado.
     */
    public void adicionarRecado(String recado) {
        recados.add(recado);
    }

    /**
     * Obtém a fila de recados do usuário.
     *
     * @return A fila de recados.
     */
    public Queue<String> getRecados() {
        return this.recados;
    }
}
