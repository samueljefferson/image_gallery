package photos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import photos.model.PhotoGallery;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the addTag.fxml file
 */
public class AddTagController {
	@FXML ComboBox<String> keyComboBox;
	@FXML TextField value;
	PhotoGallery photoGallery = null;
	
	/** 
	 * Loads the photoGallery into controller
	 * 
	 * @param photoGallery the photo gallery
	 */
	public void start(PhotoGallery photoGallery) {
		this.photoGallery = photoGallery;
		// populate comboBox with unused keys
		for (String tag : this.photoGallery.tagSet) {
			if (!this.photoGallery.getCurrentPhoto().tags.containsKey(tag)) {
				keyComboBox.getItems().add(tag);
			}
			
		}
		keyComboBox.getSelectionModel().select(0);
	}
	
	/**
	 * Inserts a tag
	 */
	public void insertTag() {
		try {
			this.photoGallery.getCurrentPhoto().addTag(keyComboBox.getSelectionModel().getSelectedItem(), value.getText());
			Stage stage = (Stage) value.getScene().getWindow();
			stage.close();
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(e.getMessage());
			alert.showAndWait();
		}
	}
}
