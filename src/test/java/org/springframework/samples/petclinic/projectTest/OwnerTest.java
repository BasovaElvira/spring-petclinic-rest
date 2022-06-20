package org.springframework.samples.petclinic.projectTest;

import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

public class OwnerTest {

	private Connection connection;

	@BeforeEach
	public void connect() throws SQLException {
		connection = DriverManager.getConnection("jdbc:postgresql://localhost/petclinic", "petclinic", "petclinic");
	}

	@AfterEach
	public void disconnect() throws SQLException {
		connection.close();
	}

	@Test
	@DisplayName("Create Location")
	public void shouldCreateOwner() throws SQLException {

		int postalCode = new Random().nextInt(999999);

		PreparedStatement sql = connection
				.prepareStatement(String.format("SELECT*FROM location WHERE='" + postalCode + "'"));
		/*
		 * { "address": "string", "city": "string", "firstName": "string", "id": 0,
		 * "lastName": "string", "pets": [ { "birthDate": "2022-06-20", "id": 0, "name":
		 * "string", "visits": [ { "date": "2022-06-20", "description": "string", "id": 0
		 * } ] } ], "telephone": "string" }
		 */

		given().contentType("application/json")
				.body("{\n" + "  \"address\": \"" + "Ленина" + "\",\n" + "  \"city\": \"Кострома\",\n"
						+ "  \"firstName\": \"Елена\",\n" + "  \"lastName\": \"Васина\",\n" + "}")
				.when().post("/api/locations").then().statusCode(201).body("id", not(empty()));

		ResultSet keys = sql.getGeneratedKeys();
		assertThat(keys.next(), is(true));
		assertThat(keys.getString(2), is("Елена"));
	}

}
