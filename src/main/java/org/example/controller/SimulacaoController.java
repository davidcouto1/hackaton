package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.example.dto.PaginatedResponseDTO;
import org.example.dto.SimulacaoRequestDTO;
import org.example.dto.SimulacaoResponseDTO;
import org.example.dto.SimulacaoResumoDTO;
import org.example.exception.BusinessException;
import org.example.model.Simulacao;
import org.example.service.SimulacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/simulacoes")
public class SimulacaoController {
    private final SimulacaoService simulacaoService;

    public SimulacaoController(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    @Operation(summary = "Realiza uma simulação de crédito", description = "Recebe os dados da simulação, executa o cálculo e retorna o envelope JSON gerado.\n\nRequer autenticação HTTP Basic (Authorization: Basic ...).",
        security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Simulação realizada com sucesso",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"valorTotal\":10000,\"parcelas\":[...]}"))),
        @ApiResponse(responseCode = "400", description = "Erro de validação ou processamento",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Mensagem de erro\"}"))),
        @ApiResponse(responseCode = "401", description = "Não autenticado - forneça credenciais válidas",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Não autenticado\"}"))),
        @ApiResponse(responseCode = "403", description = "Acesso negado - sem permissão",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Acesso negado\"}"))),
        @ApiResponse(responseCode = "404", description = "Recurso não encontrado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Recurso não encontrado\"}"))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Erro interno\"}"))),
        @ApiResponse(responseCode = "429", description = "Rate limit excedido",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Rate limit exceeded. Tente novamente em instantes.\"}")))
    })
    @PostMapping
    public ResponseEntity<SimulacaoResponseDTO> simular(@Valid @RequestBody SimulacaoRequestDTO request) {
        SimulacaoResponseDTO response = simulacaoService.fluxoCompletoSimulacaoDTO(request);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handleValidationException(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Void> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Void> handleBusinessException(BusinessException ex) {
        return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Lista todas as simulações", description = "Retorna todas as simulações cadastradas.\n\nRequer autenticação HTTP Basic (Authorization: Basic ...).",
        security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de simulações",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Não autenticado - forneça credenciais válidas",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Não autenticado\"}"))),
        @ApiResponse(responseCode = "403", description = "Acesso negado - sem permissão",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Acesso negado\"}"))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Erro interno\"}")))
    })
    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<SimulacaoResumoDTO>> listarSimulacoes(
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "20") int qtdRegistrosPagina) {
        PaginatedResponseDTO<SimulacaoResumoDTO> resposta = simulacaoService.listarSimulacoesPaginado(pagina, qtdRegistrosPagina);
        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Lista simulações por produto e dia", description = "Retorna simulações filtradas por produto e data (yyyy-MM-dd).\n\nRequer autenticação HTTP Basic (Authorization: Basic ...).",
        security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de simulações filtradas",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou erro de processamento",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Parâmetros inválidos\"}"))),
        @ApiResponse(responseCode = "401", description = "Não autenticado - forneça credenciais válidas",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Não autenticado\"}"))),
        @ApiResponse(responseCode = "403", description = "Acesso negado - sem permissão",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Acesso negado\"}"))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Erro interno\"}")))
    })
    @GetMapping("/por-dia")
    public ResponseEntity<List<SimulacaoResponseDTO>> listarPorProdutoEDia(
            @RequestParam String produto,
            @RequestParam String data) {
        try {
            LocalDateTime inicio = LocalDateTime.parse(data + "T00:00:00");
            LocalDateTime fim = LocalDateTime.parse(data + "T23:59:59");
            List<SimulacaoResponseDTO> dtos = simulacaoService.listarPorProdutoEDiaDTO(produto, inicio, fim);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Telemetria", description = "Retorna informações de telemetria da API.\n\nRequer autenticação HTTP Basic (Authorization: Basic ...).",
        security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações de telemetria",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Não autenticado - forneça credenciais válidas",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Não autenticado\"}"))),
        @ApiResponse(responseCode = "403", description = "Acesso negado - sem permissão",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Acesso negado\"}"))),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"erro\":\"Erro interno\"}")))
    })
    @GetMapping("/telemetria")
    public ResponseEntity<org.example.dto.TelemetriaDTO> telemetria() {
        org.example.dto.TelemetriaDTO telemetria = simulacaoService.gerarTelemetriaDTO();
        return ResponseEntity.ok(telemetria);
    }
}
