package client.controllers;

import client.HandleInput;
import client.Main;
import client.data.GroupAndChannel;
import client.data.User;
import com.jfoenix.controls.*;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ListController
{
    private JFXDialog dialog;
    private JFXDialog dialog2;
    private StackPane stackPane;
    public DrawerController drawerController;

    @FXML private JFXListView<VBox> sendRequestListView;
    @FXML private JFXListView<VBox> receiveRequestListView;
    @FXML public JFXListView<VBox> contactsListView;
    @FXML private JFXListView<VBox> blockedListView;
    @FXML private JFXListView<VBox> allUsersListView;
    @FXML private JFXListView<VBox> channelsListView;
    @FXML private JFXListView<VBox> groupsListView;

    public static HashMap<VBox, User> map = new HashMap<>();
    public static HashMap<VBox, GroupAndChannel> conversations = new HashMap<>();

    public void setFirstVariable(DrawerController drawerController, JFXDialog dialog, StackPane stackPane)
    {
        this.drawerController = drawerController;
        this.dialog = dialog;
        this.stackPane = stackPane;
        HandleInput.listController = this;
    }

    public void updateList(String doWhat)
    {
        Main.client.getUsersList(doWhat);
    }

    public void closeDialog()
    {
        if (dialog.isOverlayClose())
        {
            dialog.close();
        }
    }

    public void onAllUsersListViewClicked()
    {
        if (! allUsersListView.getSelectionModel().isEmpty())
        {
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Text("Request"));
            layout.setBody(new Text("Are You Sure To Send Request To This User ???"));

            JFXButton button = new JFXButton("OK");
            button.setOnMouseClicked(e ->
                                     {
                                         Main.client.sendRequest(
                                                 map.get(allUsersListView.getSelectionModel().getSelectedItem()));
                                         allUsersListView.getSelectionModel().clearSelection();
                                         dialog2.close();
                                     });

            layout.setActions(button);
            dialog2 = new JFXDialog(stackPane, layout, JFXDialog.DialogTransition.CENTER);

            dialog2.show();
        }
    }

    public void onBlockedListViewClicked()
    {
        if (! blockedListView.getSelectionModel().isEmpty())
        {
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Text("UnBlock"));
            layout.setBody(new Text("Are You Sure You Want To Unblock This User ???"));

            JFXButton button = new JFXButton("OK");
            button.setOnMouseClicked(e ->
                                     {
                                         Main.client.unblock(
                                                 map.get(blockedListView.getSelectionModel().getSelectedItem()));
                                         blockedListView.getSelectionModel().clearSelection();
                                         dialog2.close();
                                     });

            layout.setActions(button);
            dialog2 = new JFXDialog(stackPane, layout, JFXDialog.DialogTransition.CENTER);

            dialog2.show();
        }
    }

    public void onChannelsListViewClicked()
    {
        if (! channelsListView.getSelectionModel().isEmpty())
        {
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Text("Join"));
            layout.setBody(new Text("Are You Sure To Join To This Channel ???"));

            JFXButton button = new JFXButton("OK");
            button.setOnMouseClicked(e ->
                                     {
                                         Main.client.join(
                                                 conversations.get(channelsListView.getSelectionModel().getSelectedItem()), "Channel");
                                         dialog2.close();

                                         VBox vBox = channelsListView.getSelectionModel().getSelectedItem();
                                         drawerController.controller.chatController.openDrawer(vBox, false);
                                         channelsListView.getSelectionModel().clearSelection();
                                         dialog.close();

                                         drawerController.controller.listView.getItems().add(vBox);

                                     });

            layout.setActions(button);
            dialog2 = new JFXDialog(stackPane, layout, JFXDialog.DialogTransition.CENTER);

            dialog2.show();
        }
    }

    public void onContactsListViewClicked()
    {
        if (! contactsListView.getSelectionModel().isEmpty())
        {
            VBox vBox = contactsListView.getSelectionModel().getSelectedItem();
            drawerController.controller.chatController.openDrawer(vBox, true);
            contactsListView.getSelectionModel().clearSelection();
            dialog.close();

            if (! drawerController.controller.listView.getItems().contains(vBox))
            {
                drawerController.controller.listView.getItems().add(vBox);
            }
        }
    }

    public void onGroupsListViewClicked()
    {
        if (! groupsListView.getSelectionModel().isEmpty())
        {
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Text("Join"));
            layout.setBody(new Text("Are You Sure To Send Request To This Group ???"));

            JFXButton button = new JFXButton("OK");
            button.setOnMouseClicked(e ->
                                     {
                                         Main.client.join(
                                                 conversations.get(groupsListView.getSelectionModel().getSelectedItem()), "Group");

                                         dialog2.close();

                                         VBox vBox = groupsListView.getSelectionModel().getSelectedItem();
                                         drawerController.controller.chatController.openDrawer(vBox, false);
                                         groupsListView.getSelectionModel().clearSelection();
                                         dialog.close();

                                         drawerController.controller.listView.getItems().add(vBox);
                                     });

            layout.setActions(button);
            dialog2 = new JFXDialog(stackPane, layout, JFXDialog.DialogTransition.CENTER);

            dialog2.show();
        }
    }

    public void onReceiveRequestListViewClicked(MouseEvent event)
    {
        if (! receiveRequestListView.getSelectionModel().isEmpty())
        {
            JFXPopup popup = new JFXPopup();

            JFXButton accept = new JFXButton("Accept");
            accept.setOnMouseClicked(event1 ->
                                     {
                                         Main.client.manageRequests(
                                                 map.get(receiveRequestListView.getSelectionModel().getSelectedItem()),
                                                 "Accept"
                                                                   );
                                         receiveRequestListView.getSelectionModel().clearSelection();
                                         popup.hide();
                                     });

            JFXButton block = new JFXButton("Block");
            block.setOnMouseClicked(event1 ->
                                    {
                                        Main.client.manageRequests(
                                                map.get(receiveRequestListView.getSelectionModel().getSelectedItem()),
                                                "Block"
                                                                  );
                                        receiveRequestListView.getSelectionModel().clearSelection();
                                        popup.hide();
                                    });

            JFXButton reject = new JFXButton("Reject");
            reject.setOnMouseClicked(event1 ->
                                     {
                                         Main.client.manageRequests(
                                                 map.get(receiveRequestListView.getSelectionModel().getSelectedItem()),
                                                 "Reject"
                                                                   );
                                         receiveRequestListView.getSelectionModel().clearSelection();
                                         popup.hide();
                                     });

            VBox vBox = new VBox(accept, block, reject);

            popup.setPopupContent(vBox);
            popup.show(
                    receiveRequestListView, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, event.getX(),
                    event.getY()
                      );
        }

    }

    public void onSendRequestListViewClicked()
    {
        if (! sendRequestListView.getSelectionModel().isEmpty())
        {
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Text("Cancel"));
            layout.setBody(new Text("Do You Want To Cancel The Request ???"));

            JFXButton button = new JFXButton("OK");
            button.setOnMouseClicked(e ->
                                     {
                                         Main.client.manageRequests(
                                                 map.get(sendRequestListView.getSelectionModel().getSelectedItem()),
                                                 "Cancel"
                                                                   );
                                         dialog2.close();
                                         sendRequestListView.getItems().remove(
                                                 sendRequestListView.getSelectionModel().getSelectedItem());
                                         sendRequestListView.getSelectionModel().clearSelection();
                                     });

            layout.setActions(button);
            dialog2 = new JFXDialog(stackPane, layout, JFXDialog.DialogTransition.CENTER);

            dialog2.show();
        }
    }

    public void send()
    {
        updateList("SendRequests");
    }

    public void receive()
    {
        updateList("ReceiveRequests");
    }

    public void blocked()
    {
        updateList("Blocked");
    }

    public void contacts()
    {
        updateList("Contacts");
    }

    public void allUsers()
    {
        updateList("AllUsers");
    }

    public void channels()
    {
        updateList("Channels");
    }

    public void groups()
    {
        updateList("Groups");
    }

    public class UpdateList extends Task
    {
        private ArrayList<User> list;
        private String doWhat;
        private ArrayList<GroupAndChannel> groupAndChannels;
        private boolean flag;

        public UpdateList(ArrayList<User> list, String doWhat)
        {
            this.list = list;
            this.doWhat = doWhat;
            flag = true;
        }

        public UpdateList(String doWhat, ArrayList<GroupAndChannel> groupAndChannels)
        {
            this.doWhat = doWhat;
            this.groupAndChannels = groupAndChannels;
            flag = false;
        }

        @Override protected Object call()
        {
            VBox[] vBoxes;

            if (flag)
            {
                vBoxes = new VBox[list.size()];

                for (int i = 0; i < list.size(); i++)
                {
                    try
                    {
                        FXMLLoader listLoader = new FXMLLoader();
                        listLoader.setLocation(Main.class.getResource(Main.assets + "FXML/List.fxml"));
                        vBoxes[i] = listLoader.load();

                        ((Label) (((VBox) (((HBox) (vBoxes[i].getChildren().get(0))).getChildren().get(
                                2))).getChildren().get(0))).setText(list.get(i).getName());
                        ((Label) (((VBox) (((HBox) (vBoxes[i].getChildren().get(0))).getChildren().get(
                                2))).getChildren().get(1))).setText(list.get(i).isOnline() ? "Online" : "Offline");

                        ImageView imageView = ((ImageView) (((HBox) (vBoxes[i].getChildren().get(0))).getChildren().get(
                                0)));
                        imageView.setImage(new Image(new FileInputStream(list.get(i).getProfilePhotoUrl())));
                        imageView.setClip(new Circle(40, 40, 40));

                        map.put(vBoxes[i], list.get(i));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            else
            {
                vBoxes = new VBox[groupAndChannels.size()];
                System.out.println(groupAndChannels.size());

                for (int i = 0; i < groupAndChannels.size(); i++)
                {
                    try
                    {
                        FXMLLoader listLoader = new FXMLLoader();
                        listLoader.setLocation(Main.class.getResource(Main.assets + "FXML/List.fxml"));
                        vBoxes[i] = listLoader.load();

                        ((Label) (((VBox) (((HBox) (vBoxes[i].getChildren().get(0))).getChildren().get(
                                2))).getChildren().get(0))).setText(groupAndChannels.get(i).getName());
                        ((Label) (((VBox) (((HBox) (vBoxes[i].getChildren().get(0))).getChildren().get(
                                2))).getChildren().get(1))).setText(
                                String.format("%d Members", groupAndChannels.get(i).getMembers()));

                        ImageView imageView = ((ImageView) (((HBox) (vBoxes[i].getChildren().get(0))).getChildren().get(
                                0)));
                        imageView.setImage(
                                new Image(new FileInputStream(groupAndChannels.get(i).getProfilePhotoUrl())));
                        imageView.setClip(new Circle(40, 40, 40));

                        conversations.put(vBoxes[i], groupAndChannels.get(i));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            switch (doWhat)
            {
                case "SendRequest":
                    Platform.runLater(() ->
                                      {
                                          sendRequestListView.getItems().clear();
                                          sendRequestListView.getItems().addAll(vBoxes);
                                      }
                                     );
                    break;

                case "ReceiveRequest":
                    Platform.runLater(() ->
                                      {
                                          receiveRequestListView.getItems().clear();
                                          receiveRequestListView.getItems().addAll(vBoxes);
                                      }
                                     );
                    break;

                case "Contacts":
                    Platform.runLater(() ->
                                      {
                                          contactsListView.getItems().clear();
                                          contactsListView.getItems().addAll(vBoxes);
                                      }
                                     );
                    break;

                case "Blocked":
                    Platform.runLater(() ->
                                      {
                                          blockedListView.getItems().clear();
                                          blockedListView.getItems().addAll(vBoxes);
                                      });
                    break;

                case "AllUsers":
                    Platform.runLater(() ->
                                      {
                                          allUsersListView.getItems().clear();
                                          allUsersListView.getItems().addAll(vBoxes);
                                      }
                                     );
                    break;

                case "Groups":
                    Platform.runLater(() ->
                                      {
                                          groupsListView.getItems().clear();
                                          groupsListView.getItems().addAll(vBoxes);
                                      }
                                     );
                    break;

                case "Channels":
                    Platform.runLater(() ->
                                      {
                                          channelsListView.getItems().clear();
                                          channelsListView.getItems().addAll(vBoxes);
                                      }
                                     );
                    break;
            }

            return null;
        }
    }
}
