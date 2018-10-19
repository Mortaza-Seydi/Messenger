package client.threads;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

public class Uploader implements Runnable
{
    private File file;
    private String fileName;

    public Uploader(File file, String fileName)
    {
        this.file = file;
        this.fileName = fileName;
    }

    @Override public void run()
    {
        try
        {
            JSONObject object = new JSONObject();
            object.put("DO", "Upload");
            object.put("Name", fileName);

            if (file.exists())
            {
                Socket socket = new Socket("127.0.0.1", 2021);
                DataInputStream socketInput = new DataInputStream(socket.getInputStream());
                DataOutputStream socketOutput = new DataOutputStream(socket.getOutputStream());

                socketOutput.writeUTF(object.toString());
                socketOutput.flush();

                DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = dataInputStream.read(buffer)) != - 1)
                {
                    socketOutput.write(buffer, 0, bytesRead);
                }

                socketInput.close();
                socketOutput.close();
                socket.close();
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}