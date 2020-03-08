package photos.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Controller class for search by date
 */
public class SearchByDateController {
	@FXML DatePicker startDate;
	@FXML DatePicker endDate;
	GalleryViewController controller;
	
	/**
	 * Sets the GalleryView controller for the SearchByDateController
	 * 
	 * @param controller controller for GalleryView
	 */
	public void start(GalleryViewController controller) {
		this.controller = controller;
	}
	
	/**
	 * Updates the GalleryView controller with the selected start date and end date
	 * @param e ActionEvent
	 */
	public void search(ActionEvent e) {
		controller.startDate = startDate.getValue();
		controller.endDate = endDate.getValue();
		if (controller.startDate == null || controller.endDate == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("error: no start date or no end date specified");
			alert.showAndWait();
		}
		Stage stage = (Stage) startDate.getScene().getWindow();
		stage.close();
	}
}
