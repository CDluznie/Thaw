package fr.umlv.thaw.util.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseConnection implements AutoCloseable {

	private final Connection connection;
	
	/**
	 * Construct a new connection to the Thaw database.
	 * 
	 * @throws 	SQLException if an error occurs during the connection getting
	 */
	public DataBaseConnection() throws SQLException {
		String database = "thaw.db";
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.err.println("org.sqlite.jdbc Not found");
			System.err.println(e.getMessage());
			System.exit(1);
		}
		try {
			Files.deleteIfExists(Paths.get(database));
			Files.createFile(Paths.get(database));
		} catch (IOException e) {
			System.err.println("Failed to create the database");
			System.err.println(e.getMessage());
			System.exit(1);
		}
		connection = DriverManager.getConnection("jdbc:sqlite:" + database);
	}
	
	@Override
	public void close() throws SQLException {
		connection.close();
	}
	
	/**
	 * Create a SQL statement for relational database request.
	 * 
	 * @return	A SQL statement.
	 * @throws 	SQLException if an error occurs during the statement creation
	 */
	public Statement createStatement() throws SQLException {
		return connection.createStatement();
	}
	
}
