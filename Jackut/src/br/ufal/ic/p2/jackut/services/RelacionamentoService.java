package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.entities.Relacionamento;
import br.ufal.ic.p2.jackut.entities.Usuario;
import br.ufal.ic.p2.jackut.exceptions.FriendshipException;
import br.ufal.ic.p2.jackut.exceptions.RelacionamentoException;
import br.ufal.ic.p2.jackut.exceptions.UserNotFoundException;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Serviço responsável por gerenciar os relacionamentos entre usuários.
 * Permite adicionar e consultar amigos, ídolos, paqueras e inimigos.
 */
public class RelacionamentoService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Repositório central de dados */
    private final DataRepository repository;

    /** Serviço de usuários */
    private final UsuarioService usuarioService;

    /** Serviço de mensagens */
    private final MensagemService mensagemService;

    /**
     * Construtor que inicializa o serviço com as dependências necessárias.
     *
     * @param repository Repositório central de dados
     * @param usuarioService Serviço de usuários
     * @param mensagemService Serviço de mensagens
     */
    public RelacionamentoService(DataRepository repository, UsuarioService usuarioService, MensagemService mensagemService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
        this.mensagemService = mensagemService;
    }

    /**
     * Verifica se um usuário é amigo de outro.
     *
     * @param login Login do primeiro usuário
     * @param amigo Login do possível amigo
     * @return true se forem amigos, false caso contrário
     */
    public boolean ehAmigo(String login, String amigo) {
        if (!usuarioService.existeUsuario(login) || !usuarioService.existeUsuario(amigo)) {
            return false;
        }

        Usuario usuario = usuarioService.getUsuario(login);
        return usuario.getRelacionamentos().getAmigos().contains(amigo);
    }

    /**
     * Adiciona um usuário como amigo de outro.
     * Se ambos enviaram convites, a amizade é estabelecida automaticamente.
     *
     * @param login Login do usuário que está adicionando
     * @param amigo Login do usuário a ser adicionado como amigo
     * @throws UserNotFoundException Se algum dos usuários não existir
     * @throws FriendshipException Se o usuário tentar adicionar a si mesmo ou se já forem amigos
     * @throws RelacionamentoException Se o usuário a ser adicionado for inimigo
     */
    public void adicionarAmigo(String login, String amigo) {
        if (login == null || login.isEmpty()) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        if (login.equals(amigo)) {
            throw new FriendshipException("Usuário não pode adicionar a si mesmo como amigo.");
        }

        if (!usuarioService.existeUsuario(login) || !usuarioService.existeUsuario(amigo)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        Usuario usuarioEnvia = usuarioService.getUsuario(login);
        Usuario usuarioRecebe = usuarioService.getUsuario(amigo);

        if (ehInimigo(amigo, login)) {
            throw new RelacionamentoException("Função inválida: " + usuarioRecebe.getNome() + " é seu inimigo.");
        }

        Set<String> convitesAmizadeEnvia = usuarioEnvia.getRelacionamentos().getConvitesAmizade();
        Set<String> convitesAmizadeRecebe = usuarioRecebe.getRelacionamentos().getConvitesAmizade();
        Set<String> amigosEnvia = usuarioEnvia.getRelacionamentos().getAmigos();
        Set<String> amigosRecebe = usuarioRecebe.getRelacionamentos().getAmigos();

        if (convitesAmizadeEnvia.contains(amigo)) {
            // Aceitar convite pendente (ambos já enviaram convites)
            amigosRecebe.add(login);
            amigosEnvia.add(amigo);
            convitesAmizadeRecebe.remove(login);
            convitesAmizadeEnvia.remove(amigo);
            return;
        }

        if (convitesAmizadeRecebe.contains(login)) {
            throw new FriendshipException("Usuário já está adicionado como amigo, esperando aceitação do convite.");
        }

        if (amigosRecebe.contains(login)) {
            throw new FriendshipException("Usuário já está adicionado como amigo.");
        }

        convitesAmizadeRecebe.add(login);
    }

    /**
     * Obtém a lista de amigos de um usuário formatada como string.
     *
     * @param login Login do usuário
     * @return String formatada com a lista de amigos: "{amigo1,amigo2,...}"
     * @throws UserNotFoundException Se o usuário não existir
     */
    public String getAmigos(String login) {
        if (!usuarioService.existeUsuario(login)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        Usuario usuario = usuarioService.getUsuario(login);
        Set<String> amigos = usuario.getRelacionamentos().getAmigos();
        return "{" + String.join(",", amigos) + "}";
    }

    /**
     * Verifica se um usuário é fã de outro.
     *
     * @param login Login do possível fã
     * @param idolo Login do possível ídolo
     * @return true se o primeiro for fã do segundo, false caso contrário
     */
    public boolean ehFa(String login, String idolo) {
        Usuario usuario = usuarioService.getUsuario(login);
        return usuario.getRelacionamentos().getIdolos().contains(idolo);
    }

    /**
     * Adiciona um usuário como ídolo de outro.
     *
     * @param login Login do usuário que está adicionando (fã)
     * @param idolo Login do usuário a ser adicionado como ídolo
     * @throws RelacionamentoException Se o usuário tentar ser fã de si mesmo, se já for fã ou se o ídolo for inimigo
     */
    public void adicionarIdolo(String login, String idolo) {
        Usuario usuario = usuarioService.getUsuario(login);
        Usuario idoloObj = usuarioService.getUsuario(idolo);

        if (login.equals(idolo)) {
            throw new RelacionamentoException("Usuário não pode ser fã de si mesmo.");
        }

        if (usuario.getRelacionamentos().getIdolos().contains(idolo)) {
            throw new RelacionamentoException("Usuário já está adicionado como ídolo.");
        }

        if (ehInimigo(idolo, login)) {
            throw new RelacionamentoException("Função inválida: " + idoloObj.getNome() + " é seu inimigo.");
        }

        usuario.getRelacionamentos().getIdolos().add(idolo);
    }

    /**
     * Obtém a lista de fãs de um usuário formatada como string.
     *
     * @param login Login do usuário
     * @return String formatada com a lista de fãs: "{fa1,fa2,...}"
     */
    public String getFas(String login) {
        List<String> fasList = new ArrayList<>();

        for (Usuario usuario : repository.getUsuarios().values()) {
            if (!usuario.getLogin().equals(login) && usuario.getRelacionamentos().getIdolos().contains(login)) {
                fasList.add(usuario.getLogin());
            }
        }

        return "{" + String.join(",", fasList) + "}";
    }

    /**
     * Verifica se um usuário tem outro como paquera.
     *
     * @param login Login do primeiro usuário
     * @param paquera Login do possível paquera
     * @return true se o segundo for paquera do primeiro, false caso contrário
     */
    public boolean ehPaquera(String login, String paquera) {
        Usuario usuario = usuarioService.getUsuario(login);
        return usuario.getRelacionamentos().getPaqueras().contains(paquera);
    }

    /**
     * Adiciona um usuário como paquera de outro.
     * Se ambos se adicionarem como paquera, o sistema envia recados automáticos.
     *
     * @param login Login do usuário que está adicionando
     * @param paquera Login do usuário a ser adicionado como paquera
     * @throws RelacionamentoException Se o usuário tentar adicionar a si mesmo, se já for paquera ou se for inimigo
     */
    public void adicionarPaquera(String login, String paquera) {
        Usuario usuario = usuarioService.getUsuario(login);
        Usuario paqueraObj = usuarioService.getUsuario(paquera);

        if (usuario.getRelacionamentos().getPaqueras().contains(paquera)) {
            throw new RelacionamentoException("Usuário já está adicionado como paquera.");
        }

        if (login.equals(paquera)) {
            throw new RelacionamentoException("Usuário não pode ser paquera de si mesmo.");
        }

        if (ehInimigo(paquera, login)) {
            throw new RelacionamentoException("Função inválida: " + paqueraObj.getNome() + " é seu inimigo.");
        }

        usuario.getRelacionamentos().getPaqueras().add(paquera);

        if (usuario.getRelacionamentos().getPaqueras().contains(paquera) &&
                paqueraObj.getRelacionamentos().getPaqueras().contains(login)) {
            mensagemService.adicionarRecadoJackut(login, paqueraObj.getNome());
            mensagemService.adicionarRecadoJackut(paqueraObj.getLogin(), usuario.getNome());
        }
    }

    /**
     * Obtém a lista de paqueras de um usuário formatada como string.
     *
     * @param login Login do usuário
     * @return String formatada com a lista de paqueras: "{paquera1,paquera2,...}"
     */
    public String getPaqueras(String login) {
        Usuario usuario = usuarioService.getUsuario(login);
        Set<String> paqueras = usuario.getRelacionamentos().getPaqueras();
        return "{" + String.join(",", paqueras) + "}";
    }

    /**
     * Adiciona um usuário como inimigo de outro.
     *
     * @param login Login do usuário que está adicionando
     * @param inimigo Login do usuário a ser adicionado como inimigo
     * @throws UserNotFoundException Se o usuário a ser adicionado não existir
     * @throws RelacionamentoException Se o usuário tentar adicionar a si mesmo ou se já for inimigo
     */
    public void adicionarInimigo(String login, String inimigo) {
        if (!usuarioService.existeUsuario(inimigo)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        if (login.equals(inimigo)) {
            throw new RelacionamentoException("Usuário não pode ser inimigo de si mesmo.");
        }

        Usuario usuario = usuarioService.getUsuario(login);
        Set<String> inimigos = usuario.getRelacionamentos().getInimigos();

        if (inimigos.contains(inimigo)) {
            throw new RelacionamentoException("Usuário já está adicionado como inimigo.");
        }

        inimigos.add(inimigo);
    }

    /**
     * Verifica se um usuário tem outro como inimigo.
     *
     * @param login Login do primeiro usuário
     * @param inimigo Login do possível inimigo
     * @return true se o segundo for inimigo do primeiro, false caso contrário
     */
    public boolean ehInimigo(String login, String inimigo) {
        Usuario usuario = usuarioService.getUsuario(login);
        return usuario.getRelacionamentos().getInimigos().contains(inimigo);
    }

    /**
     * Adiciona um convite de amizade para um usuário.
     *
     * @param login Login do usuário que receberá o convite
     * @param id Login do usuário que enviou o convite
     */
    public void adicionarConviteAmizade(String login, String id) {
        Usuario usuario = usuarioService.getUsuario(login);
        usuario.getRelacionamentos().getConvitesAmizade().add(id);
    }

    /**
     * Remove um convite de amizade de um usuário.
     *
     * @param login Login do usuário que tem o convite
     * @param id Login do usuário que enviou o convite
     */
    public void removerConviteAmizade(String login, String id) {
        Usuario usuario = usuarioService.getUsuario(login);
        usuario.getRelacionamentos().getConvitesAmizade().remove(id);
    }
}