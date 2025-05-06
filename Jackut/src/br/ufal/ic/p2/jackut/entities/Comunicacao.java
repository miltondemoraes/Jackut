package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;

/**
 * Classe que representa uma comunicação (mensagem) entre usuários ou para uma comunidade.
 * Pode ser um recado direto ou uma mensagem para comunidade.
 */
public class Comunicacao implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Login do usuário que enviou a mensagem */
    private final String remetente;

    /** Login do usuário ou nome da comunidade destinatária */
    private final String destinatario;

    /** Conteúdo da mensagem */
    private final String conteudo;

    /** Tipo da mensagem: "recado" ou "comunidade" */
    private final String tipo;

    /**
     * Construtor que inicializa uma nova comunicação.
     *
     * @param remetente Login do usuário que enviou a mensagem
     * @param destinatario Login do usuário ou nome da comunidade destinatária
     * @param conteudo Conteúdo da mensagem
     * @param tipo Tipo da mensagem: "recado" ou "comunidade"
     */
    public Comunicacao(String remetente, String destinatario, String conteudo, String tipo) {
        this.remetente = remetente;
        this.destinatario = destinatario;
        this.conteudo = conteudo;
        this.tipo = tipo;
    }

    /**
     * @return Login do usuário que enviou a mensagem
     */
    public String getRemetente() {
        return remetente;
    }

    /**
     * @return Conteúdo da mensagem
     */
    public String getConteudo() {
        return conteudo;
    }

    /**
     * @return Tipo da mensagem: "recado" ou "comunidade"
     */
    public String getTipo() {
        return tipo;
    }
}