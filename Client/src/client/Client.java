package client;

import client.data.GroupAndChannel;
import client.data.Message;
import client.data.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Client
{
    private DataOutputStream outputStream;
    private boolean isConnected;

    public Client()
    {
        this.isConnected = false;
    }

    public Client(DataOutputStream outputStream)
    {
        this.outputStream = outputStream;
        isConnected = true;
    }

    public void signUp(String phoneNumber, String name, String username, String password) throws Exception
    {
        if (isConnected)
        {
            try
            {
                JSONObject object = new JSONObject();

                object.put("DO", "SignUp");
                object.put("phoneNumber", phoneNumber);
                object.put("name", name);
                object.put("username", username);
                object.put("password", password);

               send(object.toString());
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        else
            throw new Exception("Error");
    }

    public void logIn(String phoneNumber, String password) throws Exception
    {
        if (isConnected)
        {
            try
            {
                JSONObject object = new JSONObject();

                object.put("DO", "LogIn");
                object.put("phoneNumber", phoneNumber);
                object.put("password", password);

                send(object.toString());
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        else
            throw new Exception("Error");
    }

    public void getUsersList(String doWhat)
    {
        if (isConnected && Main.mySelf != null)
        {
            try
            {
                JSONObject object = new JSONObject();

                object.put("DO", "GetLists" + doWhat);
                object.put("Myself", Main.mySelf.getId());

                send(object.toString());
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void sendRequest(User user)
    {
        if (isConnected)
        {
            try
            {
                JSONObject object = new JSONObject();

                object.put("DO", "Request");
                object.put("SenderId", Main.mySelf.getId());
                object.put("ReceiverId", user.getId());

                send(object.toString());
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void manageRequests(User user, String doWhat)
    {
        if (isConnected)
        {
            try
            {
                JSONObject object = new JSONObject();
                object.put("DO", "RequestManage");

                switch (doWhat)
                {
                    case "Accept":
                        object.put("Type", "Accept");
                        object.put("MySelf", Main.mySelf.getId());
                        object.put("UserId", user.getId());
                        break;

                    case "Reject":
                        object.put("Type", "Reject");
                        object.put("MySelf", Main.mySelf.getId());
                        object.put("UserId", user.getId());
                        break;

                    case "Block":
                        object.put("Type", "Block");
                        object.put("MySelf", Main.mySelf.getId());
                        object.put("UserId", user.getId());
                        break;

                    case "Cancel":
                        object.put("Type", "Cancel");
                        object.put("MySelf", Main.mySelf.getId());
                        object.put("UserId", user.getId());
                        break;
                }
                send(object.toString());
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void join(GroupAndChannel groupAndChannel, String doWhat)
    {
        if (isConnected)
        {
            try
            {
                JSONObject object = new JSONObject();

                object.put("DO", "Join");
                object.put("MyId", Main.mySelf.getId());
                object.put("GroupAndChannelId"+doWhat, groupAndChannel.getId());

                send(object.toString());
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void unblock(User user)
    {
        //TODO
    }

    public void getOldMessages(User user)
    {
        if (isConnected)
        {
            try
            {
                JSONObject object = new JSONObject();
                object.put("DO", "GetOldMessages");
                object.put("MySelf", Main.mySelf.getId());
                object.put("UserId", user.getId());

                send(object.toString());

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void getJoins()
    {
        if (isConnected && Main.mySelf != null)
        {
            try
            {
                JSONObject object = new JSONObject();
                object.put("DO", "GetJoins");
                object.put("MySelf", Main.mySelf.getId());

                send(object.toString());

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void sendMessages(String message, User user)
    {
        if (isConnected)
        {
            try
            {
                JSONObject object = new JSONObject();
                object.put("DO", "SendMessage");
                object.put("MySelf", Main.mySelf.getId());
                object.put("UserId", user.getId());
                object.put("MessageText", message);

                send(object.toString());

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void sendMessages(String message, GroupAndChannel groupAndChannel)
    {
        if (isConnected)
        {
            try
            {
                JSONObject object = new JSONObject();
                object.put("DO", "SendMessageInGroupAndChannel");
                object.put("MySelf", Main.mySelf.getId());
                object.put(groupAndChannel.getType(), groupAndChannel.getId());
                object.put("MessageText", message);

                send(object.toString());

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void getOldMessages(GroupAndChannel groupAndChannel)
    {
        if (isConnected)
        {
            try
            {
                JSONObject object = new JSONObject();
                object.put("DO", "GetOldMessagesGroupAndChannel");
                object.put("MySelf", Main.mySelf.getId());
                object.put(groupAndChannel.getType(), groupAndChannel.getId());

                send(object.toString());

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void createGroup(String name, String id, ArrayList<User> users)
    {
        if (isConnected)
        {
            try
            {
                JSONObject object = new JSONObject();
                object.put("DO", "CreateGroup");
                object.put("MySelf", Main.mySelf.getId());
                object.put("Name", name);
                object.put("ID", id);
                object.put("Numbers", users.size());

                for (int i = 0; i < users.size(); i++)
                {
                    object.put("UserId"+i, users.get(i).getId());
                }

                send(object.toString());

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void createChannel(String name, String id, ArrayList<User> users)
    {
        if (isConnected)
        {
            try
            {
                JSONObject object = new JSONObject();
                object.put("DO", "CreateChannel");
                object.put("MySelf", Main.mySelf.getId());
                object.put("Name", name);
                object.put("ID", id);
                object.put("Numbers", users.size());

                for (int i = 0; i < users.size(); i++)
                {
                    object.put("UserId"+i, users.get(i).getId());
                }

                send(object.toString());

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void send(String message)
    {
        Sender sender = new Sender(message);
        Thread thread = new Thread(sender);
        thread.setDaemon(true);
        thread.start();
    }

    private class Sender implements Runnable
    {
        private String message;

        private Sender(String message)
        {
            this.message = message;
        }

        @Override public void run()
        {
            try
            {
                System.out.println(message);
                outputStream.writeUTF(message);
                outputStream.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void manageMessage(Message message, String doWhat, String newMessage)
    {
        if (isConnected)
        {
            try
            {
                JSONObject object = new JSONObject();
                object.put("DO", "MessageManage");

                switch (doWhat)
                {
                    case "Attach":
                        object.put("Type", "Attach");
                        object.put("MySelf", Main.mySelf.getId());
                        object.put("ConId", message.getConversationId());
                        object.put("MessageId", message.getId());
                        object.put("Attach", newMessage);
                        break;

                    case "Edit":
                        object.put("Type", "Edit");
                        object.put("MySelf", Main.mySelf.getId());
                        object.put("ConId", message.getConversationId());
                        object.put("MessageId", message.getId());
                        object.put("NewMessage", newMessage);
                        break;

                    case "Delete":
                        object.put("Type", "Delete");
                        object.put("MySelf", Main.mySelf.getId());
                        object.put("ConId", message.getConversationId());
                        object.put("MessageId", message.getId());
                        break;

                }

                send(object.toString());
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

}