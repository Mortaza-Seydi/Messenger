package client;

import client.data.SuperUser;

import java.io.*;

public class Database
{
    private static final String fileName = "information2.messenger";

    public static void login(SuperUser mySelf)
    {
        try
        {
            File file = new File(Main.savedPath + fileName);

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

            dataOutputStream.writeUTF(mySelf.getId() + "\n" + mySelf.getPhone() + "\n" + mySelf.getUserName()
                                              + "\n" + mySelf.getPassword() + "\n" + mySelf.getName()
                                              + "\n" + mySelf.getProfilePhotoUrl() + "\n" + mySelf.isOnline()
                                              + "\n" + mySelf.isReported() + "\n" + mySelf.getCreatedTime()
                                              + "\n" + mySelf.getLastSeen());

            dataOutputStream.flush();

            dataOutputStream.close();
            fileOutputStream.close();

            Main.mySelf = mySelf;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static SuperUser isLoggedIn()
    {
        try
        {
            File file = new File(Main.savedPath + fileName);

            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);

            String a = dataInputStream.readUTF();

            dataInputStream.close();
            fileInputStream.close();

            String[] b = a.split("\n");

            int id = Integer.parseInt(b[0]);
            String phone = b[1];
            String userName = b[2];
            String password = b[3];
            String name = b[4];
            String profilePhotoUrl = b[5];
            boolean isOnline = Boolean.parseBoolean(b[6]);
            boolean isReported = Boolean.parseBoolean(b[7]);
            String createdTime = b[8];
            String lastSeen = b[9];

            return new SuperUser(id, phone, userName, password, name, profilePhotoUrl, isOnline, isReported, createdTime, lastSeen);
        }

        catch (IOException e)
        {
            return null;
        }
    }

}
