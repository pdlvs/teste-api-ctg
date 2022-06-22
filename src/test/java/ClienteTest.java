import io.restassured.http.ContentType;

import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;

import org.hamcrest.core.IsEqual;


public class ClienteTest {

    private static final String BASE_URI = "http://localhost:8080";
    private static final String CLIENTE_ENDPOINT = "/cliente";
    private static final String DELETAR_TODOS = "/apagaTodos";
    private static final String LISTA_VAZIA = "{}";

    @Test
    @DisplayName("Quando eu requisitar a lista de clientes sem adicionar clientes antes, Então ela deve estar vazia")
    public void buscarTodosOsClientes() {

        deletarTodos();

        pegaTodosClientes()
                .statusCode(HttpStatus.SC_OK)
                .body(equalTo(LISTA_VAZIA));
    }

    @Test
    @DisplayName("Quando eu cadastrar um cliente, Então ele deve ser salvo com sucesso")
    public void cadastrarCliente() {

        Cliente cliente = new Cliente("Spike", 5,25000);

        cadastraCliente(cliente)
                .statusCode(HttpStatus.SC_CREATED)
                .body("25000.nome", equalTo("Spike"))
                .body("25000.idade", equalTo(5))
                .body("25000.id", equalTo(25000));
    }

    @Test
    @DisplayName("Quando eu atualizar um cliente, Então ele deve ser atualizado com sucesso")
    public void atualizarCliente() {

        Cliente cliente = new Cliente("Gary", 3, 54765);

        cadastraCliente(cliente);

        cliente.setNome("Gary, Lima");
        cliente.setIdade(3);
        cliente.setId(54765);

        atualizaCliente(cliente)
                .statusCode(HttpStatus.SC_OK)
                .body("54765.nome", equalTo("Gary, Lima"))
                .body("54765.idade", equalTo(3))
                .body("54765.id", equalTo(54765));
    }

    @Test
    @DisplayName("Quando eu deletar um cliente, Então ele deve ser removido com sucesso")
    public void deletarCliente() {

        Cliente cliente = new Cliente("Jorge", 20, 4894);

        cadastraCliente(cliente);

        excluirCliente(cliente)
                .statusCode(HttpStatus.SC_OK)
                .assertThat().body(not(contains("Jorge")));
    }

    private ValidatableResponse cadastraCliente (Cliente clienteParaPostar)  {
        return given()
                .contentType(ContentType.JSON)
                .body(clienteParaPostar)
                .when().
                post(baseURI + CLIENTE_ENDPOINT)
                .then();
    }

    private ValidatableResponse atualizaCliente (Cliente clienteParaAtualizar) {
        return given()
                .contentType(ContentType.JSON)
                .body(clienteParaAtualizar).
                when().
                put(BASE_URI + CLIENTE_ENDPOINT).
                then();
    }

    private ValidatableResponse excluirCliente(Cliente clienteApagar) {
        return  given()
                .contentType(ContentType.JSON)
                .when()
                .delete(BASE_URI + CLIENTE_ENDPOINT + "/" + clienteApagar.getId())
                .then();
    }

    private ValidatableResponse pegaTodosClientes () {
        return  given()
                .contentType(ContentType.JSON)
                .when()
                .get(BASE_URI)
                .then();
    }

    @AfterEach
    private void deletarTodos(){
        when()
                .delete(BASE_URI + CLIENTE_ENDPOINT + DELETAR_TODOS)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .assertThat().body(new IsEqual(LISTA_VAZIA));
    }
}
