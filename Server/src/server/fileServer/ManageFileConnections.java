package server.fileServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class ManageFileConnections implements Runnable
{
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private static final String savedPath = "ServerFiles/";

    public ManageFileConnections(Socket socket, DataInputStream inputStream, DataOutputStream outputStream)
    {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override public void run()
    {
        try
        {
            JSONObject object = new JSONObject(inputStream.readUTF());

            String doWhat = object.getString("DO") ;

            if (doWhat.equals("Download"))
            {
                String fileName = object.getString("Name");
                download(savedPath + fileName);
            }
            else if (doWhat.equals("Upload"))
            {
                String fileName = object.getString("Name");
                upload(savedPath + fileName);
            }
        }
        catch (IOException | JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void upload(String fileName) throws IOException
    {
        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(fileName));

        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != - 1)
        {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();

        inputStream.close();
        outputStream.close();
        socket.close();
    }

    public void download(String fileName) throws IOException
    {
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(fileName));

        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = dataInputStream.read(buffer)) != - 1)
        {
            outputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outputStream.close();
        socket.close();
    }
}