package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Produto;
import org.example.model.Simulacao;
import org.example.repository.simulacao.SimulacaoRepository;
import org.example.repository.auditoria.AuditoriaRepository;
import org.example.model.Auditoria;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.example.dto.SimulacaoRequestDTO;
import org.example.dto.SimulacaoResponseDTO;
import org.example.dto.PaginatedResponseDTO;
import org.example.dto.SimulacaoResumoDTO;
import org.example.service.strategy.SimulacaoStrategy;
import org.example.service.strategy.SimulacaoStrategyFactory;
import org.example.dto.SimulacaoResumoCustomDTO;
import org.example.dto.PaginatedSimulacaoResumoResponseDTO;

@Service
public class SimulacaoService {
    private final ProdutoService produtoService;
    private final SimulacaoRepository simulacaoRepository;
    private final EventHubService eventHubService;
    private final ObjectMapper objectMapper;
    private final AuditoriaRepository auditoriaRepository;
    private final EventHubQueueProducer eventHubQueueProducer;
    private final SimulacaoStrategyFactory simulacaoStrategyFactory;
    private static final Logger logger = LoggerFactory.getLogger(SimulacaoService.class);

    public SimulacaoService(ProdutoService produtoService, SimulacaoRepository simulacaoRepository, EventHubService eventHubService, ObjectMapper objectMapper, AuditoriaRepository auditoriaRepository, EventHubQueueProducer eventHubQueueProducer, SimulacaoStrategyFactory simulacaoStrategyFactory) {
        this.produtoService = produtoService;
        this.simulacaoRepository = simulacaoRepository;
        this.eventHubService = eventHubService;
        this.objectMapper = objectMapper;
        this.auditoriaRepository = auditoriaRepository;
        this.eventHubQueueProducer = eventHubQueueProducer;
        this.simulacaoStrategyFactory = simulacaoStrategyFactory;
    }

    public Simulacao salvarSimulacao(Simulacao simulacao) {
        simulacao.setDataSimulacao(LocalDateTime.now());
        Simulacao salva = simulacaoRepository.save(simulacao);
        logger.info("[AUDITORIA] Simulação salva: {}", salva);
        // Auditoria persistente
        Auditoria audit = new Auditoria("admin", "SALVAR_SIMULACAO", "Simulação salva com id=" + salva.getId());
        auditoriaRepository.save(audit);
        return salva;
    }

    public List<Simulacao> listarSimulacoes() {
        List<Simulacao> sims = simulacaoRepository.findAll();
        logger.info("[AUDITORIA] Consulta de simulações. Total: {}", sims.size());
        return sims;
    }

    public Optional<Produto> buscarProdutoPorId(Long id) {
        logger.info("[AUDITORIA] Consulta de produto por ID: {}", id);
        return produtoService.buscarPorId(id);
    }

    // Métodos para cálculo SAC e Price, validação e integração EventHub serão implementados aqui
    public boolean validarDados(Simulacao simulacao, Produto produto) {
        boolean valido = true;
        if (simulacao == null || produto == null) {
            valido = false;
        } else if (simulacao.getValorSolicitado() == null || produto.getValorMinimo() == null) {
            valido = false;
        } else if (BigDecimal.valueOf(simulacao.getValorSolicitado()).compareTo(produto.getValorMinimo()) < 0 ||
            (produto.getValorMaximo() != null && BigDecimal.valueOf(simulacao.getValorSolicitado()).compareTo(produto.getValorMaximo()) > 0)) {
            valido = false;
        } else if (simulacao.getPrazo() < produto.getPrazoMinimo() ||
            (produto.getPrazoMaximo() != null && simulacao.getPrazo() > produto.getPrazoMaximo())) {
            valido = false;
        }
        logger.info("[AUDITORIA] Validação de dados da simulação: {}. Resultado: {}", simulacao, valido);
        return valido;
    }

    public String gerarEnvelopeJson(Simulacao simulacao) {
        try {
            return objectMapper.writeValueAsString(simulacao);
        } catch (Exception e) {
            logger.error("Erro ao gerar JSON do envelope", e);
            throw new RuntimeException("Erro ao gerar JSON do envelope");
        }
    }

    public void simularEnvioEventHub(String envelopeJson) {
        eventHubQueueProducer.sendEnvelopeToQueue(envelopeJson);
    }

    public Simulacao fluxoCompletoSimulacao(Simulacao simulacao) {
        logger.info("[AUDITORIA] Início do fluxo completo de simulação para produto: {}", simulacao.getProduto());
        Produto produto = produtoService.listarProdutos().stream()
                .filter(p -> p.getNome().equalsIgnoreCase(simulacao.getProduto()))
                .findFirst().orElse(null);
        if (produto == null) throw new RuntimeException("Produto não encontrado");
        if (!validarDados(simulacao, produto)) throw new RuntimeException("Dados inválidos para o produto");
        simulacao.setTaxaJuros(produto.getTaxaJuros() != null ? produto.getTaxaJuros().doubleValue() : null);
        // Utiliza Strategy + Factory, mas não salva parcelas na entidade
        Simulacao salva = salvarSimulacao(simulacao);
        String envelope = gerarEnvelopeJson(salva);
        simularEnvioEventHub(envelope);
        logger.info("[AUDITORIA] Fluxo completo finalizado para simulação: {}", salva);
        return salva;
    }

    public List<Simulacao> listarPorProdutoEDia(String produto, LocalDateTime inicio, LocalDateTime fim) {
        return simulacaoRepository.findByProduto(produto).stream()
                .filter(s -> s.getDataSimulacao() != null &&
                        !s.getDataSimulacao().isBefore(inicio) &&
                        !s.getDataSimulacao().isAfter(fim))
                .toList();
    }

    public List<SimulacaoResponseDTO> listarPorProdutoEDiaDTO(String produto, LocalDateTime inicio, LocalDateTime fim) {
        List<Simulacao> simulacoes = listarPorProdutoEDia(produto, inicio, fim);
        List<SimulacaoResponseDTO> dtos = new ArrayList<>();
        for (Simulacao s : simulacoes) {
            SimulacaoResponseDTO dto = new SimulacaoResponseDTO();
            SimulacaoResponseDTO.ModeloEnvelopeRetornoSimulacao env = new SimulacaoResponseDTO.ModeloEnvelopeRetornoSimulacao();
            env.setIdSimulacao(s.getId());
            env.setCodigoProduto(1); // Ajustar conforme regra de negócio
            env.setDescricaoProduto(s.getProduto());
            env.setTaxaJuros(s.getTaxaJuros());
            // Não há cálculo de parcelas aqui, apenas exemplo básico
            env.setResultadoSimulacao(new ArrayList<>());
            dto.setModeloEnvelopeRetornoSimulacao(env);
            dtos.add(dto);
        }
        return dtos;
    }

    public org.example.dto.TelemetriaDTO gerarTelemetriaDTO() {
        org.example.dto.TelemetriaDTO telemetria = new org.example.dto.TelemetriaDTO();
        // Data de referência: hoje
        telemetria.setDataReferencia(java.time.LocalDate.now().toString());
        List<org.example.dto.TelemetriaDTO.EndpointTelemetria> endpoints = new ArrayList<>();
        // Simulação de dados de telemetria para o endpoint principal
        org.example.dto.TelemetriaDTO.EndpointTelemetria ep = new org.example.dto.TelemetriaDTO.EndpointTelemetria();
        ep.setNomeApi("Simulacao");
        ep.setQtdRequisicoes(135); // valor simulado
        ep.setTempoMedio(150); // ms
        ep.setTempoMinimo(23);
        ep.setTempoMaximo(860);
        ep.setPercentualSucesso(0.98);
        endpoints.add(ep);
        telemetria.setListaEndpoints(endpoints);
        return telemetria;
    }

    public SimulacaoResponseDTO fluxoCompletoSimulacaoDTO(SimulacaoRequestDTO request) {
        SimulacaoRequestDTO.ModeloEnvelopeSimulacao env = request.getModeloEnvelopeSimulacao();
        Simulacao simulacao = new Simulacao();
        simulacao.setValorSolicitado(env.getValorDesejado());
        simulacao.setPrazo(env.getPrazo());
        // Filtrar produtos que atendam aos critérios de valor e prazo
        List<Produto> produtosValidos = produtoService.listarProdutos().stream()
            .filter(p -> {
                boolean valorOk = env.getValorDesejado() != null &&
                    p.getValorMinimo() != null &&
                    BigDecimal.valueOf(env.getValorDesejado()).compareTo(p.getValorMinimo()) >= 0 &&
                    (p.getValorMaximo() == null || BigDecimal.valueOf(env.getValorDesejado()).compareTo(p.getValorMaximo()) <= 0);
                boolean prazoOk = env.getPrazo() != null &&
                    p.getPrazoMinimo() != null &&
                    env.getPrazo() >= p.getPrazoMinimo() &&
                    (p.getPrazoMaximo() == null || env.getPrazo() <= p.getPrazoMaximo());
                return valorOk && prazoOk;
            })
            .toList();
        if (produtosValidos.isEmpty()) {
            throw new org.example.exception.BusinessException("Não há produtos disponíveis para os parâmetros informados.");
        }
        Produto produto = produtosValidos.get(0); // Seleciona o primeiro produto válido
        simulacao.setProduto(produto.getNome());
        Double taxaJurosDouble = null;
        if (produto.getTaxaJuros() != null) {
            taxaJurosDouble = produto.getTaxaJuros().doubleValue();
        }
        simulacao.setTaxaJuros(taxaJurosDouble);
        // Calcular SAC e PRICE usando as estratégias refatoradas
        SimulacaoStrategy sacStrategy = simulacaoStrategyFactory.getStrategy("Sac");
        SimulacaoStrategy priceStrategy = simulacaoStrategyFactory.getStrategy("Price");
        if (taxaJurosDouble == null) {
            throw new org.example.exception.BusinessException("Produto selecionado não possui taxa de juros definida.");
        }
        SimulacaoResponseDTO.ResultadoSimulacao sac = sacStrategy.calcularParcelas(env, taxaJurosDouble);
        sac.setTipo("SAC");
        SimulacaoResponseDTO.ResultadoSimulacao price = priceStrategy.calcularParcelas(env, taxaJurosDouble);
        price.setTipo("PRICE");
        Simulacao salva = salvarSimulacao(simulacao);
        SimulacaoResponseDTO response = new SimulacaoResponseDTO();
        SimulacaoResponseDTO.ModeloEnvelopeRetornoSimulacao envResp = new SimulacaoResponseDTO.ModeloEnvelopeRetornoSimulacao();
        envResp.setIdSimulacao(salva.getId());
        envResp.setCodigoProduto(produto.getId());
        envResp.setDescricaoProduto(produto.getNome());
        envResp.setTaxaJuros(salva.getTaxaJuros());
        ArrayList<SimulacaoResponseDTO.ResultadoSimulacao> resultados = new ArrayList<>();
        resultados.add(sac);
        resultados.add(price);
        envResp.setResultadoSimulacao(resultados);
        response.setModeloEnvelopeRetornoSimulacao(envResp);
        return response;
    }

    public List<SimulacaoResponseDTO> listarSimulacoesDTO() {
        List<Simulacao> simulacoes = listarSimulacoes();
        List<SimulacaoResponseDTO> dtos = new ArrayList<>();
        for (Simulacao s : simulacoes) {
            SimulacaoResponseDTO dto = new SimulacaoResponseDTO();
            SimulacaoResponseDTO.ModeloEnvelopeRetornoSimulacao env = new SimulacaoResponseDTO.ModeloEnvelopeRetornoSimulacao();
            env.setIdSimulacao(s.getId());
            env.setCodigoProduto(1); // Ajustar conforme regra de negócio
            env.setDescricaoProduto(s.getProduto());
            env.setTaxaJuros(s.getTaxaJuros());
            env.setResultadoSimulacao(new ArrayList<>()); // Não há cálculo de parcelas aqui
            dto.setModeloEnvelopeRetornoSimulacao(env);
            dtos.add(dto);
        }
        return dtos;
    }

    @Cacheable("simulacoesPaginadas")
    public PaginatedResponseDTO<SimulacaoResumoDTO> listarSimulacoesPaginado(int page, int size) {
        List<Simulacao> todas = simulacaoRepository.findAll();
        int totalElements = todas.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = Math.max((page - 1) * size, 0);
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<SimulacaoResumoDTO> content = new ArrayList<>();
        if (fromIndex < toIndex) {
            for (Simulacao s : todas.subList(fromIndex, toIndex)) {
                SimulacaoResumoDTO dto = new SimulacaoResumoDTO();
                dto.setIdSimulacao(s.getId());
                dto.setValorDesejado(s.getValorSolicitado());
                dto.setPrazo(s.getPrazo());
                // Buscar produto pelo nome
                Produto produto = produtoService.buscarPorNome(s.getProduto());
                if (produto != null) {
                    // Estratégias SAC e Price
                    SimulacaoStrategy sacStrategy = simulacaoStrategyFactory.getStrategy("Sac");
                    SimulacaoStrategy priceStrategy = simulacaoStrategyFactory.getStrategy("Price");
                    List<org.example.dto.SimulacaoResponseDTO.Parcela> sacParcelas = sacStrategy.calcular(s, produto);
                    List<org.example.dto.SimulacaoResponseDTO.Parcela> priceParcelas = priceStrategy.calcular(s, produto);
                    double totalSac = sacParcelas.stream().mapToDouble(p -> p.getValorPrestacao()).sum();
                    double totalPrice = priceParcelas.stream().mapToDouble(p -> p.getValorPrestacao()).sum();
                    dto.setValorTotalParcelasSac(totalSac);
                    dto.setValorTotalParcelasPrice(totalPrice);
                } else {
                    dto.setValorTotalParcelasSac(null);
                    dto.setValorTotalParcelasPrice(null);
                }
                content.add(dto);
            }
        }
        return new PaginatedResponseDTO<>(content, page, size, totalElements, totalPages);
    }
}
