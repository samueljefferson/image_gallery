package photos.controller;


import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import photos.model.PhotoGallery;

/**
 * Controller class for the slideshow fxml file
 */
public class SlideshowController {
	@FXML ImageView imageView;
	@FXML Label imageName;
	PhotoGallery photoGallery = null;
	private int index;
	private int size;
	
	/**
	 * Loads the photoGallery into the slideshow controller
	 * 
	 * @param photoGallery the photo gallery
	 */
	public void load(PhotoGallery photoGallery) {
		this.photoGallery = photoGallery;
		
		if (this.photoGallery.getCurrentPhoto() == null) {
			this.photoGallery.setCurrentPhoto(this.photoGallery.getCurrentAlbum().observablePhotoList.get(0));
		}
		
		this.displayCurrentPhoto();
		this.size = this.photoGallery.getCurrentAlbum().observablePhotoList.size();
		for (int i = 0; i < this.size; i++) {
			if (this.photoGallery.getCurrentAlbum().observablePhotoList.get(i) == this.photoGallery.getCurrentPhoto()) {
				this.index = i;
				break;
			}
		}
	}
	
	/**
	 * Displays the current photo
	 */
	public void displayCurrentPhoto() {
		Image image = new Image(this.photoGallery.getCurrentPhoto().getLocation());
		imageView.setImage(image);
		imageName.setText(this.photoGallery.getCurrentPhoto().getName());
	}
	
	/**
	 * Returns the user to the galleryView
	 * 
	 * @param e ActionEvent
	 */
	public void back(ActionEvent e) {
		Stage primaryStage = (Stage) imageView.getScene().getWindow();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/photos/view/galleryView.fxml"));
		GridPane root = null;
		try {
			root = (GridPane)loader.load();
			GalleryViewController galleryViewController = loader.getController();
			galleryViewController.reload(this.photoGallery);
			
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Gallery View " + this.photoGallery.getCurrentUser().getUsername());
			primaryStage.setResizable(false);
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent e) {
					try {
						galleryViewController.quit(null);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/**
	 * Views the previous image in the album
	 * 
	 * @param e ActionEvent
	 */
	public void previous(ActionEvent e) {
		if (this.index > 0) {
			this.index--;
			this.photoGallery.setCurrentPhoto(this.photoGallery.getCurrentAlbum().observablePhotoList.get(index));
			this.displayCurrentPhoto();
		}
		
	}
	
	/**
	 * Views the next image in the album
	 * 
	 * @param e ActionEvent
	 */
	public void next(ActionEvent e) {
		if (this.index < this.size - 1) {
			this.index++;
			this.photoGallery.setCurrentPhoto(this.photoGallery.getCurrentAlbum().observablePhotoList.get(index));
			this.displayCurrentPhoto();
		}
	}
}
