package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.entities.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Repositório central de dados do sistema.
 * Armazena todas as entidades e gerencia o acesso a elas.
 */
public class DataRepository implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Mapa de usuários indexados por login */
    private Map<String, Usuario> usuarios;

    /** Mapa de sessões indexadas por ID de sessão */
    private Map<String, String> sessoes;

    /** Mapa de comunidades indexadas por nome */
    private Map<String, Comunidade> comunidades;

    /** Mapa de mensagens indexadas por login do destinatário */
    private Map<String, List<Comunicacao>> mensagens;

    /** Mapa que relaciona donos (login) às suas comunidades */
    private Map<String, Set<String>> donoParaComunidades;

    /** Contador para gerar IDs de sessão únicos */
    private int nextSessionId;

    /**
     * Construtor que inicializa todas as estruturas de dados.
     */
    public DataRepository() {
        this.usuarios = new HashMap<>();
        this.sessoes = new HashMap<>();
        this.comunidades = new HashMap<>();
        this.mensagens = new HashMap<>();
        this.donoParaComunidades = new HashMap<>();
        this.nextSessionId = 1;
    }

    // Métodos para usuários

    /**
     * @return Mapa de todos os usuários
     */
    public Map<String, Usuario> getUsuarios() {
        return usuarios;
    }

    /**
     * Adiciona um novo usuário ao repositório.
     *
     * @param usuario Objeto Usuario a ser adicionado
     */
    public void adicionarUsuario(Usuario usuario) {
        usuarios.put(usuario.getLogin(), usuario);
        donoParaComunidades.putIfAbsent(usuario.getLogin(), new HashSet<>());
    }

    /**
     * Obtém um usuário pelo login.
     *
     * @param login Login do usuário
     * @return Objeto Usuario correspondente ou null se não existir
     */
    public Usuario getUsuario(String login) {
        return usuarios.get(login);
    }

    /**
     * Verifica se existe um usuário com o login especificado.
     *
     * @param login Login do usuário
     * @return true se o usuário existir, false caso contrário
     */
    public boolean existeUsuario(String login) {
        return usuarios.containsKey(login);
    }

    /**
     * Remove um usuário do repositório.
     *
     * @param login Login do usuário a ser removido
     */
    public void removerUsuario(String login) {
        usuarios.remove(login);
    }

    // Métodos para sessões

    /**
     * @return Mapa de todas as sessões
     */
    public Map<String, String> getSessoes() {
        return sessoes;
    }

    /**
     * Cria uma nova sessão para um usuário.
     *
     * @param login Login do usuário
     * @return ID da sessão criada
     */
    public String criarSessao(String login) {
        String sessionId = String.valueOf(nextSessionId++);
        sessoes.put(sessionId, login);
        return sessionId;
    }

    /**
     * Obtém o login associado a uma sessão.
     *
     * @param sessionId ID da sessão
     * @return Login do usuário ou null se a sessão não existir
     */
    public String getLoginDaSessao(String sessionId) {
        return sessoes.get(sessionId);
    }

    /**
     * Verifica se existe uma sessão com o ID especificado.
     *
     * @param sessionId ID da sessão
     * @return true se a sessão existir, false caso contrário
     */
    public boolean existeSessao(String sessionId) {
        return sessoes.containsKey(sessionId);
    }

    /**
     * Remove uma sessão do repositório.
     *
     * @param sessionId ID da sessão a ser removida
     */
    public void removerSessao(String sessionId) {
        sessoes.remove(sessionId);
    }

    // Métodos para comunidades

    /**
     * @return Mapa de todas as comunidades
     */
    public Map<String, Comunidade> getComunidades() {
        return comunidades;
    }

    /**
     * Adiciona uma nova comunidade ao repositório.
     *
     * @param comunidade Objeto Comunidade a ser adicionado
     */
    public void adicionarComunidade(Comunidade comunidade) {
        comunidades.put(comunidade.getNome(), comunidade);
    }

    /**
     * Obtém uma comunidade pelo nome.
     *
     * @param nome Nome da comunidade
     * @return Objeto Comunidade correspondente ou null se não existir
     */
    public Comunidade getComunidade(String nome) {
        return comunidades.get(nome);
    }

    /**
     * Verifica se existe uma comunidade com o nome especificado.
     *
     * @param nome Nome da comunidade
     * @return true se a comunidade existir, false caso contrário
     */
    public boolean existeComunidade(String nome) {
        return comunidades.containsKey(nome);
    }

    /**
     * Remove uma comunidade do repositório.
     *
     * @param nome Nome da comunidade a ser removida
     */
    public void removerComunidade(String nome) {
        comunidades.remove(nome);
    }

    // Métodos para mensagens

    /**
     * @return Mapa de todas as mensagens
     */
    public Map<String, List<Comunicacao>> getMensagens() {
        return mensagens;
    }

    /**
     * Obtém a lista de mensagens de um usuário.
     *
     * @param login Login do usuário
     * @return Lista de mensagens ou uma lista vazia se não houver mensagens
     */
    public List<Comunicacao> getMensagensDoUsuario(String login) {
        return mensagens.getOrDefault(login, new ArrayList<>());
    }

    /**
     * Adiciona uma mensagem para um destinatário.
     *
     * @param destinatario Login do destinatário
     * @param mensagem Objeto Comunicacao a ser adicionado
     */
    public void adicionarMensagem(String destinatario, Comunicacao mensagem) {
        mensagens.putIfAbsent(destinatario, new ArrayList<>());
        mensagens.get(destinatario).add(mensagem);
    }

    // Métodos para relação dono-comunidade

    /**
     * @return Mapa que relaciona donos às suas comunidades
     */
    public Map<String, Set<String>> getDonoParaComunidades() {
        return donoParaComunidades;
    }

    /**
     * Obtém o conjunto de comunidades de um dono.
     *
     * @param login Login do dono
     * @return Conjunto de nomes de comunidades ou um conjunto vazio
     */
    public Set<String> getComunidadesDonoUsuario(String login) {
        return donoParaComunidades.getOrDefault(login, new HashSet<>());
    }

    /**
     * Adiciona uma comunidade à lista de comunidades de um dono.
     *
     * @param login Login do dono
     * @param comunidade Nome da comunidade
     */
    public void adicionarComunidadeAoDono(String login, String comunidade) {
        donoParaComunidades.putIfAbsent(login, new HashSet<>());
        donoParaComunidades.get(login).add(comunidade);
    }

    /**
     * Remove uma comunidade da lista de comunidades de um dono.
     *
     * @param login Login do dono
     * @param comunidade Nome da comunidade
     */
    public void removerComunidadeDoDono(String login, String comunidade) {
        if (donoParaComunidades.containsKey(login)) {
            donoParaComunidades.get(login).remove(comunidade);
        }
    }

    /**
     * Limpa todos os dados do repositório.
     * Usado para reiniciar o sistema.
     */
    public void zerarTudo() {
        usuarios.clear();
        sessoes.clear();
        comunidades.clear();
        mensagens.clear();
        donoParaComunidades.clear();
        nextSessionId = 1;
    }
}