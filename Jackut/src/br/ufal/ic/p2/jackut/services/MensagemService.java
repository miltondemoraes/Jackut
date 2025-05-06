package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.entities.Comunicacao;
import br.ufal.ic.p2.jackut.entities.Usuario;
import br.ufal.ic.p2.jackut.exceptions.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável por gerenciar as mensagens entre usuários e comunidades.
 * Permite enviar e ler recados e mensagens de comunidade.
 */
public class MensagemService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Repositório central de dados */
    private final DataRepository repository;

    /** Serviço de usuários */
    private final UsuarioService usuarioService;

    /** Serviço de comunidades */
    private final ComunidadeService comunidadeService;

    /**
     * Construtor que inicializa o serviço com as dependências necessárias.
     *
     * @param repository Repositório central de dados
     * @param usuarioService Serviço de usuários
     * @param comunidadeService Serviço de comunidades
     */
    public MensagemService(DataRepository repository, UsuarioService usuarioService, ComunidadeService comunidadeService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
        this.comunidadeService = comunidadeService;
    }

    /**
     * Envia um recado de um usuário para outro.
     *
     * @param remetente Login do usuário remetente
     * @param destinatario Login do usuário destinatário
     * @param recado Conteúdo do recado
     * @throws MessageException Se o usuário tentar enviar recado para si mesmo
     * @throws RelacionamentoException Se o destinatário for inimigo do remetente
     * @throws UserNotFoundException Se algum dos usuários não existir
     */
    public void enviarRecado(String remetente, String destinatario, String recado)
            throws MessageException, RelacionamentoException, UserNotFoundException {
        Usuario usuarioRemetente = usuarioService.getUsuario(remetente);
        Usuario usuarioDestinatario = usuarioService.getUsuario(destinatario);

        if (remetente.equals(destinatario)) {
            throw new MessageException("Usuário não pode enviar recado para si mesmo.");
        }

        if (ehInimigo(destinatario, remetente)) {
            throw new RelacionamentoException("Função inválida: " + usuarioDestinatario.getNome() + " é seu inimigo.");
        }

        Comunicacao novaMensagem = criarRecado(remetente, destinatario, recado);
        repository.adicionarMensagem(destinatario, novaMensagem);
    }

    /**
     * Lê o próximo recado disponível para um usuário.
     *
     * @param login Login do usuário
     * @return Conteúdo do recado
     * @throws MessageException Se não houver recados
     */
    public String lerRecado(String login) throws MessageException {
        List<Comunicacao> recados = getMensagensDoTipo(login, "recado");

        if (recados.isEmpty()) {
            throw new MessageException("Não há recados.");
        }

        Comunicacao recado = recados.remove(0);
        repository.getMensagensDoUsuario(login).remove(recado);

        return recado.getConteudo();
    }

    /**
     * Envia uma mensagem para todos os membros de uma comunidade.
     *
     * @param login Login do usuário remetente
     * @param comunidade Nome da comunidade
     * @param mensagem Conteúdo da mensagem
     * @throws CommunityException Se a comunidade não existir
     */
    public void enviarMensagemComunidade(String login, String comunidade, String mensagem)
            throws CommunityException {
        if (!repository.existeComunidade(comunidade)) {
            throw new CommunityException("Comunidade não existe.");
        }

        Comunicacao novaMensagem = criarMensagemComunidade(login, comunidade, mensagem);

        List<String> membros = repository.getComunidade(comunidade).getMembros();

        for (String membro : membros) {
            repository.adicionarMensagem(membro, novaMensagem);
        }
    }

    /**
     * Lê a próxima mensagem de comunidade disponível para um usuário.
     *
     * @param login Login do usuário
     * @return Conteúdo da mensagem formatada
     * @throws MessageException Se não houver mensagens
     */
    public String lerMensagemComunidade(String login) throws MessageException {
        List<Comunicacao> mensagens = getMensagensDoTipo(login, "comunidade");

        if (mensagens.isEmpty()) {
            throw new MessageException("Não há mensagens.");
        }

        Comunicacao mensagem = mensagens.remove(0);
        repository.getMensagensDoUsuario(login).remove(mensagem);

        return formatarMensagem(mensagem);
    }

    /**
     * Adiciona um recado automático do sistema Jackut para um usuário.
     * Usado para notificar sobre paqueras mútuas.
     *
     * @param login Login do usuário destinatário
     * @param recado Conteúdo base do recado
     */
    public void adicionarRecadoJackut(String login, String recado) {
        Comunicacao mensagem = criarRecado("jackut", login, recado + " é seu paquera - Recado do Jackut.");
        repository.adicionarMensagem(login, mensagem);
    }

    /**
     * Obtém todas as mensagens de um determinado tipo para um usuário.
     *
     * @param login Login do usuário
     * @param tipo Tipo da mensagem ("recado" ou "comunidade")
     * @return Lista de mensagens do tipo especificado
     */
    private List<Comunicacao> getMensagensDoTipo(String login, String tipo) {
        List<Comunicacao> mensagensDoTipo = new ArrayList<>();
        List<Comunicacao> todasMensagens = repository.getMensagensDoUsuario(login);

        for (Comunicacao mensagem : todasMensagens) {
            if (tipo.equals(mensagem.getTipo())) {
                mensagensDoTipo.add(mensagem);
            }
        }

        return mensagensDoTipo;
    }

    /**
     * Remove todas as mensagens enviadas ou recebidas por um usuário.
     *
     * @param login Login do usuário
     */
    public void removerMensagensDoUsuario(String login) {
        // Remove mensagens enviadas pelo usuário
        for (List<Comunicacao> listaMensagens : repository.getMensagens().values()) {
            listaMensagens.removeIf(mensagem -> mensagem.getRemetente().equals(login));
        }

        // Remove mensagens recebidas pelo usuário
        repository.getMensagens().remove(login);
    }

    /**
     * Remove todas as mensagens do sistema.
     */
    public void zerarMensagens() {
        repository.getMensagens().clear();
    }

    /**
     * Cria um novo objeto de comunicação do tipo recado.
     *
     * @param remetente Login do usuário remetente
     * @param destinatario Login do usuário destinatário
     * @param conteudo Conteúdo do recado
     * @return Objeto Comunicacao criado
     */
    private Comunicacao criarRecado(String remetente, String destinatario, String conteudo) {
        return new Comunicacao(remetente, destinatario, conteudo, "recado");
    }

    /**
     * Cria um novo objeto de comunicação do tipo mensagem de comunidade.
     *
     * @param remetente Login do usuário remetente
     * @param comunidade Nome da comunidade
     * @param conteudo Conteúdo da mensagem
     * @return Objeto Comunicacao criado
     */
    private Comunicacao criarMensagemComunidade(String remetente, String comunidade, String conteudo) {
        return new Comunicacao(remetente, comunidade, conteudo, "comunidade");
    }

    /**
     * Formata uma mensagem para exibição.
     *
     * @param mensagem Objeto Comunicacao a ser formatado
     * @return String formatada da mensagem
     */
    private String formatarMensagem(Comunicacao mensagem) {
        if (mensagem.getTipo().equals("recado")) {
            return mensagem.getRemetente() + ": " + mensagem.getConteudo();
        }
        return mensagem.getConteudo();
    }

    /**
     * Verifica se um usuário é inimigo de outro.
     *
     * @param login Login do primeiro usuário
     * @param outroUsuario Login do segundo usuário
     * @return true se o primeiro usuário tiver o segundo como inimigo
     * @throws UserNotFoundException Se algum dos usuários não existir
     */
    private boolean ehInimigo(String login, String outroUsuario) throws UserNotFoundException {
        Usuario usuario = usuarioService.getUsuario(login);
        return usuario.getRelacionamentos().getInimigos().contains(outroUsuario);
    }
}