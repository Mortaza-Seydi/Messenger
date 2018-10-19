package server.fileServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServerMain implements Runnable
{
    @Override public void run()
    {
        ServerSocket serverSocket;

        try
        {
            serverSocket = new ServerSocket(2021);

            while (true)
            {
                try
                {
                    Socket socket = serverSocket.accept();

                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                    Thread thread = new Thread(new ManageFileConnections(socket, inputStream, outputStream));
                    thread.setDaemon(true);
                    thread.start();
                }

                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
