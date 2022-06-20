package org.springframework.samples.petclinic.vet;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.sql.*;

import static io.restassured.RestAssured.given;

public class VetsTests {
	private static Connection connection;
	private static final String vetFirstName = "firstName";
	private static final String vetLastName = "lastName";
	private static String idString;


	@BeforeEach
	public void connect() throws SQLException {
		connection = DriverManager.getConnection(
			"jdbc:postgresql://localhost/petclinic",
			"petclinic",
			"petclinic"
		);
	}

	@BeforeTestMethod
	public void createVetForTest() throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT id FROM vets WHERE id=(SELECT max(id) FROM vets);");
		idString=resultSet.getString("id");
		PreparedStatement preparedStatement = connection.prepareStatement(
			"INSERT INTO vets (id, first_name,last_name) VALUES("+vetFirstName+","+vetLastName+")"
		);
	}

	@Test
	public void checkDisplayExistVet() {
		Response res =  given().contentType(ContentType.JSON)
			.get("/vets")
			.then()
			.statusCode(200)
			.log()
			.all()
			.extract().response();

		int i =6;
		assert true;
	}

}
