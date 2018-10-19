package client.controllers;

import client.HandleInput;
import client.Main;
import client.data.GroupAndChannel;
import client.data.User;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Controller
{

    @FXML public JFXListView<VBox> listView;

    @FXML private JFXHamburger hamburger;
    private HamburgerSlideCloseTransition hamburger2;

    @FXML private JFXDrawer drawer;
    @FXML private JFXDrawer mainDrawer;
    @FXML private StackPane stackPane;

    public ChatController chatController;
    private DrawerController drawerController;

    public void initialize()
    {
        try
        {
            FXMLLoader chatLoader = new FXMLLoader();
            chatLoader.setLocation(Main.class.getResource(Main.assets + "FXML/Chat/Chat.fxml"));
            VBox vBox1 = chatLoader.load();
            mainDrawer.setSidePane(vBox1);

            chatController = chatLoader.getController();
            chatController.setFirstVariables(mainDrawer, stackPane);

            FXMLLoader drawerLoader = new FXMLLoader();
            drawerLoader.setLocation(Main.class.getResource(Main.assets + "FXML/Drawer/Drawer.fxml"));
            VBox vBox = drawerLoader.load();
            drawer.setSidePane(vBox);
            drawer.setDisable(true);

            drawerController = drawerLoader.getController();
            drawerController.setFirstVariables(this, stackPane);

            hamburger2 = new HamburgerSlideCloseTransition(hamburger);
            hamburger2.setRate(- 1);

            if (Main.client != null)
            {
                Main.client.getJoins();
                HandleInput.controller = this;
            }
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void hamburgerPressed()
    {
        hamburger2.setRate(hamburger2.getRate() * - 1);
        hamburger2.play();

        if (drawer.isOpened())
        {
            drawer.close();
            drawer.setDisable(true);
        }
        else
        {
            drawer.setDisable(false);
            drawer.open();
        }

        drawerController.update();
    }

    public void listViewClicked(MouseEvent event)
    {
        if (! listView.getSelectionModel().isEmpty())
        {
            VBox vBox = listView.getSelectionModel().getSelectedItem();

            chatController.CloseMainDrawer();

            if (event.getButton() == MouseButton.PRIMARY)
            {
                if (ListController.conversations.containsKey(vBox))
                    chatController.openDrawer(listView.getSelectionModel().getSelectedItem(), false);

                else if (ListController.map.containsKey(vBox))
                    chatController.openDrawer(listView.getSelectionModel().getSelectedItem(), true);
            }
        }
    }

    public class MyTask implements Runnable
    {
        private ArrayList<User> users;
        private ArrayList<GroupAndChannel> groupAndChannels;
        private VBox[] groupVBoxes;
        private VBox[] userVBoxes;

        public MyTask(ArrayList<User> users, ArrayList<GroupAndChannel> groupAndChannels)
        {
            this.users = users;
            this.groupAndChannels = groupAndChannels;

            groupVBoxes = new VBox[groupAndChannels.size()];
            userVBoxes = new VBox[users.size()];
        }

        @Override public void run()
        {
            try
            {
                for (int i = 0; i < users.size(); i++)
                {
                    FXMLLoader listLoader = new FXMLLoader();
                    listLoader.setLocation(Main.class.getResource(Main.assets + "FXML/List.fxml"));
                    userVBoxes[i] = listLoader.load();

                    ((Label) (((VBox) (((HBox) (userVBoxes[i].getChildren().get(0))).getChildren().get(2))).getChildren().get(0))).setText(users.get(i).getName());
                    ((Label) (((VBox) (((HBox) (userVBoxes[i].getChildren().get(0))).getChildren().get(2))).getChildren().get(1))).setText(users.get(i).getUserName());

                    ImageView imageView = ((ImageView) (((HBox) (userVBoxes[i].getChildren().get(0))).getChildren().get(0)));
                    imageView.setImage(new Image(new FileInputStream(users.get(i).getProfilePhotoUrl())));
                    imageView.setClip(new Circle(40, 40, 40));

                    ListController.map.put(userVBoxes[i], users.get(i));

                }

                Platform.runLater(()-> listView.getItems().addAll(userVBoxes));

                for (int i = 0; i < groupAndChannels.size(); i++)
                {

                    FXMLLoader listLoader = new FXMLLoader();
                    listLoader.setLocation(Main.class.getResource(Main.assets + "FXML/List.fxml"));
                    groupVBoxes[i] = listLoader.load();

                    ((Label) (((VBox) (((HBox) (groupVBoxes[i].getChildren().get(0))).getChildren().get(2))).getChildren().get(0))).setText(groupAndChannels.get(i).getName());
                    ((Label) (((VBox) (((HBox) (groupVBoxes[i].getChildren().get(0))).getChildren().get(2))).getChildren().get(1))).setText(groupAndChannels.get(i).getStringId());

                    ImageView imageView = ((ImageView) (((HBox) (groupVBoxes[i].getChildren().get(0))).getChildren().get(0)));
                    imageView.setImage(new Image(new FileInputStream(groupAndChannels.get(i).getProfilePhotoUrl())));
                    imageView.setClip(new Circle(40, 40, 40));

                    ListController.conversations.put(groupVBoxes[i], groupAndChannels.get(i));

                }

                Platform.runLater(()-> listView.getItems().addAll(groupVBoxes));

            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
