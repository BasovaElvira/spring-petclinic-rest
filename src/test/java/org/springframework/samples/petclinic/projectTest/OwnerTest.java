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
	@DisplayName("Create owner with pet")
	public void shouldCreateOwnerWithPet() throws SQLException {

		given().contentType("application/json")
				.body("{\n" +
					"  \"firstName\": \"Mila\",\n" +
					"  \"lastName\": \"Franklin\",\n" +
					"  \"address\": \"105 W. Liberty St.\",\n" +
					"  \"city\": \"Madison\",\n" +
					"  \"telephone\": \"6085551022\",\n" +
					"  \"pets\": [\n" +
					"    {\n" +
					"      \"name\": \"Pit\",\n" +
					"      \"birthDate\": \"2015-09-07\",\n" +
					"      \"visits\": []\n" +
					"    }\n" +
					"  ]\n" +
					"}")
				.when().post("/owners").then().statusCode(201).body("id", not(empty()));

		PreparedStatement sql = connection
			.prepareStatement(String.format("SELECT*FROM owners WHERE firstName='Mila'"));
		ResultSet keys = sql.getGeneratedKeys();
		int ownerId = keys.getInt(1);
		assertThat(keys.next(), is(true));
		assertThat(keys.getString(3), is("Franklin"));
		PreparedStatement sqlOwnerId = connection
			.prepareStatement(String.format("SELECT*FROM pets WHERE ownerId='" + ownerId + "'"));
		ResultSet keyForOwner = sqlOwnerId.getGeneratedKeys();
		assertThat(keyForOwner.next(), is(true));
		assertThat(keyForOwner.getString(2), is("Pit"));

	}


}
