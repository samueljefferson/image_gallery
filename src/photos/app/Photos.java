package photos.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Starts the program.
 */
public class Photos extends Application {
	/**
	 * Sets up the login screen from the fxml file
	 */
	@Override
	public void start(Stage primayStage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/photos/view/loginScreen.fxml"));
		GridPane root = (GridPane)loader.load();		
		Scene scene = new Scene(root);
		primayStage.setScene(scene);
		primayStage.setTitle("Login");
		primayStage.setResizable(false);
		primayStage.show();
	}
	
	/**
	 * Launches the login screen
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
