package br.ufal.ic.p2.jackut;

import br.ufal.ic.p2.jackut.services.*;
import br.ufal.ic.p2.jackut.exceptions.*;

import java.io.*;

/**
 * Facade para o sistema Jackut, fornecendo uma interface simplificada.
 * Implementa o padrão de projeto Facade, ocultando a complexidade do sistema
 * e fornecendo um ponto único de acesso para todas as funcionalidades.
 * Também gerencia a persistência do sistema através de serialização.
 */
public class Facade implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Nome do arquivo para persistência do sistema */
    private static final String SISTEMA_FILE = "sistema.dat";

    /** Serviços que compõem o sistema */
    private final UsuarioService usuarioService;
    private final SessaoService sessaoService;
    private final ComunidadeService comunidadeService;
    private final MensagemService mensagemService;
    private final RelacionamentoService relacionamentoService;

    /**
     * Construtor da classe Facade.
     * Carrega o estado anterior do sistema, se existir, ou cria um novo.
     */
    public Facade() {
        ServiceLocator serviceLocator = carregarOuCriarServiceLocator();
        this.usuarioService = serviceLocator.getUsuarioService();
        this.sessaoService = serviceLocator.getSessaoService();
        this.comunidadeService = serviceLocator.getComunidadeService();
        this.mensagemService = serviceLocator.getMensagemService();
        this.relacionamentoService = serviceLocator.getRelacionamentoService();
    }

    /**
     * Carrega o ServiceLocator de um arquivo serializado ou cria um novo.
     *
     * @return ServiceLocator carregado ou recém-criado
     */
    private ServiceLocator carregarOuCriarServiceLocator() {
        try {
            File file = new File(SISTEMA_FILE);
            if (file.exists()) {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                ServiceLocator serviceLocator = (ServiceLocator) in.readObject();
                in.close();
                return serviceLocator;
            }
        } catch (Exception e) {
            // Se houver erro ao ler o arquivo, continua com um sistema novo
        }
        return new ServiceLocator();
    }

    /**
     * Salva o estado atual do sistema em um arquivo serializado.
     *
     * @throws SystemSaveException Se ocorrer um erro ao salvar o sistema
     */
    public void encerrarSistema() {
        try {
            FileOutputStream fileOut = new FileOutputStream(SISTEMA_FILE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            ServiceLocator serviceLocator = new ServiceLocator(
                    usuarioService, sessaoService, comunidadeService,
                    mensagemService, relacionamentoService
            );
            out.writeObject(serviceLocator);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            throw new SystemSaveException("Erro ao salvar o sistema");
        }
    }

    /**
     * Reseta o sistema, removendo todos os dados.
     */
    public void zerarSistema() {
        usuarioService.zerarUsuarios(); // relacionamentos são zerados com o usuario
        sessaoService.zerarSessoes();
        comunidadeService.zerarComunidades();
        mensagemService.zerarMensagens();
    }

    /**
     * Obtém o valor de um atributo de um usuário.
     *
     * @param login Login do usuário
     * @param atributo Nome do atributo
     * @return Valor do atributo
     * @throws UserNotFoundException Se o usuário não existir
     * @throws ProfileAttributeException Se o atributo não estiver preenchido
     */
    public String getAtributoUsuario(String login, String atributo) {
        return usuarioService.getAtributoUsuario(login, atributo);
    }

    /**
     * Cria um novo usuário no sistema.
     *
     * @param login Login único do usuário
     * @param senha Senha para autenticação
     * @param nome Nome completo do usuário
     * @throws InvalidUserDataException Se o login já existir ou se os dados forem inválidos
     */
    public void criarUsuario(String login, String senha, String nome) {
        usuarioService.criarUsuario(login, senha, nome);
    }

    /**
     * Abre uma sessão para um usuário após validar suas credenciais.
     *
     * @param login Login do usuário
     * @param senha Senha do usuário
     * @return ID da sessão criada
     * @throws AuthenticationException Se as credenciais forem inválidas
     */
    public String abrirSessao(String login, String senha) {
        return sessaoService.abrirSessao(login, senha);
    }

    /**
     * Encerra uma sessão específica.
     *
     * @param sessionId ID da sessão a ser encerrada
     * @return true se a sessão foi encerrada, false se não existia
     */
    public boolean encerrarSessao(String sessionId) {
        return sessaoService.encerrarSessao(sessionId);
    }

    /**
     * Verifica se uma sessão existe.
     *
     * @param sessionId ID da sessão
     * @return true se a sessão existir, false caso contrário
     */
    public boolean existeSessao(String sessionId) {
        return sessaoService.existeSessao(sessionId);
    }

    /**
     * Obtém o login do usuário associado a uma sessão.
     *
     * @param sessionId ID da sessão
     * @return Login do usuário ou null se a sessão não existir
     */
    public String getLoginDaSessao(String sessionId) {
        return sessaoService.getLoginDaSessao(sessionId);
    }

    /**
     * Edita um atributo personalizado do perfil de um usuário.
     *
     * @param sessionId ID da sessão ou login do usuário
     * @param atributo Nome do atributo
     * @param valor Novo valor do atributo
     * @throws SessionNotFoundException Se a sessão não existir
     * @throws UserNotFoundException Se o usuário não existir
     */
    public void editarPerfil(String sessionId, String atributo, String valor) {
        String login = sessaoService.validarEObterLogin(sessionId);
        usuarioService.editarPerfil(login, atributo, valor);
    }

    /**
     * Verifica se dois usuários são amigos.
     *
     * @param sessionId ID da sessão ou login do primeiro usuário
     * @param amigo Login do possível amigo
     * @return true se forem amigos, false caso contrário
     */
    public boolean ehAmigo(String sessionId, String amigo) {
        try {
            String login = sessaoService.validarEObterLogin(sessionId);
            return relacionamentoService.ehAmigo(login, amigo);
        } catch (SessionNotFoundException | UserNotFoundException e) {
            // Para manter compatibilidade com o comportamento original
            return false;
        }
    }

    /**
     * Adiciona um usuário como amigo de outro.
     *
     * @param sessionId ID da sessão ou login do usuário que está adicionando
     * @param amigo Login do usuário a ser adicionado como amigo
     * @throws SessionNotFoundException Se a sessão não existir
     * @throws UserNotFoundException Se algum dos usuários não existir
     * @throws FriendshipException Se o usuário tentar adicionar a si mesmo ou se já forem amigos
     * @throws RelacionamentoException Se o usuário a ser adicionado for inimigo
     */
    public void adicionarAmigo(String sessionId, String amigo) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarAmigo(login, amigo);
    }

    /**
     * Obtém a lista de amigos de um usuário formatada como string.
     *
     * @param sessionId ID da sessão ou login do usuário
     * @return String formatada com a lista de amigos: "{amigo1,amigo2,...}"
     * @throws SessionNotFoundException Se a sessão não existir
     * @throws UserNotFoundException Se o usuário não existir
     */
    public String getAmigos(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return relacionamentoService.getAmigos(login);
    }

    /**
     * Envia um recado de um usuário para outro.
     *
     * @param sessionId ID da sessão ou login do usuário remetente
     * @param destinatario Login do usuário destinatário
     * @param recado Conteúdo do recado
     * @throws SessionNotFoundException Se a sessão não existir
     * @throws UserNotFoundException Se algum dos usuários não existir
     * @throws MessageException Se o usuário tentar enviar recado para si mesmo
     * @throws RelacionamentoException Se o destinatário for inimigo do remetente
     */
    public void enviarRecado(String sessionId, String destinatario, String recado) {
        String remetente = sessaoService.validarEObterLogin(sessionId);
        mensagemService.enviarRecado(remetente, destinatario, recado);
    }

    /**
     * Lê o próximo recado disponível para um usuário.
     *
     * @param sessionId ID da sessão ou login do usuário
     * @return Conteúdo do recado
     * @throws SessionNotFoundException Se a sessão não existir
     * @throws UserNotFoundException Se o usuário não existir
     * @throws MessageException Se não houver recados
     */
    public String lerRecado(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return mensagemService.lerRecado(login);
    }

    /**
     * Cria uma nova comunidade.
     *
     * @param sessionId ID da sessão ou login do usuário que está criando
     * @param nome Nome único da comunidade
     * @param descricao Descrição da comunidade
     * @throws SessionNotFoundException Se a sessão não existir
     * @throws UserNotFoundException Se o usuário não existir
     * @throws CommunityException Se já existir uma comunidade com o mesmo nome
     */
    public void criarComunidade(String sessionId, String nome, String descricao) {
        String login = sessaoService.validarEObterLogin(sessionId);
        comunidadeService.criarComunidade(login, nome, descricao);
    }

    /**
     * Obtém a descrição de uma comunidade.
     *
     * @param nome Nome da comunidade
     * @return Descrição da comunidade
     * @throws CommunityException Se a comunidade não existir
     */
    public String getDescricaoComunidade(String nome) {
        return comunidadeService.getDescricaoComunidade(nome);
    }

    /**
     * Obtém o login do dono de uma comunidade.
     *
     * @param nome Nome da comunidade
     * @return Login do dono da comunidade
     * @throws CommunityException Se a comunidade não existir
     */
    public String getDonoComunidade(String nome) {
        return comunidadeService.getDonoComunidade(nome);
    }

    /**
     * Obtém a lista de membros de uma comunidade formatada como string.
     *
     * @param nome Nome da comunidade
     * @return String formatada com a lista de membros: "{membro1,membro2,...}"
     * @throws CommunityException Se a comunidade não existir
     */
    public String getMembrosComunidade(String nome) {
        return comunidadeService.getMembrosComunidade(nome);
    }

    /**
     * Obtém a lista de comunidades de um usuário formatada como string.
     *
     * @param login Login do usuário
     * @return String formatada com a lista de comunidades: "{comunidade1,comunidade2,...}"
     * @throws UserNotFoundException Se o usuário não existir
     */
    public String getComunidades(String login) {
        return comunidadeService.getComunidadesDoUsuario(login);
    }

    /**
     * Adiciona um usuário a uma comunidade.
     *
     * @param sessionId ID da sessão ou login do usuário a ser adicionado
     * @param nome Nome da comunidade
     * @throws SessionNotFoundException Se a sessão não existir
     * @throws UserNotFoundException Se o usuário não existir
     * @throws CommunityException Se a comunidade não existir ou se o usuário já for membro
     */
    public void adicionarComunidade(String sessionId, String nome) {
        String login = sessaoService.validarEObterLogin(sessionId);
        comunidadeService.adicionarUsuarioAComunidade(login, nome);
    }

    /**
     * Lê a próxima mensagem de comunidade disponível para um usuário.
     *
     * @param sessionId ID da sessão ou login do usuário
     * @return Conteúdo da mensagem formatada
     * @throws SessionNotFoundException Se a sessão não existir
     * @throws UserNotFoundException Se o usuário não existir
     * @throws MessageException Se não houver mensagens
     */
    public String lerMensagem(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return mensagemService.lerMensagemComunidade(login);
    }

    /**
     * Envia uma mensagem para todos os membros de uma comunidade.
     *
     * @param sessionId ID da sessão ou login do usuário remetente
     * @param comunidade Nome da comunidade
     * @param mensagem Conteúdo da mensagem
     * @throws SessionNotFoundException Se a sessão não existir
     * @throws UserNotFoundException Se o usuário não existir
     * @throws CommunityException Se a comunidade não existir
     */
    public void enviarMensagem(String sessionId, String comunidade, String mensagem) {
        String login = sessaoService.validarEObterLogin(sessionId);
        mensagemService.enviarMensagemComunidade(login, comunidade, mensagem);
    }

    /**
     * Verifica se um usuário é fã de outro.
     *
     * @param login Login do possível fã
     * @param idolo Login do possível ídolo
     * @return true se o primeiro for fã do segundo, false caso contrário
     */
    public boolean ehFa(String login, String idolo) {
        return relacionamentoService.ehFa(login, idolo);
    }

    /**
     * Adiciona um usuário como ídolo de outro.
     *
     * @param sessionId ID da sessão ou login do usuário que está adicionando (fã)
     * @param idolo Login do usuário a ser adicionado como ídolo
     * @throws SessionNotFoundException Se a sessão não existir
     * @throws UserNotFoundException Se algum dos usuários não existir
     * @throws RelacionamentoException Se o usuário tentar ser fã de si mesmo, se já for fã ou se o ídolo for inimigo
     */
    public void adicionarIdolo(String sessionId, String idolo) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarIdolo(login, idolo);
    }

    /**
     * Obtém a lista de fãs de um usuário formatada como string.
     *
     * @param login Login do usuário
     * @return String formatada com a lista de fãs: "{fa1,fa2,...}"
     */
    public String getFas(String login) {
        return relacionamentoService.getFas(login);
    }

    /**
     * Verifica se um usuário tem outro como paquera.
     *
     * @param sessionId ID da sessão ou login do primeiro usuário
     * @param paquera Login do possível paquera
     * @return true se o segundo for paquera do primeiro, false caso contrário
     * @throws SessionNotFoundException Se a sessão não existir
     * @throws UserNotFoundException Se algum dos usuários não existir
     */
    public boolean ehPaquera(String sessionId, String paquera) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return relacionamentoService.ehPaquera(login, paquera);
    }

    /**
     * Adiciona um usuário como paquera de outro.
     * Se ambos se adicionarem como paquera, o sistema envia recados automáticos.
     *
     * @param sessionId ID da sessão ou login do usuário que está adicionando
     * @param paquera Login do usuário a ser adicionado como paquera
     * @throws SessionNotFoundException Se a sessão não existir
     * @throws UserNotFoundException Se algum dos usuários não existir
     * @throws RelacionamentoException Se o usuário tentar adicionar a si mesmo, se já for paquera ou se for inimigo
     */
    public void adicionarPaquera(String sessionId, String paquera) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarPaquera(login, paquera);
    }

    /**
     * Obtém a lista de paqueras de um usuário formatada como string.
     *
     * @param sessionId ID da sessão ou login do usuário
     * @return String formatada com a lista de paqueras: "{paquera1,paquera2,...}"
     * @throws SessionNotFoundException Se a sessão não existir
     * @throws UserNotFoundException Se o usuário não existir
     */
    public String getPaqueras(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        return relacionamentoService.getPaqueras(login);
    }

    /**
     * Adiciona um usuário como inimigo de outro.
     *
     * @param sessionId ID da sessão ou login do usuário que está adicionando
     * @param inimigo Login do usuário a ser adicionado como inimigo
     * @throws SessionNotFoundException Se a sessão não existir
     * @throws UserNotFoundException Se algum dos usuários não existir
     * @throws RelacionamentoException Se o usuário tentar adicionar a si mesmo ou se já for inimigo
     */
    public void adicionarInimigo(String sessionId, String inimigo) {
        String login = sessaoService.validarEObterLogin(sessionId);
        relacionamentoService.adicionarInimigo(login, inimigo);
    }

    /**
     * Remove um usuário do sistema.
     * Também remove suas sessões, comunidades e mensagens.
     *
     * @param sessionId ID da sessão ou login do usuário a ser removido
     * @throws SessionNotFoundException Se a sessão não existir
     * @throws UserNotFoundException Se o usuário não existir
     */
    public void removerUsuario(String sessionId) {
        String login = sessaoService.validarEObterLogin(sessionId);
        usuarioService.removerUsuario(login);
        sessaoService.encerrarSessao(sessionId);
        comunidadeService.removerUsuarioDeComunidades(login);
        mensagemService.removerMensagensDoUsuario(login);
    }
}