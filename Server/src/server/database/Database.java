package server.database;

import server.data.GroupAndChannel;
import server.data.Message;
import server.data.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Database
{

    private Statement statement;

    public Database(Statement statement)
    {
        this.statement = statement;
    }

    public User signUp(String phoneNumber, String name, String username, String password) throws SQLException
    {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String dateTime = dateTimeFormatter.format(localDateTime);

        String s = "INSERT into messenger.users(phone, user_name, password, name, profile_photo_url, created_at, updated_at)" +
                " values ('" + phoneNumber + "', '" + username + "', '" + password + "', '" + name + "','" + "Profiles/"+username+".jpg" + "','" + dateTime + "','" + dateTime + " ')";
        statement.execute(s);

        s = "SELECT * FROM messenger.users WHERE phone = '" + phoneNumber + "'";
        ResultSet resultSet = statement.executeQuery(s);

        if (! resultSet.isBeforeFirst())
            throw new SQLException("Error");

        if (resultSet.next())
        {
            return new User(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5),
                    resultSet.getString(6),
                    resultSet.getBoolean(7),
                    resultSet.getBoolean(8),
                    resultSet.getString(9),
                    resultSet.getString(10)
            );
        }

        throw new SQLException("Error");
    }

    public User logIn(String phoneNumber, String password) throws SQLException
    {
        String s = "SELECT * from messenger.users where phone = '" + phoneNumber + "'AND password = '" + password + "'";
        ResultSet resultSet = statement.executeQuery(s);

        if (! resultSet.isBeforeFirst())
            throw new SQLException("Phone Number Or Password Is Incorrect");

        if (resultSet.next())
        {
            return new User(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getString(5),
                    resultSet.getString(6),
                    resultSet.getBoolean(7),
                    resultSet.getBoolean(8),
                    resultSet.getString(9),
                    resultSet.getString(10)
            );
        }

        throw new SQLException("Error");
    }

    public ArrayList<User> getLists(int id) throws SQLException
    {

        String s =
                "SELECT * from messenger.users where id != '" + id + "'";

        ResultSet resultSet = statement.executeQuery(s);

        return getUsers(resultSet);
    }

    public ArrayList<User> getSendRequest(int id) throws SQLException
    {
        String s =
                "SELECT * from messenger.users " +
                        "INNER JOIN messenger.requests " +
                        "ON  requests.reciver_id = users.id " +
                        "WHERE requests.sender_id = '" + id +
                        "' AND requests.is_accepted = '0' AND requests.is_rejected = '0'";

        ResultSet resultSet = statement.executeQuery(s);

        return getUsers(resultSet);
    }

    public ArrayList<User> getReceiveRequests(int id) throws SQLException
    {
        String s =
                "SELECT * from messenger.users " +
                        "INNER JOIN messenger.requests " +
                        "ON  requests.sender_id = users.id " +
                        "WHERE requests.reciver_id = '" + id +
                        "' AND requests.is_accepted = '0' AND requests.is_rejected = '0' AND requests.is_blocked = '0'";

        ResultSet resultSet = statement.executeQuery(s);

        return getUsers(resultSet);
    }

    public ArrayList<User> getContacts(int id) throws SQLException
    {
        String s =
                "SELECT * from messenger.users INNER JOIN messenger.contacts " +
                        "ON contacts.contact_id = users.id " +
                        "WHERE contacts.user_id = '" + id + "'";

        ResultSet resultSet = statement.executeQuery(s);

        return getUsers(resultSet);
    }

    public ArrayList<User> getBlocked(int id) throws SQLException
    {
        String s =
                "SELECT * from messenger.users INNER JOIN messenger.block_list " +
                        "ON block_list.bloced_user_id = users.id " +
                        "WHERE block_list.users_id = '" + id + "'";

        ResultSet resultSet = statement.executeQuery(s);

        return getUsers(resultSet);
    }

    private ArrayList<User> getUsers(ResultSet resultSet) throws SQLException
    {
        ArrayList<User> list = new ArrayList<>();

        if (! resultSet.isBeforeFirst())
            return list;

        else if (resultSet.first())
        {
            do
            {
                User user = new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6),
                        resultSet.getBoolean(7),
                        resultSet.getBoolean(8),
                        resultSet.getString(9),
                        resultSet.getString(10)
                );

                list.add(user);

            } while (resultSet.next());
        }
        return list;
    }

    public void request(int id1, int id2) throws SQLException
    {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String dateTime = dateTimeFormatter.format(localDateTime);

        String s = "INSERT into messenger.requests(sender_id, reciver_id, created_at) values ('" + id1 + "', '" + id2 + "', '" + dateTime + " ')";
        statement.execute(s);
    }

    public void manageRequests(int myId, int userId, String doWhat) throws SQLException
    {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String dateTime = dateTimeFormatter.format(localDateTime);

        switch (doWhat)
        {
            case "Accept":
                String sql = "UPDATE messenger.requests SET is_accepted = '1' " +
                        "WHERE sender_id = '" + userId + "' AND reciver_id = '" + myId + "'";
                statement.execute(sql);

                sql = "INSERT INTO messenger.contacts (user_id, contact_id, created_at) " +
                        "VALUES ('" + myId + "', '" + userId + "', '" + dateTime + "')";
                statement.execute(sql);

                sql = "INSERT INTO messenger.contacts (user_id, contact_id, created_at) " +
                        "VALUES ('" + userId + "', '" + myId + "', '" + dateTime + "')";

                statement.execute(sql);

                sql = "INSERT INTO messenger.chats (first_user, second_user) " +
                        "VALUES ('" + myId + "', '" + userId + "')";

                statement.execute(sql);

                int chatId = getChatId(myId, userId);
                sql = "INSERT INTO messenger.conversation (type, type_id) VALUES ('chats', '" + chatId + "')";
                statement.execute(sql);

                int conId = getConversationId("chats");
                sql = "INSERT INTO messenger.participants (user_id, conversation_id)" +
                        " VALUES ('" + myId + "', '" + conId + "')";
                statement.execute(sql);

                sql = "INSERT INTO messenger.participants (user_id, conversation_id)" +
                        " VALUES ('" + userId + "', '" + conId + "')";
                statement.execute(sql);

                break;

            case "Reject":
                String sql2 = "UPDATE messenger.requests SET is_rejected = '1'" +
                        "WHERE sender_id = '" + userId + "' AND reciver_id = '" + myId + "'";
                statement.execute(sql2);
                break;

            case "Block":
                String sql3 = "INSERT into messenger.block_list " +
                        "VALUES ('" + myId + "', '" + userId + "', '" + dateTime + "')";
                statement.execute(sql3);
                break;

            case "Cancel":
                String sql4 = "DELETE from messenger.requests " +
                        "where sender_id = '" + myId + "' AND reciver_id = '" + userId + "'";
                statement.execute(sql4);
                break;
        }
    }

    public Message sendMessage(int myId, int userId, String message) throws SQLException
    {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String dateTime = dateTimeFormatter.format(localDateTime);

        int conversationId = getConversationId("chats");

        String sql2 =
                "INSERT INTO messenger.messages (conversation_id, sender_id, message, created_at) " +
                        "VALUES ( '" + conversationId + "', '" + myId + "', '" + message + "', '" + dateTime + "' ) ";

        statement.execute(sql2);

        String sql3 =
                "SELECT * from messenger.messages " +
                        "WHERE conversation_id = '" + conversationId + "' AND sender_id = '" + myId + "' " +
                        "AND message = '" + message + "' AND created_at = '" + dateTime + "' ";

        ResultSet resultSet1 = statement.executeQuery(sql3);

        if (! resultSet1.isBeforeFirst())
            return null;

        else if (resultSet1.first())
        {
            return new Message(
                    resultSet1.getInt(1),
                    resultSet1.getInt(2),
                    resultSet1.getInt(3),
                    resultSet1.getString(4),
                    resultSet1.getString(5),
                    resultSet1.getString(6),
                    resultSet1.getString(7)
            );
        }

        return null;
    }

    private int getConversationId(String doWhat) throws SQLException
    {
        String sql =
                "SELECT * from messenger.conversation " +
                        "inner join messenger."+ doWhat +" ON conversation.type_id = " + doWhat + ".id " +
                        "WHERE conversation.type = '" + doWhat + "'";

        ResultSet resultSet = statement.executeQuery(sql);

        if (! resultSet.isBeforeFirst())
            return 0;

        else if (resultSet.first())
        {
            return resultSet.getInt(1);
        }

        return 0;
    }

    public ArrayList<Message> getOldMessages(int myId, int userId) throws SQLException
    {
        ArrayList<Message> messages = new ArrayList<>();

       int conversationId = getConversationId("chats");

        String sql3 =
                "SELECT * from messenger.messages " +
                        "WHERE conversation_id = '" + conversationId + "' ORDER BY id ASC ";

        ResultSet resultSet1 = statement.executeQuery(sql3);

        if (! resultSet1.isBeforeFirst())
            return messages;

        else if (resultSet1.first())
        {
            do
            {
                Message message = new Message(
                        resultSet1.getInt(1),
                        resultSet1.getInt(2),
                        resultSet1.getInt(3),
                        resultSet1.getString(4),
                        resultSet1.getString(5),
                        resultSet1.getString(6),
                        resultSet1.getString(7)
                );

                messages.add(message);

            } while (resultSet1.next());
        }

        return messages;
    }

    private int getChatId(int myId, int userId) throws SQLException
    {
        String sql =
                "SELECT id from messenger.chats " +
                        "WHERE ( first_user = '" + myId + "' AND second_user = '" + userId + "' ) " +
                        " OR ( first_user = '" + userId + "' AND second_user = '" + myId + "' )";

        ResultSet resultSet = statement.executeQuery(sql);

        if (! resultSet.isBeforeFirst())
            return 0;

        else if (resultSet.first())
        {
            return resultSet.getInt(1);
        }

        return 0;
    }

    public GroupAndChannel createGroup(int myId, String name, String id, ArrayList<Integer> ids) throws SQLException
    {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String dateTime = dateTimeFormatter.format(localDateTime);

        String sql = "INSERT into messenger.groups(string_id, admin_id, name, profile_photo_url, created_at)" +
                " VALUES ('" + id + "', '" + myId + "', '" + name + "', '" + "Groups/"+ id + ".jpg" + "', '" + dateTime + "')";

        statement.execute(sql);

        sql = "SELECT * from messenger.groups where string_id = '" + id + "'";
        ResultSet resultSet = statement.executeQuery(sql);

        if (! resultSet.isBeforeFirst())
            throw new SQLException("Error");

        if (resultSet.next())
        {
            GroupAndChannel group = new GroupAndChannel(
                    resultSet.getInt(1),
                    resultSet.getInt(3),
                    resultSet.getString(2),
                    resultSet.getString(4),
                    resultSet.getString(5),
                    resultSet.getString(6)
            );

            sql = "INSERT INTO messenger.conversation (type, type_id) VALUES ('groups', '" + group.getId() + "')";
            statement.execute(sql);

            int conId = getConversationId("groups");

            sql = "INSERT INTO messenger.participants (user_id, conversation_id)" +
                    " VALUES ('" + myId + "', '" + conId + "')";
            statement.execute(sql);

            for (Integer id1 : ids)
            {
                sql = "INSERT INTO messenger.participants (user_id, conversation_id)" +
                        " VALUES ('" + id1 + "', '" + conId + "')";
                statement.execute(sql);
            }

            return group;
        }

        throw new SQLException("Error");
    }

    public GroupAndChannel createChannel(int myId, String name, String id, ArrayList<Integer> ids) throws SQLException
    {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String dateTime = dateTimeFormatter.format(localDateTime);

        String sql = "INSERT into messenger.channels(string_id, admin_id, name, profile_photo_url, created_at)" +
                " VALUES ('" + id + "', '" + myId + "', '" + name + "', '" + "Channels/"+ id + ".jpg" + "', '" + dateTime + "')";

        statement.execute(sql);

        sql = "SELECT * from messenger.channels where string_id = '" + id + "'";
        ResultSet resultSet = statement.executeQuery(sql);

        if (! resultSet.isBeforeFirst())
            throw new SQLException("Error");

        if (resultSet.next())
        {
            GroupAndChannel channel = new GroupAndChannel(
                    resultSet.getInt(1),
                    resultSet.getInt(3),
                    resultSet.getString(2),
                    resultSet.getString(4),
                    resultSet.getString(5),
                    resultSet.getString(6)
            );

            sql = "INSERT INTO messenger.conversation (type, type_id) VALUES ('channels', '" + channel.getId() + "')";
            statement.execute(sql);

            int conId = getConversationId("channels");

            sql = "INSERT INTO messenger.participants (user_id, conversation_id)" +
                    " VALUES ('" + myId + "', '" + conId + "')";
            statement.execute(sql);

            for (Integer id1 : ids)
            {
                sql = "INSERT INTO messenger.participants (user_id, conversation_id)" +
                        " VALUES ('" + id1 + "', '" + conId + "')";
                statement.execute(sql);
            }

            return channel;
        }

        throw new SQLException("Error");
    }

    public ArrayList<GroupAndChannel> getChannels(int id) throws SQLException
    {
        String sql = "SELECT * from messenger.channels where admin_id != '" + id + "'";

        ResultSet resultSet = statement.executeQuery(sql);

        return getGroupsAndChannels(resultSet);
    }

    public ArrayList<GroupAndChannel> getGroups(int id) throws SQLException
    {
        String sql = "SELECT * from messenger.groups where admin_id != '" + id + "'";

        ResultSet resultSet = statement.executeQuery(sql);

        return getGroupsAndChannels(resultSet);
    }

    private ArrayList<GroupAndChannel> getGroupsAndChannels(ResultSet resultSet) throws SQLException
    {
        ArrayList<GroupAndChannel> groupAndChannels = new ArrayList<>();

        if (! resultSet.isBeforeFirst())
            return groupAndChannels;

        else if (resultSet.first())
        {
            do
            {
                GroupAndChannel groupAndChannel = new GroupAndChannel(
                        resultSet.getInt(1),
                        resultSet.getInt(3),
                        resultSet.getString(2),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getString(6)
                );

                groupAndChannels.add(groupAndChannel);

            } while (resultSet.next());
        }

        return groupAndChannels;
    }

    public ArrayList<ArrayList<User>> getJoins(int myId) throws SQLException
    {
        String sql = "select * from messenger.users " +
                "inner join messenger.chats " +
                "on users.id = chats.first_user " +
                "inner join messenger.conversation " +
                "on chats.id = conversation.type_id " +
                "inner join messenger.participants " +
                "on participants.conversation_id = conversation.id " +
                "where participants.user_id = '" + myId + "' " +
                "and conversation.type = 'chats' " +
                "and users.id != '" + myId + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<User> users1 = getUsers(resultSet);

        sql = "select * from messenger.users " +
                "inner join messenger.chats " +
                "on users.id = chats.second_user " +
                "inner join messenger.conversation " +
                "on chats.id = conversation.type_id " +
                "inner join messenger.participants " +
                "on participants.conversation_id = conversation.id " +
                "where participants.user_id = '" + myId + "' " +
                "and conversation.type = 'chats' " +
                "and users.id != '" + myId + "'";
        ResultSet resultSet2 = statement.executeQuery(sql);
        ArrayList<User> users2 = getUsers(resultSet2);

        ArrayList<ArrayList<User>> users = new ArrayList<>();
        users.add(users1);
        users.add(users2);
        return users;
    }

    public ArrayList<ArrayList<GroupAndChannel>> getJoins2(int myId) throws SQLException
    {
        String sql = "select * from messenger.channels " +
                "inner join messenger.conversation " +
                "on channels.id = conversation.type_id " +
                "inner join messenger.participants " +
                "on participants.conversation_id = conversation.id " +
                "where participants.user_id = '" + myId + "' " +
                "and conversation.type = 'channels'";
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<GroupAndChannel> channels = getGroupsAndChannels(resultSet);

        sql = "select * from messenger.groups " +
                "inner join messenger.conversation " +
                "on groups.id = conversation.type_id " +
                "inner join messenger.participants " +
                "on participants.conversation_id = conversation.id " +
                "where participants.user_id = '" + myId + "' " +
                "and conversation.type = 'groups'";
        ResultSet resultSet2 = statement.executeQuery(sql);
        ArrayList<GroupAndChannel> groups = getGroupsAndChannels(resultSet2);

        ArrayList<ArrayList<GroupAndChannel>> groupAndChannels = new ArrayList<>();
        groupAndChannels.add(channels);
        groupAndChannels.add(groups);
        return groupAndChannels;
    }

    public ArrayList<Message> getGroupAndChannelOldMessages(int myId, int id) throws SQLException
    {
        ArrayList<Message> messages = new ArrayList<>();

        int conversationId = getConversationId("channels");

        String sql3 =
                "SELECT * from messenger.messages " +
                        "WHERE conversation_id = '" + conversationId + "' ORDER BY id ASC ";

        ResultSet resultSet1 = statement.executeQuery(sql3);

        if (! resultSet1.isBeforeFirst())
            return messages;

        else if (resultSet1.first())
        {
            do
            {
                Message message = new Message (
                        resultSet1.getInt(1),
                        resultSet1.getInt(2),
                        resultSet1.getInt(3),
                        resultSet1.getString(4),
                        resultSet1.getString(5),
                        resultSet1.getString(6),
                        resultSet1.getString(7)
                );

                messages.add(message);

            } while (resultSet1.next());
        }

        return messages;
    }

    public Message sendMessageInGroupAndChannel(int myId, int id, String message) throws SQLException
    {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String dateTime = dateTimeFormatter.format(localDateTime);

        int conversationId = getConversationId("channels");

        String sql2 =
                "INSERT INTO messenger.messages (conversation_id, sender_id, message, created_at) " +
                        "VALUES ( '" + conversationId + "', '" + myId + "', '" + message + "', '" + dateTime + "' ) ";

        statement.execute(sql2);

        String sql3 =
                "SELECT * from messenger.messages " +
                        "WHERE conversation_id = '" + conversationId + "' AND sender_id = '" + myId + "' " +
                        "AND message = '" + message + "' AND created_at = '" + dateTime + "' ";

        ResultSet resultSet1 = statement.executeQuery(sql3);

        if (! resultSet1.isBeforeFirst())
            return null;

        else if (resultSet1.first())
        {
            return new Message(
                    resultSet1.getInt(1),
                    resultSet1.getInt(2),
                    resultSet1.getInt(3),
                    resultSet1.getString(4),
                    resultSet1.getString(5),
                    resultSet1.getString(6),
                    resultSet1.getString(7)
            );
        }

        return null;

    }

    public ArrayList<User> getGroupAndChannelMembers (int id) throws SQLException
    {
        int conId = getConversationId("channels");

        String sql = "select * from messenger.users " +
                "inner join messenger.participants " +
                "on participants.user_id = users.id " +
                "where participants.conversation_id = '" + conId + "'";
        ResultSet resultSet = statement.executeQuery(sql);
        return getUsers(resultSet);
    }
}