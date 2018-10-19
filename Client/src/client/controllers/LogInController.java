package client.controllers;

import client.Database;
import client.HandleInput;
import client.Main;
import client.data.SuperUser;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.json.JSONObject;

public class LogInController
{
    private Stage stage;
    private Scene signUpScene;
    private Scene uiScene;

    @FXML private JFXPasswordField password;
    @FXML private JFXTextField phoneNumber;
    @FXML private AnchorPane anchorPane;
    private JFXSnackbar snackBar;

    public void setFirstVariables(Stage stage, Scene signUpScene, Scene uiScene)
    {
        this.stage = stage;
        this.signUpScene = signUpScene;
        this.uiScene = uiScene;
        HandleInput.logInController = this;
        snackBar = new JFXSnackbar(anchorPane);
    }

    public void showSignUp()
    {
        stage.setScene(signUpScene);
    }

    public void logIn()
    {
        try
        {
            Main.client.logIn(phoneNumber.getText(), password.getText());
        }
        catch (Exception e)
        {
            snackBar.show(e.getMessage(), 2000);
        }
    }

    public class ToastResult extends Task<Void>
    {
        private JSONObject message;

        public ToastResult(JSONObject message)
        {
            this.message = message;
        }

        @Override protected Void call()
        {
            try
            {
                String result = message.getString("Result");

                if (result.equals("OK"))
                {
                    SuperUser mySelf = new SuperUser(
                            message.getInt("id"),
                            message.getString("phone"),
                            message.getString("username"),
                            message.getString("password"),
                            message.getString("name"),
                            message.getString("profilePhotoUrl"),
                            message.getBoolean("IsOnline"),
                            message.getBoolean("isReported"),
                            message.getString("createdTime"),
                            message.getString("createdTime")
                    );

                    Database.login(mySelf);

                    Platform.runLater(() -> stage.setScene(uiScene));
                }
                else
                {
                    Platform.runLater(() -> snackBar.show(result, 2000));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }
}
