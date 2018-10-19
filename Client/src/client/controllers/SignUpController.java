package client.controllers;

import client.Database;
import client.HandleInput;
import client.Main;
import client.threads.Uploader;
import client.data.SuperUser;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SignUpController
{
    private Stage stage;
    private Scene logInScene;
    private Scene uiScene;
    private File file;

    @FXML private ImageView imageView;

    @FXML private JFXTextField phoneNumber;
    @FXML private JFXTextField name;
    @FXML private JFXTextField username;
    @FXML private JFXPasswordField password;

    public void setFirstVariables(Stage stage, Scene logInScene, Scene uiScene)
    {
        this.stage = stage;
        this.logInScene = logInScene;
        this.uiScene = uiScene;
        HandleInput.signUpController = this;
    }

    public void showLogIn()
    {
        stage.setScene(logInScene);
    }

    public void signUp()
    {
        try
        {
            if (file.exists())
            {
                HandleInput.signUpController = this;
                Main.client.signUp(phoneNumber.getText(), name.getText(), username.getText(), password.getText());
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void uploadPhoto()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Pictures", "*.jpg"));
        file = fileChooser.showOpenDialog(stage);

        if (file != null)
        {
            try
            {
                Image image = new Image(new FileInputStream(file.getAbsolutePath()));
                imageView.setImage(image);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    public class ShowResult implements Runnable
    {
        JSONObject result;

        public ShowResult(JSONObject result)
        {
            this.result = result;
        }

        @Override public void run()
        {
            try
            {
                if (result.getString("Result").equals("OK"))
                {
                    Uploader uploader = new Uploader(file, Main.savedPath + "Profiles/"+result.getString("username")+".jpg");
                    uploader.run();

                    SuperUser mySelf = new SuperUser(
                            result.getInt("id"),
                            result.getString("phone"),
                            result.getString("username"),
                            result.getString("password"),
                            result.getString("name"),
                            result.getString("profilePhotoUrl"),
                            result.getBoolean("IsOnline"),
                            result.getBoolean("isReported"),
                            result.getString("createdTime"),
                            result.getString("createdTime")
                    );

                    Database.login(mySelf);

                    Platform.runLater(() -> stage.setScene(uiScene));
                }
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
}
