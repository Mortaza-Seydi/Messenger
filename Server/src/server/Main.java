package server;

import server.database.Database;
import server.fileServer.FileServerMain;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class Main
{
    public static HashMap<Integer, Socket> onlineUsers = new HashMap<>();

    public static void main(String[] args)
    {

        final String url = "jdbc:mysql://localhost:3306/messenger?useSSL=false";
        final String user = "root";
        final String password = "1234";

        try
        {
            ServerSocket serverSocket = new ServerSocket(2020);

            Connection connection = DriverManager.getConnection(url, user, password);
            Statement  statement  = connection.createStatement();

            Thread fileServer = new Thread(new FileServerMain());
            fileServer.setDaemon(true);
            fileServer.start();

            while (true)
            {
                try
                {
                    Socket socket = serverSocket.accept();

                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                    Thread thread = new Thread(new ManageConnections(socket, inputStream, outputStream, new Database(statement)));
                    thread.setDaemon(true);
                    thread.start();
                }

                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        catch (SQLException | IOException e)
        {
            e.printStackTrace();
        }
    }
}