package client;

import client.threads.Downloader;
import client.controllers.*;
import client.data.GroupAndChannel;
import client.data.Message;
import client.data.User;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HandleInput
{
    public static ListController listController;
    public static SignUpController signUpController;
    public static LogInController logInController;
    public static ChatController chatController;
    public static ChannelController channelController;
    public static GroupController groupController;
    public static Controller controller;

    private static void download(String fileName)
    {
        Downloader downloader = new Downloader(fileName);
        downloader.run();
    }

    public static void handle(String message)
    {
        try
        {
            JSONObject answer = new JSONObject(message);

            switch (answer.getString("DO"))
            {
                case "GetListsAnswerSendRequests":
                {
                    ArrayList<User> sendRequestsList = new ArrayList<>();

                    for (int i = 0; i < answer.getInt("SendRequestNumbers"); i++)
                    {
                        download("Profiles/" + answer.getString("SendRequestUserName" + i) + ".jpg");

                        User user = new User(
                                answer.getInt("SendRequestID" + i),
                                answer.getString("SendRequestUserName" + i),
                                answer.getString("SendRequestName" + i),
                                Main.savedPath + "Profiles/" + answer.getString("SendRequestUserName" + i) + ".jpg",
                                answer.getBoolean("SendRequestIsOnline" + i)
                        );

                        sendRequestsList.add(user);
                    }

                    ListController.UpdateList updateList = listController.new UpdateList(
                            sendRequestsList, "SendRequest");
                    Thread thread = new Thread(updateList);
                    thread.setDaemon(true);
                    thread.start();
                    break;
                }

                case "GetListsAnswerReceiveRequests":
                {
                    ArrayList<User> receiveRequestList = new ArrayList<>();

                    for (int i = 0; i < answer.getInt("ReceiveRequestNumbers"); i++)
                    {
                        download("Profiles/" + answer.getString("ReceiveRequestUserName" + i) + ".jpg");

                        User user = new User(
                                answer.getInt("ReceiveRequestID" + i),
                                answer.getString("ReceiveRequestUserName" + i),
                                answer.getString("ReceiveRequestName" + i),
                                Main.savedPath + "Profiles/" + answer.getString("ReceiveRequestUserName" + i) + ".jpg",
                                answer.getBoolean("ReceiveRequestIsOnline" + i)
                        );

                        receiveRequestList.add(user);
                    }

                    ListController.UpdateList updateList = listController.new UpdateList(
                            receiveRequestList, "ReceiveRequest");
                    Thread thread = new Thread(updateList);
                    thread.setDaemon(true);
                    thread.start();
                    break;
                }

                case "GetListsAnswerContacts":
                {
                    ArrayList<User> contactsList = new ArrayList<>();

                    for (int i = 0; i < answer.getInt("ContactsNumbers"); i++)
                    {
                        download("Profiles/" + answer.getString("ContactsUserName" + i) + ".jpg");

                        User user = new User(
                                answer.getInt("ContactsID" + i),
                                answer.getString("ContactsUserName" + i),
                                answer.getString("ContactsName" + i),
                                Main.savedPath + "Profiles/" + answer.getString("ContactsUserName" + i) + ".jpg",
                                answer.getBoolean("ContactsIsOnline" + i)
                        );

                        contactsList.add(user);
                    }
                    ListController.UpdateList updateList = listController.new UpdateList(contactsList, "Contacts");
                    Thread thread = new Thread(updateList);
                    thread.setDaemon(true);
                    thread.start();

                    break;
                }

                case "GetListsAnswerBlocked":
                {
                    ArrayList<User> blockedList = new ArrayList<>();

                    for (int i = 0; i < answer.getInt("BlockedNumbers"); i++)
                    {
                        download("Profiles/" + answer.getString("BlockedUserName" + i) + ".jpg");

                        User user = new User(
                                answer.getInt("BlockedID" + i),
                                answer.getString("BlockedUserName" + i),
                                answer.getString("BlockedName" + i),
                                Main.savedPath + "Profiles/" + answer.getString("BlockedUserName" + i) + ".jpg",
                                answer.getBoolean("BlockedIsOnline" + i)
                        );

                        blockedList.add(user);
                    }

                    ListController.UpdateList updateList = listController.new UpdateList(blockedList, "Blocked");
                    Thread thread = new Thread(updateList);
                    thread.setDaemon(true);
                    thread.start();
                    break;
                }

                case "GetListsAnswerAllUsers":
                {

                    ArrayList<User> othersList = new ArrayList<>();

                    for (int i = 0; i < answer.getInt("OthersNumbers"); i++)
                    {
                        download("Profiles/" + answer.getString("OthersUserName" + i) + ".jpg");

                        User user = new User(
                                answer.getInt("OthersID" + i),
                                answer.getString("OthersUserName" + i),
                                answer.getString("OthersName" + i),
                                Main.savedPath + "Profiles/" + answer.getString("OthersUserName" + i) + ".jpg",
                                answer.getBoolean("OthersIsOnline" + i)
                        );

                        othersList.add(user);
                    }

                    ListController.UpdateList updateList = listController.new UpdateList(othersList, "AllUsers");
                    Thread thread = new Thread(updateList);
                    thread.setDaemon(true);
                    thread.start();
                    break;
                }

                case "GetListsAnswerGroups" :
                {

                    ArrayList<GroupAndChannel> groups = new ArrayList<>();

                    for (int i = 0; i < answer.getInt("GroupsNumbers"); i++)
                    {
                        download("Groups/" + answer.getString("GroupsStringId" + i) + ".jpg");

                        GroupAndChannel group = new GroupAndChannel(
                                answer.getInt("GroupsId"+i),
                                answer.getString("GroupsStringId"+i),
                                answer.getString("GroupsName"+i),
                                Main.savedPath + "Groups/" + answer.getString("GroupsStringId"+i) + ".jpg",
                                answer.getString("GroupsCreatedTime"+i),
                                answer.getLong("GroupsMembers"+i),
                                "Group"
                        );


                        groups.add(group);
                    }

                    ListController.UpdateList updateList = listController.new UpdateList("Groups", groups);
                    Thread thread = new Thread(updateList);
                    thread.setDaemon(true);
                    thread.start();
                    break;
                }

                case "GetListsAnswerChannels" :
                {
                    ArrayList<GroupAndChannel> channels = new ArrayList<>();

                    for (int i = 0; i < answer.getInt("ChannelsNumbers"); i++)
                    {
                        download("Channels/" + answer.getString("ChannelsStringId" + i) + ".jpg");

                        GroupAndChannel channel = new GroupAndChannel(
                                answer.getInt("ChannelsId"+i),
                                answer.getString("ChannelsStringId"+i),
                                answer.getString("ChannelsName"+i),
                                Main.savedPath + "Channels/" + answer.getString("ChannelsStringId"+i) + ".jpg",
                                answer.getString("ChannelsCreatedTime"+i),
                                answer.getLong("ChannelsMembers"+i),
                                "Channel"
                        );

                        channels.add(channel);
                    }

                    ListController.UpdateList updateList = listController.new UpdateList("Channels", channels);
                    Thread thread = new Thread(updateList);
                    thread.setDaemon(true);
                    thread.start();
                    break;
                }

                case "SendMessageAnswer":
                {
                    if (answer.getString("Result").equals("OK"))
                    {
                        String attachUrl = "", editedAt = "";

                        if (answer.has("attachUrl"))
                        {
                            attachUrl = answer.getString("attachUrl");
                        }

                        if (answer.has("editedAt"))
                        {
                            editedAt = answer.getString("editedAt");
                        }


                        Message newMessage = new Message(
                                answer.getInt("ID"),
                                answer.getInt("conversationId"),
                                answer.getInt("senderId"),
                                attachUrl+"\n"+answer.getString("message"),
                                attachUrl,
                                answer.getString("createdAt"),
                                editedAt
                        );

                        ChatController.Drawer drawer = chatController.new Drawer(newMessage, "SEND");
                        Thread thread = new Thread(drawer);
                        thread.setDaemon(true);
                        thread.start();
                    }


                    else
                        System.out.println(answer.getString("Result"));
                    break;
                }

                case "ReceiveMessageAnswer":
                {
                    if (answer.getString("Result").equals("OK"))
                    {
                        String attachUrl = "", editedAt = "";

                        if (answer.has("attachUrl"))
                        {
                            attachUrl = answer.getString("attachUrl");

                            Downloader downloader = new Downloader(answer.getString("attachUrl"));
                            Thread thread = new Thread(downloader);
                            thread.setDaemon(true);
                            thread.start();
                        }

                        if (answer.has("editedAt"))
                        {
                            editedAt = answer.getString("editedAt");
                        }


                        Message newMessage = new Message(
                                answer.getInt("ID"),
                                answer.getInt("conversationId"),
                                answer.getInt("senderId"),
                                attachUrl+"\n"+answer.getString("message"),
                                attachUrl,
                                answer.getString("createdAt"),
                                editedAt
                        );

                        ChatController.Drawer drawer = chatController.new Drawer(newMessage, "RECEIVE");
                        Thread thread = new Thread(drawer);
                        thread.setDaemon(true);
                        thread.start();
                    }

                    else
                        System.out.println(answer.getString("Result"));
                    break;
                }

                case "GetOldMessagesAnswer" :
                {
                    if (answer.getString("Result").equals("OK"))
                    {
                        ArrayList<Message> messages = new ArrayList<>();

                        for (int i = 0; i < answer.getInt("Numbers"); i++)
                        {

                            String attachUrl = "", editedAt = "";

                            if (answer.has("attachUrl"+i))
                            {
                                attachUrl = answer.getString("attachUrl"+i);

                                Downloader downloader = new Downloader(answer.getString("attachUrl"+i));
                                Thread thread = new Thread(downloader);
                                thread.setDaemon(true);
                                thread.start();
                            }

                            if (answer.has("editedAt"+i))
                            {
                                editedAt = answer.getString("editedAt"+i);
                            }


                            Message newMessage = new Message(
                                    answer.getInt("ID"+i),
                                    answer.getInt("conversationId"+i),
                                    answer.getInt("senderId"+i),
                                    attachUrl+"\n"+answer.getString("message"+i),
                                    attachUrl,
                                    answer.getString("createdAt"+i),
                                    editedAt
                            );

                            messages.add(newMessage);
                        }

                        ChatController.Drawer drawer = chatController.new Drawer(messages, "OldMessage");
                        Thread thread = new Thread(drawer);
                        thread.setDaemon(true);
                        thread.start();
                    }

                    else
                        System.out.println(answer.getString("Result"));
                    break;
                }

                case "ReceiveRequest":
                {
                    Notifications notifications = Notifications.create()
                            .title("Request")
                            .text("You Have New Request")
                            .hideAfter(Duration.seconds(5))
                            .position(Pos.BOTTOM_RIGHT);

                    notifications.show();
                    break;
                }

                case "SignUpResult":
                {
                    SignUpController.ShowResult showResult = signUpController.new ShowResult(answer);
                    Thread thread = new Thread(showResult);
                    thread.setDaemon(true);
                    thread.start();
                    break;
                }

                case "LogInResult":
                {
                    try
                    {
                        LogInController.ToastResult toastResult = logInController.new ToastResult(answer);
                        Thread thread = new Thread(toastResult);
                        thread.setDaemon(true);
                        thread.start();
                    }

                    catch (NullPointerException e)
                    {
                        //TODO
                    }
                    break;
                }

                case "CreateChannelResult" :
                {
                    if (answer.getString("Result").equals("OK"))
                    {
                        GroupAndChannel channel = new GroupAndChannel(
                                answer.getInt("ID"),
                        answer.getString("StringId"),
                        answer.getString("Name"),
                        answer.getString("ProfilePhotoUrl"),
                        answer.getString("CreatedAt"),
                                0,
                                "Channel");

                        ChannelController.Result result = channelController.new Result(channel);
                        Thread thread = new Thread(result);
                        thread.setDaemon(true);
                        thread.start();
                    }

                    else
                    {
                        System.out.println(answer.getString("Result"));
                    }

                    break;
                }

                case "CreateGroupResult" :
                {
                    if (answer.getString("Result").equals("OK"))
                    {
                        GroupAndChannel group = new GroupAndChannel(
                                answer.getInt("ID"),
                                answer.getString("StringId"),
                                answer.getString("Name"),
                                answer.getString("ProfilePhotoUrl"),
                                answer.getString("CreatedAt"),
                                0,
                                "Group");

                        GroupController.Result result = groupController.new Result(group);
                        Thread thread = new Thread(result);
                        thread.setDaemon(true);
                        thread.start();
                    }

                    else
                    {
                        System.out.println(answer.getString("Result"));
                    }

                    break;
                }

                case "GetJoinsAnswer" :
                {
                    ArrayList<User> users = new ArrayList<>();
                    ArrayList<GroupAndChannel> groupAndChannels = new ArrayList<>();

                    if (answer.getString("Result").equals("OK"))
                    {

                        for (int i = 0; i < answer.getInt("1UsersNumbers"); i++)
                        {
                            download("Profiles/" + answer.getString("1UsersUserName" + i) + ".jpg");

                            User user = new User(
                                    answer.getInt("1UsersID" + i),
                                    answer.getString("1UsersUserName" + i),
                                    answer.getString("1UsersName" + i),
                                    Main.savedPath + "Profiles/" + answer.getString("1UsersUserName" + i) + ".jpg",
                                    answer.getBoolean("1UsersIsOnline" + i)
                            );

                            users.add(user);
                        }

                        for (int i = 0; i < answer.getInt("2UsersNumbers"); i++)
                        {
                            download("Profiles/" + answer.getString("2UsersUserName" + i) + ".jpg");

                            User user = new User(
                                    answer.getInt("2UsersID" + i),
                                    answer.getString("2UsersUserName" + i),
                                    answer.getString("2UsersName" + i),
                                    Main.savedPath + "Profiles/" + answer.getString("2UsersUserName" + i) + ".jpg",
                                    answer.getBoolean("2UsersIsOnline" + i)
                            );

                            users.add(user);
                        }

                        for (int i = 0; i < answer.getInt("GroupsNumbers"); i++)
                        {
                            download("Groups/" + answer.getString("GroupsStringId" + i) + ".jpg");

                            GroupAndChannel group = new GroupAndChannel(
                                    answer.getInt("GroupsId" + i),
                                    answer.getString("GroupsStringId" + i),
                                    answer.getString("GroupsName" + i),
                                    Main.savedPath + "Groups/"+answer.getString("GroupsStringId" + i) + ".jpg",
                                    answer.getString("GroupsCreatedTime" + i),
                                    0,
                                    "Group");

                            groupAndChannels.add(group);

                         }

                        for (int i = 0; i < answer.getInt("ChannelsNumbers"); i++)
                        {

                            download("Channels/" + answer.getString("ChannelsStringId" + i) + ".jpg");

                            GroupAndChannel channel = new GroupAndChannel(
                                    answer.getInt("ChannelsId"+i),
                                    answer.getString("ChannelsStringId"+i),
                                    answer.getString("ChannelsName"+i),
                                    Main.savedPath + "Channels/" + answer.getString("ChannelsStringId"+i) + ".jpg",
                                    answer.getString("ChannelsCreatedTime"+i),
                                    answer.getLong("ChannelsMembers"+i),
                                    "Channel"
                            );
                            groupAndChannels.add(channel);

                        }

                        Controller.MyTask myTask = controller.new MyTask(users, groupAndChannels);
                        Thread thread = new Thread(myTask);
                        thread.setDaemon(true);
                        thread.start();
                    }

                    break;
                }

                case "GetOldMessagesGroupAndChannelAnswer" :
                {
                    if (answer.getString("Result").equals("OK"))
                    {
                        ArrayList<Message> messages = new ArrayList<>();

                        for (int i = 0; i < answer.getInt("Numbers"); i++)
                        {

                            String attachUrl = "", editedAt = "";

                            if (answer.has("attachUrl"+i))
                            {
                                attachUrl = answer.getString("attachUrl"+i);

                                Downloader downloader = new Downloader(answer.getString("attachUrl"+i));
                                Thread thread = new Thread(downloader);
                                thread.setDaemon(true);
                                thread.start();
                            }

                            if (answer.has("editedAt"+i))
                            {
                                editedAt = answer.getString("editedAt"+i);
                            }


                            Message newMessage = new Message(
                                    answer.getInt("ID"+i),
                                    answer.getInt("conversationId"+i),
                                    answer.getInt("senderId"+i),
                                    attachUrl+"\n"+answer.getString("message"+i),
                                    attachUrl,
                                    answer.getString("createdAt"+i),
                                    editedAt
                            );

                            messages.add(newMessage);
                        }

                        ChatController.Drawer drawer = chatController.new Drawer(messages, "OldMessage");
                        Thread thread = new Thread(drawer);
                        thread.setDaemon(true);
                        thread.start();
                    }

                    break;
                }

            }
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

}
