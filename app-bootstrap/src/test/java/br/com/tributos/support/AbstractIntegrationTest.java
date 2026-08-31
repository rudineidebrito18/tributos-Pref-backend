package br.com.tributos.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

/**
 * Base dos testes de integração com Postgres efêmero compartilhado entre todas as classes de teste.
 * {@link DynamicPropertySource} tem prioridade sobre {@code DB_URL} do {@code .env} local.
 * O container é iniciado uma única vez por JVM (static) para não ser derrubado entre classes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    protected static final WireMockServer WIRE_MOCK_OAUTH = new WireMockServer(0);

    static {
        POSTGRES.start();
        WIRE_MOCK_OAUTH.start();
        WireMock.configureFor("localhost", WIRE_MOCK_OAUTH.port());
    }

    @DynamicPropertySource
    static void registrarDatasourceDoContainer(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("pix.bb.oauth.homologacao-base-url",
            () -> "http://localhost:" + WIRE_MOCK_OAUTH.port());
        registry.add("pix.bb.oauth.producao-base-url",
            () -> "http://localhost:" + WIRE_MOCK_OAUTH.port());
    }
}
