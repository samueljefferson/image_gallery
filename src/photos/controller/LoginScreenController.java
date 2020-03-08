package photos.controller;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Controller for the login screen
 */
public class LoginScreenController {
	@FXML TextField usernameField;
	@FXML TextField passwordField;
	
	/**
	 * Sets up the galleryView and changes the primary scene to it
	 * @param e ActionEvent
	 */
	public void login(ActionEvent e) {
		Stage primaryStage = (Stage) usernameField.getScene().getWindow();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/photos/view/galleryView.fxml"));
		GridPane root = null;
		try {
			root = (GridPane)loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		GalleryViewController galleryViewController = loader.getController();
		boolean success = galleryViewController.start(usernameField.getText(), passwordField.getText());
		
		if (success) {
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Gallery View " + usernameField.getText());
			primaryStage.setResizable(false);
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent e) {
					try {
						galleryViewController.quit(null);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
			
		}
	}
	
	/**
	 * Quits the program when run.
	 * 
	 * @param e ActionEvent
	 */
	public void quit(ActionEvent e) {
		Platform.exit();
	}
}
