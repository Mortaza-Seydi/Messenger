package client.controllers;

import client.HandleInput;
import client.Main;
import client.threads.Uploader;
import client.data.GroupAndChannel;
import client.data.Message;
import client.data.User;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatController
{
    private JFXDrawer drawer;
    private StackPane stackPane;

    @FXML private JFXTextArea sendField;
    @FXML private JFXListView<HBox> mainListView;

    @FXML private ImageView imageView;
    @FXML private Label name;
    @FXML private Label status;

    private VBox onlineUser;
    private File file = null;

    public static HashMap<HBox, Message> messageHashMap = new HashMap<>();

    public void CloseMainDrawer()
    {
        drawer.close();
        drawer.setDisable(true);
        mainListView.getItems().clear();
    }

    public void setFirstVariables(JFXDrawer drawer, StackPane stackPane)
    {
        this.drawer = drawer;
        this.stackPane = stackPane;
        mainListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        HandleInput.chatController = this;
    }

    public void listViewClicked(MouseEvent event)
    {
        if (! mainListView.getSelectionModel().isEmpty() && mainListView.getSelectionModel().getSelectedItems().get(0).getChildren().get(3).isVisible())
        {
            JFXPopup popup = new JFXPopup();

            JFXButton accept = new JFXButton("Delete");
            accept.setOnMouseClicked(event1 ->
                                     {
                                         Main.client.manageMessage(messageHashMap.get(mainListView.getSelectionModel().getSelectedItem()), "Delete", null);
                                         mainListView.getSelectionModel().clearSelection();
                                         popup.hide();
                                     });

            JFXButton block = new JFXButton("Edit");
            block.setOnMouseClicked(event1 ->
                                    {
                                        Main.client.manageMessage(
                                                ChatController.messageHashMap.get(mainListView.getSelectionModel().getSelectedItem()), "Edit",
                                                sendField.getText()
                                                                 );

                                        sendField.setText(null);
                                        mainListView.getSelectionModel().clearSelection();
                                        popup.hide();
                                    });


            VBox vBox = new VBox(accept, block);

            popup.setPopupContent(vBox);
            popup.show(
                    mainListView, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, event.getX(),
                    event.getY()
                      );
        }
    }

    public void sendMessage()
    {

        if (ListController.map.containsKey(onlineUser))
            Main.client.sendMessages(sendField.getText(), ListController.map.get(onlineUser));

        else
            Main.client.sendMessages(sendField.getText(), ListController.conversations.get(onlineUser));

        sendField.setText(null);
    }

    public void attach()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("AnyThing", "*.*"));
        file = fileChooser.showOpenDialog(null);
    }

    public void openDrawer(VBox vBox, boolean flag)
    {
        onlineUser = vBox;
        try
        {
            if (flag)
            {
                User user = ListController.map.get(vBox);
                imageView.setImage(new Image(new FileInputStream(user.getProfilePhotoUrl())));
                imageView.setClip(new Circle(30, 30, 30));
                name.setText(user.getName());
                status.setText(user.isOnline() ? "Online" : "Offline");

                Main.client.getOldMessages(user);
            }
            else
            {
                GroupAndChannel groupAndChannel = ListController.conversations.get(vBox);

                imageView.setImage(new Image(new FileInputStream(groupAndChannel.getProfilePhotoUrl())));
                imageView.setClip(new Circle(30, 30, 30));
                name.setText(groupAndChannel.getName());
                status.setText(String.format("%d Members", groupAndChannel.getMembers()));

                Main.client.getOldMessages(groupAndChannel);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        drawer.setDisable(false);
        drawer.open();
    }

    public class Drawer extends Task<Void>
    {
        private Message message;
        private String  doWhat;
        private ArrayList<Message> messages;

        public Drawer(Message message, String doWhat)
        {
            this.message = message;
            this.doWhat = doWhat;
        }

        public Drawer(ArrayList<Message> messages, String doWhat)
        {
            this.messages = messages;
            this.doWhat = doWhat;
        }

        @Override protected Void call()
        {
            try
            {
                switch (doWhat)
                {
                    case "SEND":
                    {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(Main.class.getResource(Main.assets + "FXML/Chat/Show.fxml"));
                        HBox hBox = fxmlLoader.load();

                        ((TextArea) (((VBox) (hBox.getChildren().get(2))).getChildren().get(0))).setText(
                                message.getMessage());

                        (hBox.getChildren().get(0)).setVisible(false);
                        (hBox.getChildren().get(1)).setVisible(false);

                        ImageView imageView = (ImageView) (hBox.getChildren().get(3));
                        imageView.setImage(new Image(new FileInputStream(Main.mySelf.getProfilePhotoUrl())));
                        imageView.setClip(new Circle(20, 20, 20));

                        Platform.runLater(() -> mainListView.getItems().add(hBox));

                        ChatController.messageHashMap.put(hBox, message);

                        if (file != null)
                        {
                            Uploader uploader = new Uploader(
                                    file, "Files/" + message.getId() + file.getPath().substring(
                                    file.getPath().indexOf('.'), file.getPath().length()));
                            Thread thread = new Thread(uploader);
                            thread.setDaemon(true);
                            thread.start();

                            Main.client.manageMessage(
                                    message, "Attach", message.getId() + file.getPath().substring(
                                            file.getPath().indexOf('.'), file.getPath().length()));
                        }

                        break;
                    }

                    case "RECEIVE":
                    {

                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(Main.class.getResource(Main.assets + "FXML/Chat/Show.fxml"));
                        HBox hBox = fxmlLoader.load();

                        ((TextArea) (((VBox) (hBox.getChildren().get(1))).getChildren().get(0))).setText(
                                message.getMessage());

                        (hBox.getChildren().get(2)).setVisible(false);
                        (hBox.getChildren().get(3)).setVisible(false);

                        ImageView imageView = ((ImageView) (hBox.getChildren().get(0)));
                        try
                        {
                            imageView.setImage(new Image(
                                    new FileInputStream(ListController.map.get(onlineUser).getProfilePhotoUrl())));

                        }
                        catch (NullPointerException e)
                        {
                            imageView.setImage(new Image(
                                    new FileInputStream(ListController.conversations.get(onlineUser).getProfilePhotoUrl())));

                        }

                        imageView.setClip(new Circle(20, 20, 20));

                        Platform.runLater(() -> mainListView.getItems().add(hBox));

                        Notifications notifications = Notifications.create()
                                .title("New Message")
                                .text(message.getMessage())
                                .hideAfter(Duration.seconds(5))
                                .position(Pos.BOTTOM_RIGHT);

                        Platform.runLater(notifications::show);

                        ChatController.messageHashMap.put(hBox, message);

                        break;
                    }

                    case "OldMessage":
                        ArrayList<HBox> hBoxes = new ArrayList<>();

                        for (Message message : messages)
                        {
                            if (message.getSenderId() == Main.mySelf.getId())
                            {
                                FXMLLoader fxmlLoader = new FXMLLoader();
                                fxmlLoader.setLocation(Main.class.getResource(Main.assets + "FXML/Chat/Show.fxml"));
                                HBox hBox = fxmlLoader.load();

                                ((TextArea) (((VBox) (hBox.getChildren().get(2))).getChildren().get(0))).setText(
                                        message.getMessage());

                                (hBox.getChildren().get(0)).setVisible(false);
                                (hBox.getChildren().get(1)).setVisible(false);

                                ImageView imageView = ((ImageView) (hBox.getChildren().get(3)));
                                try
                                {
                                    imageView.setImage(new Image(new FileInputStream(Main.mySelf.getProfilePhotoUrl())));
                                }
                                catch (IOException e)
                                {

                                }

                                imageView.setClip(new Circle(20, 20, 20));

                                hBoxes.add(hBox);
                                ChatController.messageHashMap.put(hBox, message);
                            }

                            else
                            {
                                FXMLLoader fxmlLoader = new FXMLLoader();
                                fxmlLoader.setLocation(Main.class.getResource(Main.assets + "FXML/Chat/Show.fxml"));
                                HBox hBox = fxmlLoader.load();

                                ((TextArea) (((VBox) (hBox.getChildren().get(1))).getChildren().get(0))).setText(
                                        message.getMessage());

                                (hBox.getChildren().get(2)).setVisible(false);
                                (hBox.getChildren().get(3)).setVisible(false);

                                ImageView imageView = ((ImageView) (hBox.getChildren().get(0)));
                                try
                                {
                                    imageView.setImage(new Image(new FileInputStream(ListController.conversations.get(onlineUser).getProfilePhotoUrl())));
                                }
                                catch (NullPointerException e)
                                {
                                    imageView.setImage(new Image(new FileInputStream(ListController.map.get(onlineUser).getProfilePhotoUrl())));

                                }
                                imageView.setClip(new Circle(20, 20, 20));

                                hBoxes.add(hBox);
                                ChatController.messageHashMap.put(hBox, message);
                            }
                        }

                        Platform.runLater(() -> mainListView.getItems().addAll(hBoxes));
                        break;
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }

            return  null;
        }
    }
}