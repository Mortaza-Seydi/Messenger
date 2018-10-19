package client.controllers;

import client.Main;
import client.threads.Downloader;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DrawerController
{

    @FXML private Label name;
    @FXML private ImageView profilePhoto;

    public Controller controller;

    private JFXDialog dialog;
    private JFXDialogLayout layout;

    private VBox vBox;
    private VBox vBox1;
    private VBox vBox2;
    private ListController listController;
    private ChannelController channelController;
    private GroupController groupController;

    public void setFirstVariables(Controller controller, StackPane stackPane)
    {
        this.controller = controller;

        try
        {
            layout = new JFXDialogLayout();

            dialog = new JFXDialog(stackPane, layout, JFXDialog.DialogTransition.CENTER);

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getResource(Main.assets + "FXML/Drawer/Group.fxml"));
            vBox = fxmlLoader.load();

            FXMLLoader fxmlLoader2 = new FXMLLoader();
            fxmlLoader2.setLocation(Main.class.getResource(Main.assets + "FXML/Drawer/Channel.fxml"));
            vBox2 = fxmlLoader2.load();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource(Main.assets + "FXML/Drawer/List.fxml"));
            vBox1 = loader.load();

            listController = loader.getController();
            listController.setFirstVariable(this, dialog, stackPane);

            groupController = fxmlLoader.getController();
            groupController.setFirstVariable(dialog, listController);

            channelController = fxmlLoader2.getController();
            channelController.setFirstVariable(dialog, listController);

            if (Main.mySelf != null)
            {
                Downloader downloader = new Downloader("Profiles/" + Main.mySelf.getUserName() + ".jpg");
                downloader.run();

                name.setText(Main.mySelf.getName());
                profilePhoto.setImage(new Image(new FileInputStream(Main.savedPath + "Profiles/"+Main.mySelf.getUserName()+".jpg")));
                profilePhoto.setClip(new Circle(60,60,60));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void update()
    {
        try
        {

            if (Main.mySelf != null)
            {
                if (! new File("Profiles/" + Main.mySelf.getUserName() + ".jpg").exists())
                {
                    Downloader downloader = new Downloader("Profiles/" + Main.mySelf.getUserName() + ".jpg");
                    downloader.run();
                }

                name.setText(Main.mySelf.getName());
                profilePhoto.setImage(
                        new Image(new FileInputStream(Main.savedPath + "Profiles/" + Main.mySelf.getUserName() + ".jpg")));
                profilePhoto.setClip(new Circle(60, 60, 60));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void newGroup()
    {
        layout.setActions(vBox);
        dialog.show();
        controller.hamburgerPressed();
        groupController.update();
        controller.chatController.CloseMainDrawer();
    }

    public void list()
    {
        layout.setActions(vBox1);
        dialog.show();
        controller.hamburgerPressed();
        listController.send();
        controller.chatController.CloseMainDrawer();
    }

    public void newChannel()
    {
        layout.setActions(vBox2);
        dialog.show();
        controller.hamburgerPressed();
        channelController.update();
        controller.chatController.CloseMainDrawer();
    }
}
