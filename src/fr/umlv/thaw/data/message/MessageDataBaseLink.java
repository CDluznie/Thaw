package fr.umlv.thaw.data.message;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import fr.umlv.thaw.data.channel.Channel;
import fr.umlv.thaw.data.chatter.Chatter;
import fr.umlv.thaw.util.database.DataBaseConnection;
import fr.umlv.thaw.util.date.Date;

public class MessageDataBaseLink {

	private final DataBaseConnection connection;

	/**
	 * Construct a link to the database for make operation on message.
	 *
	 * @param 	connection a connection to the database
	 * @throws 	NullPointerException if connection is null
	 */
	public MessageDataBaseLink(DataBaseConnection connection) {
		this.connection = Objects.requireNonNull(connection);
	}

	/**
	 * Insert into the database the specified message.
	 * This operation is equivalent to add to the channel table
	 * the chatting name of sender, the body of the message and the date.
	 *
	 * @param 	message the message to add to the database
	 * @throws 	NullPointerException if message is null
	 * @throws 	SQLException if an error occurs during the process
	 */
	public void insert(Message message) throws SQLException {
		Objects.requireNonNull(message);
		Date date = message.getDate();
		String ymd = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
		String hms = date.getHour() + ":" + date.getMinute() + ":" + date.getSecond();	
		String formattedDate = ymd + " " + hms;
		try(Statement statement = connection.createStatement()) {
			statement.executeUpdate("INSERT INTO " + message.getChannel().tableName() + " VALUES('" + message.getSender().getChattingName() + "', '" + message.getBody() + "', '" + formattedDate + "')");
		}
	}

	/**
	 * Select the messages from the specified channel.
	 * This operation is equivalent to instantiate messages from the database values.
	 * Be careful, this method will browse all the database entry.
	 * The returned list is descending ordered by date.
	 * The returned list is immutable.
	 * 
	 * @param 	channel the specified channel
	 * @return	The messages list of the channel.
	 * @throws 	NullPointerException if channel is null
	 * @throws 	ParseException if a parsing error during the parsing
	 * @throws 	SQLException if an error occurs during the process
	 */
	public List<Message> select(Channel channel) throws SQLException, ParseException {
		Objects.requireNonNull(channel);
		ArrayList<Message> messages = new ArrayList<>();
		Chatter chatter;
		String body;
		Date date;
		String sql = "SELECT * FROM " + channel.tableName() + " ORDER BY datetime(date);";
		try(Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql)) {
				while (result.next()) {
					chatter = getChatter(result);
					body = result.getString("message");
					date = Date.parse(result.getString("date"));
					messages.add(new Message(channel,chatter,body,date));
				}
		}
		return Collections.unmodifiableList(messages);
	}
	
	private Chatter getChatter(ResultSet result) throws SQLException {
		return new Chatter() {
			private String chattingName = result.getString("name");
			@Override
			public String getChattingName() {
				return chattingName;
			}
		};
	}

}
