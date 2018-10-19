package client.threads;

import client.Main;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class Downloader implements Runnable
{
    private String fileName;

    public Downloader(String fileName)
    {
        this.fileName = fileName;
    }

    @Override public void run()
    {
        try
        {
            JSONObject object = new JSONObject();
            object.put("DO", "Download");
            object.put("Name", fileName);

            File file = new File(Main.savedPath + fileName);

            if (!file.exists())
            {
                Socket socket = new Socket("127.0.0.1", 2021);
                DataInputStream socketInput  = new DataInputStream(socket.getInputStream());
                DataOutputStream socketOutput = new DataOutputStream(socket.getOutputStream());

                socketOutput.writeUTF(object.toString());
                socketOutput.flush();

                DataOutputStream outputStream = new DataOutputStream(
                        new FileOutputStream(Main.savedPath + fileName));

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = socketInput.read(buffer)) != - 1)
                {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();

                socketInput.close();
                socketOutput.close();
                socket.close();
            }
        }

        catch (IOException | JSONException e)
        {
            e.printStackTrace();
        }

    }
}
