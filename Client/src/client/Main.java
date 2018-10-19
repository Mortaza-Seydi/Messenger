package client;

import client.threads.Reader;
import client.controllers.LogInController;
import client.controllers.SignUpController;
import client.data.SuperUser;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class Main extends Application
{
    public static Client client;
    public static SuperUser mySelf;

    public static final String savedPath = "UserFiles/";
    public static final String assets = "assets/";

    public static void main(String[] args)
    {
        try
        {
            mySelf = Database.isLoggedIn();

            Socket socket = new Socket("127.0.0.1", 2020);
            DataInputStream  inputStream  = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            client = new Client(outputStream);

            Reader reader = new Reader(inputStream);
            Thread thread = new Thread(reader);
            thread.setDaemon(true);
            thread.start();

            if (mySelf != null)
            {
                JSONObject object = new JSONObject();
                object.put("DO", "LogIn");
                object.put("phoneNumber", mySelf.getPhone());
                object.put("password", mySelf.getPassword());

                outputStream.writeUTF(object.toString());
                outputStream.flush();
            }

            launch(args);

        }
        catch (IOException | JSONException e)
        {
            client = new Client();
            launch(args);
        }
    }

    @Override public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader logInLoader = new FXMLLoader();
        logInLoader.setLocation(Main.class.getResource(assets + "FXML/LogIn.fxml"));
        Scene logInScene = new Scene(logInLoader.load());

        FXMLLoader signUpLoader = new FXMLLoader();
        signUpLoader.setLocation(Main.class.getResource(assets + "FXML/SignUp.fxml"));
        Scene signUpScene = new Scene(signUpLoader.load());

        FXMLLoader uiLoader = new FXMLLoader();
        uiLoader.setLocation(Main.class.getResource(assets + "FXML/UI.fxml"));
        Scene uiScene = new Scene(uiLoader.load());

        LogInController logInController = logInLoader.getController();
        logInController.setFirstVariables(primaryStage, signUpScene, uiScene);

        SignUpController signUpController = signUpLoader.getController();
        signUpController.setFirstVariables(primaryStage, logInScene, uiScene);

        Image image = new Image((Main.class.getResource(assets + "Pictures/icon.png")).toString());
        primaryStage.getIcons().add(image);

        primaryStage.setTitle("Messenger");
        primaryStage.setResizable(false);

        if (mySelf == null)
        {
            primaryStage.setScene(logInScene);
        }
        else
        {
            primaryStage.setScene(uiScene);
        }

        primaryStage.show();
    }
}
