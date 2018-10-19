package client.controllers;

import client.HandleInput;
import client.Main;
import client.threads.Uploader;
import client.data.GroupAndChannel;
import client.data.User;
import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import org.controlsfx.control.CheckListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class GroupController
{
    @FXML private ImageView groupPhoto;
    @FXML private JFXTextField groupName;
    @FXML private JFXTextField groupId;
    @FXML private JFXListView<VBox> listView;

    private JFXDialog dialog;
    private ListController listController;
    private File file;
    private ArrayList<User> users = new ArrayList<>();

    public void setFirstVariable(JFXDialog dialog, ListController listController)
    {
        this.dialog = dialog;
        this.listController = listController;
        HandleInput.groupController = this;
    }

    public void closeGroupDialog()
    {
        if (dialog.isOverlayClose())
        {
            dialog.close();
            listView.getItems().clear();
        }
    }

    public void selectGroupPhoto()
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

                groupPhoto.setImage(image);
                groupPhoto.setClip(new Circle(50, 50, 50));

            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void update()
    {
        listView.getItems().addAll(listController.contactsListView.getItems());
    }

    public void addUsers()
    {
        VBox vBox = listView.getSelectionModel().getSelectedItem();
        users.add(ListController.map.get(vBox));
        listView.getItems().remove(vBox);
    }

    public void createGroup()
    {
        if (!groupName.getText().equals("") && !groupId.getText().equals("") && file != null)
        {
            Main.client.createGroup(groupName.getText(), groupId.getText(), users);
        }
    }

    public class Result implements Runnable
    {
        private GroupAndChannel group;

        public Result(GroupAndChannel group)
        {
            this.group = group;
        }

        @Override public void run()
        {
            Uploader uploader = new Uploader(file, "Groups/"+group.getStringId()+".jpg");
            uploader.run();
        }
    }
}
