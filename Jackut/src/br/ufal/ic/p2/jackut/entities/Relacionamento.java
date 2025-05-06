package br.ufal.ic.p2.jackut.entities;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Classe que gerencia os relacionamentos de um usuário com outros usuários.
 * Mantém listas de amigos, fãs, paqueras, inimigos e convites de amizade.
 */
public class Relacionamento implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Mapa que armazena os diferentes tipos de relacionamentos */
    private final Map<String, Set<String>> relacionamentos;

    /** Conjunto de convites de amizade pendentes */
    private final Set<String> convitesAmizade;

    /**
     * Construtor que inicializa as estruturas de dados para os relacionamentos.
     * Cria conjuntos vazios para cada tipo de relacionamento.
     */
    public Relacionamento() {
        this.convitesAmizade = new LinkedHashSet<>();
        this.relacionamentos = new HashMap<>();
        this.relacionamentos.put("amigo", new LinkedHashSet<>());
        this.relacionamentos.put("fa", new LinkedHashSet<>());
        this.relacionamentos.put("paquera", new LinkedHashSet<>());
        this.relacionamentos.put("inimigo", new LinkedHashSet<>());
    }

    /**
     * @return Conjunto de convites de amizade pendentes
     */
    public Set<String> getConvitesAmizade() {
        return convitesAmizade;
    }

    /**
     * @return Conjunto de logins dos amigos do usuário
     */
    public Set<String> getAmigos() {
        return this.relacionamentos.get("amigo");
    }

    /**
     * @return Conjunto de logins dos ídolos do usuário (de quem é fã)
     */
    public Set<String> getIdolos() {
        return this.relacionamentos.get("fa");
    }

    /**
     * @return Conjunto de logins das paqueras do usuário
     */
    public Set<String> getPaqueras() {
        return this.relacionamentos.get("paquera");
    }

    /**
     * @return Conjunto de logins dos inimigos do usuário
     */
    public Set<String> getInimigos() {
        return this.relacionamentos.get("inimigo");
    }

    /**
     * @return Mapa completo de todos os relacionamentos
     */
    public Map<String, Set<String>> getRelacionamentos() {
        return relacionamentos;
    }
}