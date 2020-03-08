package photos.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import photos.model.PhotoGallery;
import photos.model.User;
import javafx.scene.control.Alert.AlertType;

/**
 * Controller class for the admin screen
 */
public class AdminController {
	@FXML ListView userListView;
	@FXML TextField newUsername;
	@FXML TextField newPassword;
	PhotoGallery photoGallery = null;
	

	/**
	 * On starting the AdminController the photoGallery loads all the users
	 * from their dat files into an ArrayList.  Then it makes an ObservableList
	 * from the ArrayList.
	 * 
	 * @param photoGallery the PhotoGallery
	 */
	public void start(PhotoGallery photoGallery) {
		this.photoGallery = photoGallery;
		userListView.setItems(this.photoGallery.getObservableUserList());
	}
	
	/**
	 * Create a user using the two TextFields for the username and the password
	 * 
	 * @param e ActionEvent
	 */
	public void createUser(ActionEvent e) {
		try {
			// create new user
			User user = this.photoGallery.createUser(newUsername.getText(), newPassword.getText());
			User.createStockAlbum(user);
			// load the new user
			this.photoGallery.loadUsers();
			userListView.setItems(this.photoGallery.getObservableUserList());
		} catch(Exception e1) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(e1.getMessage());
			alert.showAndWait();
		}
	}
	
	/**
	 * Delete the user selected on the ListView.  Cannot delete the admin.
	 * 
	 * @param e ActionEvent
	 */
	public void deleteSelectedUser(ActionEvent e) {
		System.out.println("deleting " + userListView.getSelectionModel().getSelectedItem());
		try {
			this.photoGallery.deleteUser((User) userListView.getSelectionModel().getSelectedItem());
			userListView.setItems(this.photoGallery.getObservableUserList());
		} catch (Exception e1) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(e1.getMessage());
			alert.showAndWait();
		}
	}
	
	/**
	 * Close the admin window
	 * 
	 * @param e ActionEvent
	 */
	public void closeWindow(ActionEvent e) {
		Stage stage = (Stage) newUsername.getScene().getWindow();
		stage.close();
	}

}
