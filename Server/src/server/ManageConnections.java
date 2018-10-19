package server;

import org.json.JSONException;
import org.json.JSONObject;
import server.data.GroupAndChannel;
import server.data.Message;
import server.data.User;
import server.database.Database;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class ManageConnections implements Runnable
{
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private boolean canAccess = false;

    private Database database;

    public ManageConnections(Socket socket, DataInputStream inputStream, DataOutputStream outputStream, Database database)
    {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.database = database;
    }

    @Override public void run()
    {
        while (true)
        {
            try
            {
                JSONObject object = new JSONObject(inputStream.readUTF());

                if (object.getString("DO").equals("SignUp"))
                {
                    try
                    {
                        User user = database.signUp(
                                object.getString("phoneNumber"),
                                object.getString("name"),
                                object.getString("username"),
                                object.getString("password")
                                       );

                        JSONObject result = new JSONObject();

                        result.put("DO", "SignUpResult");
                        result.put("Result", "OK");

                        result.put("id", user.getId());
                        result.put("phone", user.getPhone());
                        result.put("username", user.getUserName());
                        result.put("password", user.getPassword());
                        result.put("name", user.getName());
                        result.put("profilePhotoUrl", user.getProfilePhotoUrl());
                        result.put("IsOnline", user.isOnline() ? "true" : "false");
                        result.put("isReported", user.isReported() ? "true" : "false");
                        result.put("createdTime", user.getCreatedTime());
                        result.put("createdTime", user.getLastSeen());

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();

                    }
                    catch (SQLException e)
                    {
                        JSONObject result = new JSONObject();

                        result.put("DO", "SignUpResult");
                        result.put("Result", e.getMessage());

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();
                    }
                }

                else if (object.getString("DO").equals("LogIn"))
                {
                    try
                    {
                        User user = database.logIn(
                                object.getString("phoneNumber"),
                                object.getString("password")
                                                  );

                        JSONObject result = new JSONObject();

                        result.put("DO", "LogInResult");
                        result.put("Result", "OK");

                        result.put("id", user.getId());
                        result.put("phone", user.getPhone());
                        result.put("username", user.getUserName());
                        result.put("password", user.getPassword());
                        result.put("name", user.getName());
                        result.put("profilePhotoUrl", user.getProfilePhotoUrl());
                        result.put("IsOnline", user.isOnline() ? "true" : "false");
                        result.put("isReported", user.isReported() ? "true" : "false");
                        result.put("createdTime", user.getCreatedTime());
                        result.put("createdTime", user.getLastSeen());

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();

                        Main.onlineUsers.put(user.getId(), socket);
                        canAccess  = true;
                    }
                    catch (SQLException e)
                    {
                        JSONObject result = new JSONObject();

                        result.put("DO", "LogInResult");
                        result.put("Result", e.getMessage());

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();
                    }
                }

                else if (object.getString("DO").equals("GetListsSendRequests"))
                {
                    try
                    {
                        int id = object.getInt("Myself");

                        ArrayList<User> sendRequestsList = database.getSendRequest(id);

                        JSONObject result = new JSONObject();

                        result.put("DO", "GetListsAnswerSendRequests");
                        result.put("Result", "OK");

                        result.put("SendRequestNumbers", sendRequestsList.size());

                        for (int i = 0; i < sendRequestsList.size(); i++)
                        {
                            result.put("SendRequestID" + i, sendRequestsList.get(i).getId());
                            result.put("SendRequestUserName" + i, sendRequestsList.get(i).getUserName());
                            result.put("SendRequestName" + i, sendRequestsList.get(i).getName());
                            result.put("SendRequestIsOnline" + i, sendRequestsList.get(i).isOnline());
                        }

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();

                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }

                }

                else if (object.getString("DO").equals("GetListsReceiveRequests"))
                {
                    try
                    {
                        int id = object.getInt("Myself");

                        ArrayList<User> receiveRequestList = database.getReceiveRequests(id);

                        JSONObject result = new JSONObject();

                        result.put("DO", "GetListsAnswerReceiveRequests");
                        result.put("Result", "OK");

                        result.put("ReceiveRequestNumbers", receiveRequestList.size());

                        for (int i = 0; i < receiveRequestList.size(); i++)
                        {
                            result.put("ReceiveRequestID" + i, receiveRequestList.get(i).getId());
                            result.put("ReceiveRequestUserName" + i, receiveRequestList.get(i).getUserName());
                            result.put("ReceiveRequestName" + i, receiveRequestList.get(i).getName());
                            result.put("ReceiveRequestIsOnline" + i, receiveRequestList.get(i).isOnline());
                        }

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();

                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }

                }

                else if (object.getString("DO").equals("GetListsContacts"))
                {
                    try
                    {
                        int id = object.getInt("Myself");

                        ArrayList<User> contactsList = database.getContacts(id);

                        JSONObject result = new JSONObject();

                        result.put("DO", "GetListsAnswerContacts");
                        result.put("Result", "OK");

                        result.put("ContactsNumbers", contactsList.size());

                        for (int i = 0; i < contactsList.size(); i++)
                        {
                            result.put("ContactsID" + i, contactsList.get(i).getId());
                            result.put("ContactsUserName" + i, contactsList.get(i).getUserName());
                            result.put("ContactsName" + i, contactsList.get(i).getName());
                            result.put("ContactsIsOnline" + i, contactsList.get(i).isOnline());
                        }

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();

                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }

                }

                else if (object.getString("DO").equals("GetListsBlocked"))
                {
                    try
                    {
                        int id = object.getInt("Myself");

                        ArrayList<User> blockedList = database.getBlocked(id);

                        JSONObject result = new JSONObject();

                        result.put("DO", "GetListsAnswerBlocked");
                        result.put("Result", "OK");

                        result.put("BlockedNumbers", blockedList.size());

                        for (int i = 0; i < blockedList.size(); i++)
                        {
                            result.put("BlockedID" + i, blockedList.get(i).getId());
                            result.put("BlockedUserName" + i, blockedList.get(i).getUserName());
                            result.put("BlockedName" + i, blockedList.get(i).getName());
                            result.put("BlockedIsOnline" + i, blockedList.get(i).isOnline());
                        }

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();

                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }

                }

                else if (object.getString("DO").equals("GetListsAllUsers"))
                {
                    try
                    {
                        int id = object.getInt("Myself");

                        ArrayList<User> othersList = database.getLists(id);

                        JSONObject result = new JSONObject();

                        result.put("DO", "GetListsAnswerAllUsers");
                        result.put("Result", "OK");

                        result.put("OthersNumbers", othersList.size());

                        for (int i = 0; i < othersList.size(); i++)
                        {
                            result.put("OthersID" + i, othersList.get(i).getId());
                            result.put("OthersUserName" + i, othersList.get(i).getUserName());
                            result.put("OthersName" + i, othersList.get(i).getName());
                            result.put("OthersIsOnline" + i, othersList.get(i).isOnline());
                        }

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();

                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }

                else if (object.getString("DO").equals("GetListsGroups"))
                {
                    try
                    {
                        int id = object.getInt("Myself");

                        ArrayList<GroupAndChannel> groups = database.getGroups(id);

                        JSONObject result = new JSONObject();

                        result.put("DO", "GetListsAnswerGroups");
                        result.put("Result", "OK");

                        result.put("GroupsNumbers", groups.size());

                        for (int i = 0; i < groups.size(); i++)
                        {
                            result.put("GroupsId"+i, groups.get(i).getId());
                            result.put("GroupsStringId"+i, groups.get(i).getStringId());
                            result.put("GroupsName"+i, groups.get(i).getName());
                            result.put("GroupsProfilePhotoUrl"+i, groups.get(i).getProfilePhotoUrl());
                            result.put("GroupsCreatedTime"+i, groups.get(i).getCreatedTime());
                            result.put("GroupsMembers"+i, groups.get(i).getMembers());
                        }

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();

                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }

                else if (object.getString("DO").equals("GetListsChannels"))
                {
                    try
                    {
                        int id = object.getInt("Myself");

                        ArrayList<GroupAndChannel> channels = database.getChannels(id);

                        JSONObject result = new JSONObject();

                        result.put("DO", "GetListsAnswerChannels");
                        result.put("Result", "OK");

                        result.put("ChannelsNumbers", channels.size());

                        for (int i = 0; i < channels.size(); i++)
                        {
                            result.put("ChannelsId"+i, channels.get(i).getId());
                            result.put("ChannelsStringId"+i, channels.get(i).getStringId());
                            result.put("ChannelsName"+i, channels.get(i).getName());
                            result.put("ChannelsProfilePhotoUrl"+i, channels.get(i).getProfilePhotoUrl());
                            result.put("ChannelsCreatedTime"+i, channels.get(i).getCreatedTime());
                            result.put("ChannelsMembers"+i, channels.get(i).getMembers());
                        }

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();

                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }

                else if (object.getString("DO").equals("Request"))
                {
                    try
                    {
                        database.request(object.getInt("SenderId"),
                                         object.getInt("ReceiverId"));

                        if (Main.onlineUsers.containsKey(object.getInt("ReceiverId")))
                        {
                            Socket userSocket = Main.onlineUsers.get(object.getInt("ReceiverId"));
                            JSONObject result2 = new JSONObject();
                            result2.put("DO", "ReceiveRequest");

                            DataOutputStream dataOutputStream = new DataOutputStream(userSocket.getOutputStream());
                            dataOutputStream.writeUTF(result2.toString());
                            dataOutputStream.flush();

                        }
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }

                else if (object.getString("DO").equals("SendMessage"))
                {
                    try
                    {
                        int myId = object.getInt("MySelf");
                        int  userId = object.getInt("UserId");
                        String message = object.getString("MessageText");

                        Message newMessage = database.sendMessage(myId, userId, message);

                        JSONObject result = new JSONObject();
                        result.put("DO", "SendMessageAnswer");
                        result.put("Result", "OK");
                        result.put("ID", newMessage.getId());
                        result.put("conversationId", newMessage.getConversationId());
                        result.put("senderId", newMessage.getSenderId());
                        result.put("message", newMessage.getMessage());
                        result.put("attachUrl", newMessage.getAttachUrl());
                        result.put("createdAt", newMessage.getCreatedAt());
                        result.put("editedAt", newMessage.getEditedAt());

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();

                        if (Main.onlineUsers.containsKey(userId))
                        {
                            Socket userSocket = Main.onlineUsers.get(userId);
                            JSONObject result2 = new JSONObject();
                            result2.put("DO", "ReceiveMessageAnswer");
                            result2.put("Result", "OK");
                            result2.put("ID", newMessage.getId());
                            result2.put("conversationId", newMessage.getConversationId());
                            result2.put("senderId", newMessage.getSenderId());
                            result2.put("message", newMessage.getMessage());
                            result2.put("attachUrl", newMessage.getAttachUrl());
                            result2.put("createdAt", newMessage.getCreatedAt());
                            result2.put("editedAt", newMessage.getEditedAt());
                            DataOutputStream dataOutputStream = new DataOutputStream(userSocket.getOutputStream());
                            dataOutputStream.writeUTF(result2.toString());
                            dataOutputStream.flush();
                        }

                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }

                else if (object.getString("DO").equals("RequestManage"))
                {
                    try
                    {
                       database.manageRequests(object.getInt("MySelf"), object.getInt("UserId"), object.getString("Type"));
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }

                else if (object.getString("DO").equals("GetOldMessages"))
                {
                    try
                    {
                        int myId = object.getInt("MySelf");
                        int  userId = object.getInt("UserId");

                        ArrayList<Message> messages = database.getOldMessages(myId, userId);

                        JSONObject result = new JSONObject();

                        result.put("DO", "GetOldMessagesAnswer");
                        result.put("Result", "OK");
                        result.put("Numbers", messages.size());

                        for (int i = 0; i < messages.size(); i++)
                        {
                            result.put("ID"+i, messages.get(i).getId());
                            result.put("conversationId"+i, messages.get(i).getConversationId());
                            result.put("senderId"+i, messages.get(i).getSenderId());
                            result.put("message"+i, messages.get(i).getMessage());
                            result.put("attachUrl"+i, messages.get(i).getAttachUrl());
                            result.put("createdAt"+i, messages.get(i).getCreatedAt());
                            result.put("editedAt"+i, messages.get(i).getEditedAt());
                        }

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();

                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }

                else if (object.getString("DO").equals("CreateGroup"))
                {
                    try
                    {
                        int myId = object.getInt("MySelf");
                        String name = object.getString("Name");
                        String id = object.getString("ID");

                        ArrayList<Integer> ids = new ArrayList<>();

                        for (int i = 0; i < object.getInt("Numbers"); i++)
                        {
                            ids.add(object.getInt("UserId"+i));
                        }

                        GroupAndChannel group = database.createGroup(myId, name, id, ids);

                        JSONObject result = new JSONObject();

                        result.put("DO", "CreateGroupResult");
                        result.put("Result", "OK");

                        result.put("ID", group.getId());
                        result.put("StringId", group.getStringId());
                        result.put("Name", group.getName());
                        result.put("ProfilePhotoUrl", group.getProfilePhotoUrl());
                        result.put("CreatedAt", group.getCreatedTime());

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();

                    }

                    catch (SQLException e)
                    {
                        JSONObject result = new JSONObject();

                        result.put("DO", "CreateGroupResult");
                        result.put("Result", e.getMessage());

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();
                    }
                }

                else if (object.getString("DO").equals("CreateChannel"))
                {
                    try
                    {
                        int myId = object.getInt("MySelf");
                        String name = object.getString("Name");
                        String id = object.getString("ID");

                        ArrayList<Integer> ids = new ArrayList<>();

                        for (int i = 0; i < object.getInt("Numbers"); i++)
                        {
                            ids.add(object.getInt("UserId"+i));
                        }

                        GroupAndChannel channel = database.createChannel(myId, name, id, ids);

                        JSONObject result = new JSONObject();

                        result.put("DO", "CreateChannelResult");
                        result.put("Result", "OK");

                        result.put("ID", channel.getId());
                        result.put("StringId", channel.getStringId());
                        result.put("Name", channel.getName());
                        result.put("ProfilePhotoUrl", channel.getProfilePhotoUrl());
                        result.put("CreatedAt", channel.getCreatedTime());

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();

                    }
                    catch (SQLException e)
                    {
                        JSONObject result = new JSONObject();

                        result.put("DO", "CreateChannelResult");
                        result.put("Result", e.getMessage());

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();
                    }
                }

                else if (object.getString("DO").equals("GetJoins"))
                {
                    try
                    {
                        int myId = object.getInt("MySelf");

                        JSONObject result = new JSONObject();

                        result.put("DO", "GetJoinsAnswer");
                        result.put("Result", "OK");

                        ArrayList<ArrayList<User>> usersList = database.getJoins(myId);

                        ArrayList<User> users1 = usersList.get(0);

                        result.put("1UsersNumbers", users1.size());

                        for (int i = 0; i < users1.size(); i++)
                        {
                            result.put("1UsersID" + i, users1.get(i).getId());
                            result.put("1UsersUserName" + i, users1.get(i).getUserName());
                            result.put("1UsersName" + i, users1.get(i).getName());
                            result.put("1UsersIsOnline" + i, users1.get(i).isOnline());
                        }

                        ArrayList<User> users2 = usersList.get(1);

                        result.put("2UsersNumbers", users2.size());

                        for (int i = 0; i < users2.size(); i++)
                        {
                            result.put("2UsersID" + i, users2.get(i).getId());
                            result.put("2UsersUserName" + i, users2.get(i).getUserName());
                            result.put("2UsersName" + i, users2.get(i).getName());
                            result.put("2UsersIsOnline" + i, users2.get(i).isOnline());
                        }

                        ArrayList<ArrayList<GroupAndChannel>> groupsAndChannels = database.getJoins2(myId);

                        ArrayList<GroupAndChannel> groups = groupsAndChannels.get(1);

                        result.put("GroupsNumbers", groups.size());

                        for (int i = 0; i < groups.size(); i++)
                        {
                            result.put("GroupsId" + i, groups.get(i).getId());
                            result.put("GroupsStringId" + i, groups.get(i).getStringId());
                            result.put("GroupsName" + i, groups.get(i).getName());
                            result.put("GroupsProfilePhotoUrl" + i, groups.get(i).getProfilePhotoUrl());
                            result.put("GroupsCreatedTime" + i, groups.get(i).getCreatedTime());
                            result.put("GroupsMembers" + i, groups.get(i).getMembers());
                        }

                        ArrayList<GroupAndChannel> channels = groupsAndChannels.get(0);

                        result.put("ChannelsNumbers", channels.size());

                        for (int i = 0; i < channels.size(); i++)
                        {
                            result.put("ChannelsId" + i, channels.get(i).getId());
                            result.put("ChannelsStringId" + i, channels.get(i).getStringId());
                            result.put("ChannelsName" + i, channels.get(i).getName());
                            result.put("ChannelsProfilePhotoUrl" + i, channels.get(i).getProfilePhotoUrl());
                            result.put("ChannelsCreatedTime" + i, channels.get(i).getCreatedTime());
                            result.put("ChannelsMembers" + i, channels.get(i).getMembers());
                        }

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();
                    }

                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }

                else if (object.getString("DO").equals("SendMessageInGroupAndChannel"))
                {
                    try
                    {
                        int myId = object.getInt("MySelf");

                        int id;
                        if (object.has("channel"))
                        {
                            id = object.getInt("Channel");
                        }
                        else
                            id = object.getInt("Group");

                        String message = object.getString("MessageText");

                        Message newMessage = database.sendMessageInGroupAndChannel(myId, id, message);

                        JSONObject result = new JSONObject();
                        result.put("DO", "SendMessageAnswer");
                        result.put("Result", "OK");
                        result.put("ID", newMessage.getId());
                        result.put("conversationId", newMessage.getConversationId());
                        result.put("senderId", newMessage.getSenderId());
                        result.put("message", newMessage.getMessage());
                        result.put("attachUrl", newMessage.getAttachUrl());
                        result.put("createdAt", newMessage.getCreatedAt());
                        result.put("editedAt", newMessage.getEditedAt());

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();

                        for (User user : database.getGroupAndChannelMembers(id))
                        {
                            Socket userSocket = Main.onlineUsers.get(user.getId());
                            JSONObject result2 = new JSONObject();
                            result2.put("DO", "ReceiveMessageAnswer");
                            result2.put("Result", "OK");
                            result2.put("ID", newMessage.getId());
                            result2.put("conversationId", newMessage.getConversationId());
                            result2.put("senderId", newMessage.getSenderId());
                            result2.put("message", newMessage.getMessage());
                            result2.put("attachUrl", newMessage.getAttachUrl());
                            result2.put("createdAt", newMessage.getCreatedAt());
                            result2.put("editedAt", newMessage.getEditedAt());
                            DataOutputStream dataOutputStream = new DataOutputStream(userSocket.getOutputStream());
                            dataOutputStream.writeUTF(result2.toString());
                            dataOutputStream.flush();
                        }

                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }

                else if (object.getString("DO").equals("GetOldMessagesGroupAndChannel"))
                {
                    try
                    {
                        int myId = object.getInt("MySelf");

                        int id;
                        if (object.has("channel"))
                        {
                            id = object.getInt("Channel");
                        }
                        else
                            id = object.getInt("Group");

                        ArrayList<Message> messages = database.getGroupAndChannelOldMessages(myId, id);

                        JSONObject result = new JSONObject();

                        result.put("DO", "GetOldMessagesGroupAndChannelAnswer");
                        result.put("Result", "OK");
                        result.put("Numbers", messages.size());

                        for (int i = 0; i < messages.size(); i++)
                        {
                            result.put("ID"+i, messages.get(i).getId());
                            result.put("conversationId"+i, messages.get(i).getConversationId());
                            result.put("senderId"+i, messages.get(i).getSenderId());
                            result.put("message"+i, messages.get(i).getMessage());
                            result.put("attachUrl"+i, messages.get(i).getAttachUrl());
                            result.put("createdAt"+i, messages.get(i).getCreatedAt());
                            result.put("editedAt"+i, messages.get(i).getEditedAt());
                        }

                        outputStream.writeUTF(result.toString());
                        outputStream.flush();

                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }

            }
            catch (JSONException | IOException e)
            {
                break;
            }
        }
    }
}