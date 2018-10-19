package client.controllers;

import client.HandleInput;
import client.Main;
import client.threads.Uploader;
import client.data.GroupAndChannel;
import client.data.User;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ChannelController
{
    @FXML private ImageView channelPhoto;
    @FXML private JFXTextField channelName;
    @FXML private JFXTextField channelId;
    @FXML private JFXListView<VBox> listView;

    private JFXDialog dialog;
    private ListController listController;
    private File file;
    private ArrayList<User> users = new ArrayList<>();

    public void setFirstVariable(JFXDialog dialog, ListController listController)
    {
        this.dialog = dialog;
        this.listController = listController;
        HandleInput.channelController = this;
    }

    public void update()
    {
        listView.getItems().addAll(listController.contactsListView.getItems());
    }

    public void closeChannelDialog()
    {
        if (dialog.isOverlayClose())
        {
            dialog.close();
            listView.getItems().clear();
        }
    }

    public void selectChannelPhoto()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Pictures", "*.jpg"));
        file = fileChooser.showOpenDialog(null);

        if (file != null)
        {
            try
            {
                Image image = new Image(new FileInputStream(file));

                channelPhoto.setImage(image);
                channelPhoto.setClip(new Circle(50, 50, 50));
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void addUsers()
    {
        VBox vBox = listView.getSelectionModel().getSelectedItem();
        users.add(ListController.map.get(vBox));
        listView.getItems().remove(vBox);
    }

    public void createChannel()
    {
        if (!channelName.getText().equals("") && !channelId.getText().equals("") && file != null)
        {
            Main.client.createChannel(channelName.getText(), channelId.getText(), users);
        }
    }

    public class Result implements Runnable
    {
        private GroupAndChannel channel;

        public Result(GroupAndChannel channel)
        {
            this.channel = channel;
        }

        @Override public void run()
        {
            try
            {

                Uploader uploader = new Uploader(file, "Channels/" + channel.getStringId() + ".jpg");
                uploader.run();

                dialog.close();

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Main.class.getResource(Main.assets + "FXML/List.fxml"));
                VBox vBox = loader.load();

                ((Label) (((VBox) (((HBox) (vBox.getChildren().get(0))).getChildren().get(2))).getChildren().get(0))).setText(channel.getName());
                ((Label) (((VBox) (((HBox) (vBox.getChildren().get(0))).getChildren().get(2))).getChildren().get(1))).setText(channel.getStringId());

                ImageView imageView = ((ImageView) (((HBox) (vBox.getChildren().get(0))).getChildren().get(0)));
                imageView.setImage(new Image(new FileInputStream(channel.getProfilePhotoUrl())));
                imageView.setClip(new Circle(40, 40, 40));


                listController.drawerController.controller.listView.getItems().add(vBox);
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

}