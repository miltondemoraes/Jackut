package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Classe que representa uma comunidade no sistema Jackut.
 * Uma comunidade possui um dono, membros e uma descrição.
 */
public class Comunidade implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** ID da sessão que criou a comunidade */
    private final String sessionID;

    /** Login do usuário dono da comunidade */
    private final String donoComunidade;

    /** Nome único da comunidade */
    private final String nome;

    /** Descrição da comunidade */
    private final String descricao;

    /** Lista de logins dos membros da comunidade */
    private List<String> membros;

    /**
     * Construtor que inicializa uma nova comunidade.
     *
     * @param sessionID ID da sessão que criou a comunidade
     * @param donoComunidade Login do usuário dono da comunidade
     * @param nome Nome único da comunidade
     * @param descricao Descrição da comunidade
     */
    public Comunidade(String sessionID, String donoComunidade, String nome, String descricao) {
        this.sessionID = sessionID;
        this.donoComunidade = donoComunidade;
        this.nome = nome;
        this.descricao = descricao;
        this.membros = new ArrayList<>();
    }

    /**
     * @return Nome da comunidade
     */
    public String getNome() {
        return nome;
    }

    /**
     * @return Descrição da comunidade
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @return Login do usuário dono da comunidade
     */
    public String getDonoComunidade() {
        return donoComunidade;
    }

    /**
     * @return Lista de logins dos membros da comunidade
     */
    public List<String> getMembros() {
        return membros;
    }
}