package br.ufal.ic.p2.jackut.services;

import br.ufal.ic.p2.jackut.entities.Comunidade;
import br.ufal.ic.p2.jackut.entities.Usuario;
import br.ufal.ic.p2.jackut.exceptions.CommunityException;
import br.ufal.ic.p2.jackut.exceptions.UserNotFoundException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * Serviço responsável por gerenciar as operações relacionadas às comunidades.
 * Permite criar, consultar e gerenciar membros de comunidades.
 */
public class ComunidadeService implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Repositório central de dados */
    private final DataRepository repository;

    /** Serviço de usuários */
    private final UsuarioService usuarioService;

    /**
     * Construtor que inicializa o serviço com as dependências necessárias.
     *
     * @param repository Repositório central de dados
     * @param usuarioService Serviço de usuários
     */
    public ComunidadeService(DataRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }

    /**
     * Cria uma nova comunidade.
     *
     * @param login Login do usuário que está criando a comunidade
     * @param nome Nome único da comunidade
     * @param descricao Descrição da comunidade
     * @throws CommunityException Se já existir uma comunidade com o mesmo nome
     */
    public void criarComunidade(String login, String nome, String descricao) {
        if (repository.existeComunidade(nome)) {
            throw new CommunityException("Comunidade com esse nome já existe.");
        }

        Usuario usuario = usuarioService.getUsuario(login);

        // Passa o login do dono ao criar a comunidade
        Comunidade comunidade = new Comunidade(login, login, nome, descricao);
        adicionarMembroComunidade(comunidade, login);

        repository.adicionarComunidade(comunidade);
        repository.adicionarComunidadeAoDono(login, nome);

        adicionarComunidadeAoUsuario(usuario, nome);
    }

    /**
     * Obtém uma comunidade pelo nome.
     *
     * @param nome Nome da comunidade
     * @return A comunidade encontrada
     * @throws CommunityException Se a comunidade não existir
     */
    public Comunidade getComunidade(String nome) {
        Comunidade comunidade = repository.getComunidade(nome);

        if (comunidade == null) {
            throw new CommunityException("Comunidade não existe.");
        }

        return comunidade;
    }

    /**
     * Obtém a descrição de uma comunidade.
     *
     * @param nome Nome da comunidade
     * @return Descrição da comunidade
     * @throws CommunityException Se a comunidade não existir
     */
    public String getDescricaoComunidade(String nome) {
        return getComunidade(nome).getDescricao();
    }

    /**
     * Obtém o login do dono de uma comunidade.
     *
     * @param nome Nome da comunidade
     * @return Login do dono da comunidade
     * @throws CommunityException Se a comunidade não existir
     */
    public String getDonoComunidade(String nome) {
        Comunidade comunidade = getComunidade(nome);
        return comunidade.getDonoComunidade();
    }

    /**
     * Obtém a lista de membros de uma comunidade formatada como string.
     *
     * @param nome Nome da comunidade
     * @return String formatada com a lista de membros: "{membro1,membro2,...}"
     * @throws CommunityException Se a comunidade não existir
     */
    public String getMembrosComunidade(String nome) {
        Comunidade comunidade = getComunidade(nome);
        return "{" + String.join(",", comunidade.getMembros()) + "}";
    }

    /**
     * Obtém a lista de comunidades de um usuário formatada como string.
     *
     * @param login Login do usuário
     * @return String formatada com a lista de comunidades: "{comunidade1,comunidade2,...}"
     * @throws UserNotFoundException Se o usuário não existir
     */
    public String getComunidadesDoUsuario(String login) {
        if (login == null || login.isEmpty() || !usuarioService.existeUsuario(login)) {
            throw new UserNotFoundException("Usuário não cadastrado.");
        }

        Usuario usuario = usuarioService.getUsuario(login);
        return "{" + String.join(",", usuario.getComunidadesCadastradas()) + "}";
    }

    /**
     * Adiciona um usuário a uma comunidade.
     *
     * @param login Login do usuário a ser adicionado
     * @param nomeComunidade Nome da comunidade
     * @throws CommunityException Se a comunidade não existir ou se o usuário já for membro
     * @throws UserNotFoundException Se o usuário não existir
     */
    public void adicionarUsuarioAComunidade(String login, String nomeComunidade) {
        Comunidade comunidade = getComunidade(nomeComunidade);
        Usuario usuario = usuarioService.getUsuario(login);

        if (isMembro(comunidade, login)) {
            throw new CommunityException("Usuario já faz parte dessa comunidade.");
        }

        adicionarMembroComunidade(comunidade, login);
        adicionarComunidadeAoUsuario(usuario, nomeComunidade);
    }

    /**
     * Remove um usuário de todas as comunidades que ele é dono.
     * Também remove essas comunidades de todos os membros.
     *
     * @param login Login do usuário
     */
    public void removerUsuarioDeComunidades(String login) {
        // Remove comunidades onde o usuário é dono
        Set<String> comunidadesDoDono = repository.getComunidadesDonoUsuario(login);

        for (String nomeComunidade : comunidadesDoDono) {
            Comunidade comunidade = repository.getComunidade(nomeComunidade);

            if (comunidade != null) {
                // Remove a comunidade de todos os membros
                for (String membro : comunidade.getMembros()) {
                    Usuario usuarioMembro = repository.getUsuario(membro);
                    if (usuarioMembro != null) {
                        removerComunidadeCadastrada(usuarioMembro, nomeComunidade);
                    }
                }

                repository.removerComunidade(nomeComunidade);
            }
        }

        repository.getDonoParaComunidades().remove(login);
    }

    /**
     * Remove todas as comunidades do sistema.
     */
    public void zerarComunidades() {
        repository.getComunidades().clear();
        repository.getDonoParaComunidades().clear();
    }

    /**
     * Verifica se um usuário é membro de uma comunidade.
     *
     * @param comunidade A comunidade a verificar
     * @param login Login do usuário
     * @return true se o usuário for membro, false caso contrário
     */
    private boolean isMembro(Comunidade comunidade, String login) {
        return comunidade.getMembros().contains(login);
    }

    /**
     * Adiciona um usuário como membro de uma comunidade.
     *
     * @param comunidade A comunidade
     * @param login Login do usuário
     */
    private void adicionarMembroComunidade(Comunidade comunidade, String login) {
        if (!comunidade.getMembros().contains(login)) {
            comunidade.getMembros().add(login);
        }
    }

    /**
     * Adiciona uma comunidade à lista de comunidades de um usuário.
     *
     * @param usuario O usuário
     * @param nomeComunidade Nome da comunidade
     */
    private void adicionarComunidadeAoUsuario(Usuario usuario, String nomeComunidade) {
        if (!usuario.getComunidadesCadastradas().contains(nomeComunidade)) {
            usuario.getComunidadesCadastradas().add(nomeComunidade);
        }
    }

    /**
     * Remove uma comunidade da lista de comunidades de um usuário.
     *
     * @param usuario O usuário
     * @param nome Nome da comunidade
     */
    private void removerComunidadeCadastrada(Usuario usuario, String nome) {
        usuario.getComunidadesCadastradas().removeIf(com -> com.equals(nome));
    }
}