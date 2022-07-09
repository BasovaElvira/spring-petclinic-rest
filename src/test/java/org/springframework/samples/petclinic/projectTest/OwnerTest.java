package org.springframework.samples.petclinic.projectTest;

import org.junit.jupiter.api.*;
import org.springframework.util.Assert;

import java.sql.*;
import java.util.ArrayList;

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
	@DisplayName("Create owner without Pet")
	public void shouldCreateOwnerWithoutPets() throws SQLException {

		given().contentType("application/json")
			.body("{\n" +
				"  \"firstName\": \"Jane\",\n" +
				"  \"lastName\": \"Frost\",\n" +
				"  \"address\": \"105 W. Killua St.\",\n" +
				"  \"city\": \"New-York\",\n" +
				"  \"telephone\": \"123\",\n" +
				"}")
			.when().post("/owners").then().statusCode(400);
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

		ResultSet stat = connection.createStatement()
			.executeQuery("SELECT*FROM owners WHERE first_name='Mila'");
		ArrayList<String> values = new ArrayList<>();
		while(stat.next()) {
			values.add(stat.getString("Id"));
			values.add(stat.getString("first_name"));
			values.add(stat.getString("last_name"));
			values.add(stat.getString("address"));
			values.add(stat.getString("city"));
			values.add(stat.getString("telephone"));
		}
		assertThat(values.get(1), is("Mila"));
		assertThat(values.get(2), is("Franklin"));
		assertThat(values.get(3), is("105 W. Liberty St."));
		assertThat(values.get(4), is("Madison"));
		assertThat(values.get(5), is("6085551022"));

		String sql = String.format("SELECT*FROM pets WHERE owner_Id='" + values.get(0) + "';");
		ResultSet statPets = connection.createStatement()
			.executeQuery(sql);
		ArrayList<String> petValues = new ArrayList<>();
		while(statPets.next()) {
			petValues.add(statPets.getString("name"));
			petValues.add(statPets.getString("birth_date"));
		}
		assertThat(petValues.get(0), is("Pit"));
		assertThat(petValues.get(1), is("2015-09-07"));
	}

	@Test
	@DisplayName("Create owner with pet and visits")
	public void shouldCreateOwnerWithoutPetAndVisits() throws SQLException {
		given().contentType("application/json")
			.body("{\n" +
				"  \"address\": \"123, International st.\",\n" +
				"  \"city\": \"Moscow\",\n" +
				"  \"firstName\": \"Vitaliy\",\n" +
				"  \"lastName\": \"Ivanov\",\n" +
				"  \"pets\": [\n" +
				"    {\n" +
				"      \"birthDate\": \"2022-07-09\",\n" +
				"      \"name\": \"Mimi\",\n" +
				"      \"visits\": [\n" +
				"        {\n" +
				"          \"date\": \"2022-07-09\",\n" +
				"          \"description\": \"first visit\"\n" +
				"        }\n" +
				"      ]\n" +
				"    }\n" +
				"  ],\n" +
				"  \"telephone\": \"+79165544554\"\n" +
				"}")
			.when().post("/owners").then().statusCode(201).body("id", not(empty()));


		ResultSet stat = connection.createStatement()
			.executeQuery("SELECT*FROM owners WHERE first_name='Mila'");
		ArrayList<String> values = new ArrayList<>();
		while(stat.next()) {
			values.add(stat.getString("Id"));
			values.add(stat.getString("first_name"));
			values.add(stat.getString("last_name"));
			values.add(stat.getString("address"));
			values.add(stat.getString("city"));
			values.add(stat.getString("telephone"));
		}
		assertThat(values.get(1), is("Vitaliy"));
		assertThat(values.get(2), is("Ivanov"));
		assertThat(values.get(3), is("123, International st."));
		assertThat(values.get(4), is("Moscow"));
		assertThat(values.get(5), is("+79165544554"));

		String sqlPets = String.format("SELECT*FROM pets WHERE owner_Id='" + values.get(0) + "';");
		ResultSet statPets = connection.createStatement()
			.executeQuery(sqlPets);
		ArrayList<String> petValues = new ArrayList<>();
		while(statPets.next()) {
			petValues.add(statPets.getString("Id"));
			petValues.add(statPets.getString("name"));
			petValues.add(statPets.getString("birth_date"));
		}
		assertThat(petValues.get(1), is("Mimi"));
		assertThat(petValues.get(2), is("2022-07-09"));

		String sqlVisits = String.format("SELECT*FROM visits WHERE pet_Id='" + petValues.get(0) + "';");
		ResultSet statVisits = connection.createStatement()
			.executeQuery(sqlVisits);
		ArrayList<String> petVisits = new ArrayList<>();
		while(statPets.next()) {
			petValues.add(statVisits.getString("visit_date"));
			petValues.add(statVisits.getString("description"));
		}
		assertThat(petValues.get(1), is("2022-07-09"));
		assertThat(petValues.get(2), is("first visit"));
	}

	@Test
	@DisplayName("Create owner without first_name")
	public void shouldCreateOwnerWithoutFirstName() throws SQLException {

		given().contentType("application/json")
			.body("{\n" +
				"  \"lastName\": \"Frost\",\n" +
				"  \"address\": \"105 W. Killua St.\",\n" +
				"  \"city\": \"New-York\",\n" +
				"  \"telephone\": \"123\",\n" +
				"}")
			.when().post("/owners").then().statusCode(400);
	}

}
