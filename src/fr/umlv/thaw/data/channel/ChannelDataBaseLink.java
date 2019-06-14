package fr.umlv.thaw.data.channel;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import fr.umlv.thaw.util.database.DataBaseConnection;

public class ChannelDataBaseLink {

	private final DataBaseConnection connection;

	/**
	 * Construct a link to the database for make operation on channel.
	 *
	 * @param 	connection a connection to the database
	 * @throws 	NullPointerException if connection is null
	 */
	public ChannelDataBaseLink(DataBaseConnection connection) {
		this.connection = Objects.requireNonNull(connection);
	}

	/**
	 * Insert into the database the specified channel.
	 * This operation is equivalent to create a new table for the channel.
	 *
	 * @param 	channel the channel to add to the database
	 * @throws 	NullPointerException if channel is null
	 * @throws 	SQLException if an error occurs during the process
	 */
	public void insert(Channel channel) throws SQLException  {
		Objects.requireNonNull(channel);
		try(Statement statement = connection.createStatement()) {
			statement.executeUpdate("DROP TABLE IF EXISTS " + channel.tableName());
			statement.executeUpdate("CREATE TABLE " + channel.tableName() + " (name STRING, message STRING, date TEXT)");
		}
	}

	/**
	 * Perpetuate the channel into the database.
	 * This operation is equivalent to rename the table of the channel
	 * to the previous name suffixed by '_CLOSE'.
	 * The channel must be already add into the database.
	 *
	 * @param 	channel the channel to add to the database
	 * @throws 	NullPointerException if channel is null
	 * @throws 	SQLException if an error occurs during the process
	 */
	public void perpetuate(Channel channel) throws SQLException {
		Objects.requireNonNull(channel);
		try(Statement statement = connection.createStatement()) {
			statement.executeUpdate("ALTER TABLE " + channel.tableName() + " RENAME TO " + channel.tableName() + "_CLOSE");
		}
	}
	
}
