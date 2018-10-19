package client.threads;

import client.HandleInput;
import client.Main;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.IOException;

public class Reader implements Runnable
{
    private DataInputStream  inputStream;
    private HandleInput handleInput;

    public Reader(DataInputStream inputStream)
    {
        this.inputStream = inputStream;
        handleInput = new HandleInput();
    }

    @Override public void run()
    {
        while (true)
        {
            try
            {
                String message = inputStream.readUTF();
                System.out.println(message);
                Platform.runLater(()->HandleInput.handle(message));
            }
            catch (IOException e)
            {
                e.printStackTrace();
                break;
            }
        }
    }
}
