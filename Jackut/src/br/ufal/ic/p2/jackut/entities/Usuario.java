package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Classe que representa um usuário no sistema Jackut.
 * Armazena informações básicas do usuário, seus atributos personalizados,
 * comunidades das quais participa e seus relacionamentos com outros usuários.
 */
public class Usuario implements Serializable {
    @Serial
    private static long serialVersionUID = 1L;

    /** Login único do usuário, usado como identificador */
    private String login;

    /** Senha do usuário para autenticação */
    private String senha;

    /** Nome completo do usuário */
    private String nome;

    /** Mapa de atributos personalizados do usuário (chave-valor) */
    private Map<String, String> atributos;

    /** Lista de comunidades das quais o usuário é membro */
    private List<String> comunidadesCadastradas;

    /** Objeto que gerencia os relacionamentos do usuário com outros usuários */
    private Relacionamento relacionamentos;

    /**
     * Construtor que inicializa um novo usuário com seus dados básicos.
     *
     * @param login Login único do usuário
     * @param senha Senha para autenticação
     * @param nome Nome completo do usuário
     */
    public Usuario(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.atributos = new HashMap<>();
        this.comunidadesCadastradas = new ArrayList<>();
        this.relacionamentos = new Relacionamento();
    }

    /**
     * @return O login do usuário
     */
    public String getLogin() {
        return login;
    }

    /**
     * @return A senha do usuário
     */
    public String getSenha() {
        return senha;
    }

    /**
     * @return O nome completo do usuário
     */
    public String getNome() {
        return nome;
    }

    /**
     * @return Mapa de atributos personalizados do usuário
     */
    public Map<String, String> getAtributos() {
        return atributos;
    }

    /**
     * @return Lista de comunidades das quais o usuário é membro
     */
    public List<String> getComunidadesCadastradas() {
        return comunidadesCadastradas;
    }

    /**
     * @return Objeto que gerencia os relacionamentos do usuário
     */
    public Relacionamento getRelacionamentos() {
        return relacionamentos;
    }
}