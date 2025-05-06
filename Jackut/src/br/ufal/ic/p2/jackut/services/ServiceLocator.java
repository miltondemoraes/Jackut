package br.ufal.ic.p2.jackut.services;

import java.io.Serial;
import java.io.Serializable;

/**
 * Localizador de serviços que facilita o acesso aos diferentes serviços do sistema.
 * Implementa o padrão de projeto Service Locator.
 */
public class ServiceLocator implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** Serviço de usuários */
    private final UsuarioService usuarioService;

    /** Serviço de sessões */
    private final SessaoService sessaoService;

    /** Serviço de comunidades */
    private final ComunidadeService comunidadeService;

    /** Serviço de mensagens */
    private final MensagemService mensagemService;

    /** Serviço de relacionamentos */
    private final RelacionamentoService relacionamentoService;

    /**
     * Cria um novo localizador de serviços com instâncias padrão.
     * Inicializa todos os serviços com um repositório compartilhado.
     */
    public ServiceLocator() {
        DataRepository repository = new DataRepository();
        this.usuarioService = new UsuarioService(repository);
        this.sessaoService = new SessaoService(repository, usuarioService);
        this.comunidadeService = new ComunidadeService(repository, usuarioService);
        this.mensagemService = new MensagemService(repository, usuarioService, comunidadeService);
        this.relacionamentoService = new RelacionamentoService(repository, usuarioService, mensagemService);
    }

    /**
     * Cria um localizador de serviços com serviços específicos.
     * Útil para testes e injeção de dependências.
     *
     * @param usuarioService Serviço de usuários
     * @param sessaoService Serviço de sessões
     * @param comunidadeService Serviço de comunidades
     * @param mensagemService Serviço de mensagens
     * @param relacionamentoService Serviço de relacionamentos
     */
    public ServiceLocator(
            UsuarioService usuarioService,
            SessaoService sessaoService,
            ComunidadeService comunidadeService,
            MensagemService mensagemService,
            RelacionamentoService relacionamentoService) {
        this.usuarioService = usuarioService;
        this.sessaoService = sessaoService;
        this.comunidadeService = comunidadeService;
        this.mensagemService = mensagemService;
        this.relacionamentoService = relacionamentoService;
    }

    /**
     * @return Serviço de usuários
     */
    public UsuarioService getUsuarioService() {
        return usuarioService;
    }

    /**
     * @return Serviço de sessões
     */
    public SessaoService getSessaoService() {
        return sessaoService;
    }

    /**
     * @return Serviço de comunidades
     */
    public ComunidadeService getComunidadeService() {
        return comunidadeService;
    }

    /**
     * @return Serviço de mensagens
     */
    public MensagemService getMensagemService() {
        return mensagemService;
    }

    /**
     * @return Serviço de relacionamentos
     */
    public RelacionamentoService getRelacionamentoService() {
        return relacionamentoService;
    }
}