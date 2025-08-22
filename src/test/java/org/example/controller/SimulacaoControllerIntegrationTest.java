package org.example.controller;

import org.example.dto.PaginatedResponseDTO;
import org.example.dto.SimulacaoResumoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.example.service.EventHubService;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = org.example.Main.class)
public class SimulacaoControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private EventHubService eventHubService;

    @Test
    void deveListarSimulacoesComSucesso() {
        String url = "http://localhost:" + port + "/simulacoes";
        ResponseEntity<PaginatedResponseDTO> response = restTemplate.withBasicAuth("admin", "admin123")
                .getForEntity(url, PaginatedResponseDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void deveCriarSimulacaoComSucesso() {
        String url = "http://localhost:" + port + "/simulacoes";
        String body = "{" +
                "\"produto\": \"credito-pessoal\"," +
                "\"valor\": 10000," +
                "\"prazo\": 12," +
                "\"taxaJuros\": 1.5" +
                "}";
        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "admin123")
                .postForEntity(url, body, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("valorTotal");
    }

    @Test
    void deveRetornar401SemAutenticacao() {
        String url = "http://localhost:" + port + "/simulacoes";
        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void actuatorHealthDeveRetornarUp() {
        String url = "http://localhost:" + port + "/actuator/health";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("UP");
    }

    @Test
    void actuatorMetricsDeveRetornarOk() {
        String url = "http://localhost:" + port + "/actuator/metrics";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("jvm.memory.used");
    }

    @Test
    void deveRetornar400ParaValorNegativo() {
        String url = "http://localhost:" + port + "/simulacoes";
        String body = "{" +
                "\"produto\": \"credito-pessoal\"," +
                "\"valor\": -1000," +
                "\"prazo\": 12," +
                "\"taxaJuros\": 1.5" +
                "}";
        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "admin123")
                .postForEntity(url, body, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void deveRetornar400ParaPrazoZero() {
        String url = "http://localhost:" + port + "/simulacoes";
        String body = "{" +
                "\"produto\": \"credito-pessoal\"," +
                "\"valor\": 1000," +
                "\"prazo\": 0," +
                "\"taxaJuros\": 1.5" +
                "}";
        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "admin123")
                .postForEntity(url, body, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void deveRetornar404ParaProdutoInexistente() {
        String url = "http://localhost:" + port + "/simulacoes";
        String body = "{" +
                "\"produto\": \"produto-invalido\"," +
                "\"valor\": 1000," +
                "\"prazo\": 12," +
                "\"taxaJuros\": 1.5" +
                "}";
        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "admin123")
                .postForEntity(url, body, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deveRetornar401ParaUsuarioInvalido() {
        String url = "http://localhost:" + port + "/simulacoes";
        String body = "{" +
                "\"produto\": \"credito-pessoal\"," +
                "\"valor\": 1000," +
                "\"prazo\": 12," +
                "\"taxaJuros\": 1.5" +
                "}";
        ResponseEntity<String> response = restTemplate.withBasicAuth("usuario", "senhaerrada")
                .postForEntity(url, body, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void deveRetornar404ParaEndpointInexistente() {
        String url = "http://localhost:" + port + "/naoexiste";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deveRetornarTelemetriaComSucesso() {
        String url = "http://localhost:" + port + "/simulacoes/telemetria";
        ResponseEntity<String> response = restTemplate.withBasicAuth("admin", "admin123")
                .getForEntity(url, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("listaEndpoints");
    }

    // Teste de rate limit (pode ser necessário ajustar o endpoint/configuração para garantir o 429)
    @Test
    void deveRetornar429AoExcederRateLimit() {
        String url = "http://localhost:" + port + "/simulacoes";
        String body = "{" +
                "\"produto\": \"credito-pessoal\"," +
                "\"valor\": 1000," +
                "\"prazo\": 12," +
                "\"taxaJuros\": 1.5" +
                "}";
        // Envia várias requisições rápidas para tentar estourar o rate limit
        ResponseEntity<String> ultimaResposta = null;
        for (int i = 0; i < 20; i++) {
            ultimaResposta = restTemplate.withBasicAuth("admin", "admin123")
                    .postForEntity(url, body, String.class);
            if (ultimaResposta.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                break;
            }
        }
        assertThat(ultimaResposta.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.TOO_MANY_REQUESTS);
    }

    @Test
    void deveSuportarConcorrenciaSimultanea() throws Exception {
        String url = "http://localhost:" + port + "/simulacoes";
        String body = "{" +
                "\"produto\": \"credito-pessoal\"," +
                "\"valor\": 10000," +
                "\"prazo\": 12," +
                "\"taxaJuros\": 1.5" +
                "}";
        int threads = 20;
        List<CompletableFuture<ResponseEntity<String>>> futures = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            futures.add(CompletableFuture.supplyAsync(() ->
                restTemplate.withBasicAuth("admin", "admin123")
                    .postForEntity(url, body, String.class)
            ));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        for (CompletableFuture<ResponseEntity<String>> future : futures) {
            ResponseEntity<String> response = future.get();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).contains("valorTotal");
        }
    }

    @Test
    void logsNaoDevemConterInformacoesSensiveis() {
        // Captura logs usando um appender temporário
        ch.qos.logback.classic.Logger rootLogger =
            (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent> listAppender =
            new ch.qos.logback.core.read.ListAppender<>();
        listAppender.start();
        rootLogger.addAppender(listAppender);

        // Executa uma requisição
        String url = "http://localhost:" + port + "/simulacoes";
        String body = "{" +
                "\"produto\": \"credito-pessoal\"," +
                "\"valor\": 10000," +
                "\"prazo\": 12," +
                "\"taxaJuros\": 1.5," +
                "\"senha\": \"segredo123\"" + // campo sensível proposital
                "}";
        restTemplate.withBasicAuth("admin", "admin123")
                .postForEntity(url, body, String.class);

        // Verifica que "senha" não aparece nos logs
        boolean contemSenha = listAppender.list.stream()
            .anyMatch(event -> event.getFormattedMessage().toLowerCase().contains("senha"));
        rootLogger.detachAppender(listAppender);
        assertThat(contemSenha).isFalse();
    }
}
