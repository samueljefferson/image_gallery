package photos.controller;


import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import photos.model.Album;
import photos.model.Photo;
import photos.model.PhotoGallery;
import photos.model.User;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

/**
 * Controller class for the photo gallery
 */
public class GalleryViewController {
	@FXML ListView<Album> albumListView;
	@FXML ListView<Photo> photoListView;
	@FXML ListView<String> tagListView;
	@FXML Menu adminMenu;
	@FXML ImageView imageView;
	@FXML Label photoName;
	@FXML Label photoDate;
	@FXML Label photoCaption;
	
	PhotoGallery photoGallery;
	public LocalDate startDate = null;
	public LocalDate endDate = null;
	
	/**
	 * Run after PhotoGallery is constructed.  Logs in user.
	 * 
	 * @param username the user's username
	 * @param password the user's password
	 * @return if the user was able to login successfully
	 */
	public boolean start(String username, String password) {
		boolean success = false;
		this.photoGallery = new PhotoGallery();
		
		try {
			this.photoGallery.login(username, password);
			if (username.equals("stock") || username.equals("admin")) {
				// make sure that stock user has stock photos album
				boolean found = false;
				for (Album album : this.photoGallery.getCurrentUser().observableAlbumList) {
					if (album.getName().equals("stock photos")) {
						found = true;
						break;
					}
				}
				if (!found) {
					this.photoGallery.getCurrentUser().createAlbum("stock photos");
					// select stock photos
					Album stock = null;
					for (Album check : this.photoGallery.getCurrentUser().observableAlbumList) {
						if (check.getName().equals("stock photos")) {
							stock = check;
							break;
						}
					}
					File file = new File("stockPhotos/Angora.jpg");
					stock.createPhoto(file.getName(), file.toURI().toString(), file.lastModified());
					file = new File("stockPhotos/Capybara.JPG");
					stock.createPhoto(file.getName(), file.toURI().toString(), file.lastModified());
					file = new File("stockPhotos/cat on snow.jpg");
					stock.createPhoto(file.getName(), file.toURI().toString(), file.lastModified());
					file = new File("stockPhotos/Cattle tyrant on Capybara.jpg");
					stock.createPhoto(file.getName(), file.toURI().toString(), file.lastModified());
					file = new File("stockPhotos/Close up of a black domestic cat.jpg");
					stock.createPhoto(file.getName(), file.toURI().toString(), file.lastModified());
					file = new File("stockPhotos/Otter in Southwold.jpg");
					stock.createPhoto(file.getName(), file.toURI().toString(), file.lastModified());
					file = new File("stockPhotos/rotating globe.gif");
					stock.createPhoto(file.getName(), file.toURI().toString(), file.lastModified());
					stock.closeAlbum();
				}
			}
			success = true;
			albumListView.setItems(this.photoGallery.getCurrentUser().getObservableAlbumList());
			
			if (this.photoGallery.getCurrentUser().getUsername().equals(this.photoGallery.getAdmin().getUsername())) {
				adminMenu.setVisible(true);
			} 
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(e.getMessage());
			alert.showAndWait();
		}
		return success;	
	}
	
	/**
	 * Used when going back to the GalleryViewController from the slideshow
	 * 
	 * @param photoGallery the photoGallery
	 */
	public void reload(PhotoGallery photoGallery) {
		this.photoGallery = photoGallery;
		albumListView.setItems(this.photoGallery.getCurrentUser().getObservableAlbumList());
		
		// ListView with thumbnails
		photoListView.setItems(this.photoGallery.getCurrentAlbum().observablePhotoList);
		photoListView.setCellFactory(new Callback<ListView<Photo>,
			ListCell<Photo>>() {
				@Override
				public ListCell<Photo> call (ListView<Photo> photoListView) {
					return new createThumbnail();
				}
		});
		
		
		this.imageView.setImage(new Image(this.photoGallery.getCurrentPhoto().getLocation()));
		photoName.setText(this.photoGallery.getCurrentPhoto().getName());
		photoDate.setText(this.photoGallery.getCurrentPhoto().getDate());
		photoCaption.setText(photoGallery.getCurrentPhoto().getCaption());
		tagListView.setItems(photoGallery.getCurrentPhoto().getObservableTagList());
	}
	
	/**
	 * Event when the user clicks on an album in the Album ListView, sets User.getCurrentAlbum() to the selected album
	 * 
	 * @param e the MouseEvent
	 */
	public void albumClick(MouseEvent e) {
		// close the previous album
		if (this.photoGallery.getCurrentAlbum() != null) {
			this.photoGallery.getCurrentAlbum().closeAlbum();
			if (this.photoGallery.getCurrentAlbum().isTemp()) {
				// delete album because temporary
				try {
					this.photoGallery.getCurrentUser().deleteAlbum(this.photoGallery.getCurrentAlbum().getName());
					this.albumListView.refresh();
					System.out.println("removed album");
				} catch (Exception e1) {
					
				}
			}
		}
		// close the previous photo
		if (this.photoGallery.getCurrentPhoto() != null) {
			this.photoGallery.setCurrentPhoto(null);
		}
		
		// set current album to album that was clicked on
		try {
			int index = albumListView.getSelectionModel().getSelectedIndex();
			this.photoGallery.setCurrentAlbum(this.photoGallery.getCurrentUser().getObservableAlbumList().get(index));
			// open current album in the next list view to create an ObservableList from its ArrayList
			this.photoGallery.getCurrentAlbum().openAlbum();
			// set ListView to view ObservableList of album
			
			// ListView with thumbnails
			photoListView.setItems(this.photoGallery.getCurrentAlbum().observablePhotoList);
			photoListView.setCellFactory(new Callback<ListView<Photo>,
				ListCell<Photo>>() {
					@Override
					public ListCell<Photo> call (ListView<Photo> photoListView) {
						return new createThumbnail();
					}
			});
			
			// make the current photo not visible when another album is selected
			this.photoGallery.setCurrentPhoto(null);
			imageView.setImage(null);
			photoName.setText("");
			photoDate.setText("");
			photoCaption.setText("");
			tagListView.setItems(null);
		} catch (Exception e1) {
		}
	}
	
	/**
	 * Class used by the cell factory to generate the ListView
	 * @author James Beetham
	 * @author Samuel Jefferson
	 *
	 */
	static class createThumbnail extends ListCell<Photo> {
		@Override
		public void updateItem(Photo photo, boolean empty) {
			super.updateItem(photo, empty);
			if (photo != null) {
				ImageView imageView = new ImageView();
				imageView.setFitHeight(50);
				imageView.setFitWidth(50);
				Image image = new Image(photo.getLocation());
				imageView.setImage(image);

				GridPane gridPane = new GridPane();
				setGraphic(gridPane);
				gridPane.getChildren().add(imageView);
				
				if (photo.getCaption() == null) {
					setText("");
				} else {
					setText(photo.getCaption());
				}
			} else {
				setGraphic(null);
				setText(null);
			}
		}
		
	}
	
	/**
	 * Event when the user clicks on a photo in an album
	 * 
	 * @param e the MouseEvent
	 */
	public void photoClick(MouseEvent e) {
		int index = photoListView.getSelectionModel().getSelectedIndex();
		// get current photo
		try {
			this.photoGallery.setCurrentPhoto(this.photoGallery.getCurrentAlbum().observablePhotoList.get(index));
			
			// display image and details
			Image image = new Image(this.photoGallery.getCurrentPhoto().getLocation());
			this.imageView.setImage(image);
			photoName.setText(this.photoGallery.getCurrentPhoto().getName());
			photoDate.setText(this.photoGallery.getCurrentPhoto().getDate());

			// open the photo for tags
			this.photoGallery.getCurrentPhoto().openPhoto();
			tagListView.setItems(photoGallery.getCurrentPhoto().getObservableTagList());
			
			if (this.photoGallery.getCurrentPhoto().getCaption() != null) {
				photoCaption.setText(photoGallery.getCurrentPhoto().getCaption());
			} else {
				photoCaption.setText("");
			}
			tagListView.setItems(photoGallery.getCurrentPhoto().getObservableTagList());
		} catch (Exception e1) {	
		}
	}
	
	/**
	 * Blanks out ImageView, date, caption and tags.
	 */
	public void blankImage() {
		this.imageView.setImage(null);
		photoName.setText("");
		photoDate.setText("");
		photoCaption.setText("");
		tagListView.setItems(null);
	}
	
	// Methods are listed here in the order they appear on the menu
	
	/**
	 * Logs out current user.  Saves their data and goes back to login screen.
	 * 
	 * @param e ActionEvent
	 * @throws IOException a possible error
	 */
	public void logout(ActionEvent e) throws IOException {
		// close current album
		if (photoGallery.getCurrentAlbum() != null) {
			photoGallery.getCurrentAlbum().closeAlbum();
		}
		// write data
		User.writeUser(this.photoGallery.getCurrentUser());
		// change window to login screen
		Stage primayStage = (Stage) albumListView.getScene().getWindow();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/photos/view/loginScreen.fxml"));
		GridPane root = (GridPane)loader.load();
		
		Scene scene = new Scene(root);
		primayStage.setScene(scene);
		primayStage.setTitle("Login");
		primayStage.setResizable(false);
		
	}
	
	/**
	 * Saves the data and quits the program
	 * 
	 * @param e ActionEvent
	 * @throws IOException a possible error
	 */
	public void quit(ActionEvent e) throws IOException {
		// close current album
		if (photoGallery.getCurrentAlbum() != null) {
			photoGallery.getCurrentAlbum().closeAlbum();
		}
		// write data
		User.writeUser(this.photoGallery.getCurrentUser());
		Platform.exit();
	}

	/**
	 * Create an album.
	 * 
	 * @param e ActionEvent
	 */
	public void createAlbum(ActionEvent e) {
		// create a TextInputDialog
		TextInputDialog textInputDialog = new TextInputDialog();
		textInputDialog.setTitle("Create Album");
		textInputDialog.setHeaderText("Name for album");
		textInputDialog.setGraphic(null);
		Optional<String> albumName = textInputDialog.showAndWait();
		
		if (!albumName.get().equals("")) {
			try {
				this.photoGallery.getCurrentUser().createAlbum(albumName.get());
			} catch (Exception e1) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(e1.getMessage());
				alert.showAndWait();
			}
		} 
	}
	
	/**
	 * Change the name of the selected album
	 * 
	 * @param e ActionEvent
	 */
	public void renameAlbum(ActionEvent e) {
		if (photoGallery.getCurrentAlbum() != null) {
			// create a TextInputDialog
			TextInputDialog textInputDialog = new TextInputDialog();
			textInputDialog.setTitle("Rename Album");
			textInputDialog.setHeaderText("new name for album");
			textInputDialog.setGraphic(null);
			Optional<String> albumName = textInputDialog.showAndWait();
			
			if (albumName.isPresent()) {
				try {
					this.photoGallery.getCurrentUser().renameAlbum(this.photoGallery.getCurrentAlbum(), albumName.get());
					this.albumListView.refresh();
				} catch (Exception e1) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText(e1.getMessage());
					alert.showAndWait();
				}
			}
		}
	}
	
	/**
	 * Set the albums to be sorted by their name.
	 */
	public void albumByName() {
		this.photoGallery.setAlbumSortby("name");
		this.photoGallery.sortAlbums();
	}
	
	/**
	 * Set the albums to be sorted by their date.
	 */
	public void albumByDate() {
		this.photoGallery.setAlbumSortby("date");
		this.photoGallery.sortAlbums();
	}
	
	/**
	 * Sets the albums to be sorted in ascending order
	 */
	public void albumAscending() {
		this.photoGallery.setAlbumOrder("ascending");
		this.photoGallery.sortAlbums();
	}
	
	/**
	 * Sets the albums to be sorted in descending order
	 */
	public void albumDescending() {
		this.photoGallery.setAlbumOrder("descending");
		this.photoGallery.sortAlbums();
	}
	
	/**
	 * Delete the selected album
	 */
	public void deleteAlbum() {
		if (this.photoGallery.getCurrentAlbum() != null) {
			this.photoGallery.getCurrentUser().deleteAlbum(this.photoGallery.getCurrentAlbum().getName());
			albumListView.getSelectionModel().select(null);
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("error: no album is currently selected");
			alert.showAndWait();
		}
	}
	
	/**
	 * View the currently selected album as a slide show
	 * 
	 * @param e ActionEvent
	 */
	public void viewSlideshow(ActionEvent e) {
		if (this.photoGallery.getCurrentAlbum() != null && this.photoGallery.getCurrentAlbum().observablePhotoList.size() != 0) {
			this.photoGallery.getCurrentAlbum().closeAlbum();
			
			// change the current scene to the slideshow
			Stage primaryStage = (Stage) photoName.getScene().getWindow();
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/photos/view/slideshow.fxml"));
			GridPane root = null;
			try {
				root = (GridPane)loader.load();
				
				SlideshowController slideshowController = loader.getController();
				slideshowController.load(this.photoGallery);
				
				Scene scene = new Scene(root);
				primaryStage.setScene(scene);
				primaryStage.setTitle("Slideshow");
				primaryStage.setResizable(false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}				
		}
	}
	
	/**
	 * Sets caption in image by prompting user with a pop up
	 * 
	 * @param e ActionEvent
	 */
	public void setCaption(ActionEvent e) {
		// make sure there is a currently selected image
		if (photoGallery.getCurrentPhoto() != null) {
			TextInputDialog textInputDialog = new TextInputDialog("caption");
			textInputDialog.setTitle("Set caption");
			textInputDialog.setContentText("Enter a caption");
			textInputDialog.setGraphic(null);
			textInputDialog.setHeaderText(null);
			Optional<String> result = textInputDialog.showAndWait();
			
			if (result.isPresent()) {
				photoGallery.getCurrentPhoto().setCaption(result.get());
				photoCaption.setText(result.get());
				photoListView.refresh();
			}
		}
	}
	
	/**
	 * Removes the caption from an image
	 * 
	 * @param e ActionEvent
	 */
	public void removeCaption(ActionEvent e) {
		if (photoGallery.getCurrentPhoto() != null) {
			photoGallery.getCurrentPhoto().setCaption("");
			photoCaption.setText("");
			photoListView.refresh();
		}
	}
	
	/**
	 * Creates a new tag for the photoGallery
	 * 
	 * @param e ActionEvent
	 */
	public void createTag(ActionEvent e) {
		// TODO
		TextInputDialog textInputDialog = new TextInputDialog();
		textInputDialog.setTitle("Create a new tag");
		textInputDialog.setHeaderText("enter the tag here");
		textInputDialog.setGraphic(null);
		Optional<String> tagName = textInputDialog.showAndWait();
		
		if (tagName.isPresent()) {
			// check if tag is already used
			if (this.photoGallery.tagSet.contains(tagName.get())) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("errror: tag " + tagName.get() + " is already in use");
				alert.showAndWait();
			} else {
				this.photoGallery.tagSet.add(tagName.get());
			}
		}
	}
	
	/**
	 * Add a tag to selected photo.
	 * 
	 * @param e ActionEvent
	 */
	public void addTag(ActionEvent e) {
		// check if there are any unused tags in current photo
		boolean unusedTags = false;
		for (String tag : this.photoGallery.tagSet) {
			if (!this.photoGallery.getCurrentPhoto().tags.containsKey(tag)) {
				unusedTags = true;
				break;
			}
		}
		if (!unusedTags) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("errror: there are no unused tags for selected photo");
			alert.showAndWait();
		} else if (photoGallery.getCurrentPhoto() != null) {			
			Stage adminStage = new Stage();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/photos/view/addTag.fxml"));
			GridPane root = null;
			try {
				root = (GridPane)loader.load();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			AddTagController addTagController = loader.getController();
			addTagController.start(photoGallery);
			
			Scene scene = new Scene(root);
			adminStage.setScene(scene);
			adminStage.setTitle("Tag screen");
			adminStage.setResizable(false);
			adminStage.showAndWait();
			tagListView.setItems(photoGallery.getCurrentPhoto().getObservableTagList());
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("errror: no photo is selected to add tags to");
			alert.showAndWait();
		}
	}
	
	/**
	 * Remove selected tag from photo.
	 * 
	 * @param e ActionEvent
	 */
	public void removeTag(ActionEvent e) {
		if (this.photoGallery.getCurrentPhoto() != null) {
			try {
				String key = tagListView.getSelectionModel().getSelectedItem().split(",")[0];
				this.photoGallery.getCurrentPhoto().removeTag(key);
				tagListView.getSelectionModel().select(null);
			} catch (Exception e1) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("errror: no tag is currently selected");
				alert.showAndWait();
			}
			
		}
	}
	
	/**
	 * Adds a photo to the currently open Album
	 * 
	 * @param e ActionEvent
	 */
	public void addPhoto(ActionEvent e) {
		if (photoGallery.getCurrentAlbum() != null) {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog((Stage) albumListView.getScene().getWindow());

			String ext = file.getName().substring(file.getName().length()-3).toLowerCase();
			
			HashSet<String> goodExt = new HashSet<String>();
			goodExt.add("jpg");
			goodExt.add("jpeg");
			goodExt.add("png");
			goodExt.add("gif");
			
			if (file != null && goodExt.contains(ext)) {
				photoGallery.getCurrentAlbum().createPhoto(file.getName(), file.toURI().toString(), file.lastModified());
			} else {
				if (file != null) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("errror: file extension " + ext + " is invalid");
					alert.showAndWait();
				}
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("errror: no album is currently selected");
			alert.showAndWait();
		}
	}
	
	/**
	 * Copy photo from current album to another album.
	 * 
	 * @return if the copy was successful or not
	 */
	public boolean copyPhoto() {
		boolean success = false;
		
		if (this.photoGallery.getCurrentAlbum() != null && this.photoGallery.getCurrentPhoto() != null) {
			// create a TextInputDialog to get destination album
			TextInputDialog textInputDialog = new TextInputDialog();
			textInputDialog.setTitle("Destination album");
			textInputDialog.setHeaderText("Write destination album");
			textInputDialog.setGraphic(null);
			Optional<String> albumName = textInputDialog.showAndWait();
			
			if (albumName.isPresent()) {
				// find album using albumName
				Album destination = null;
				Photo photo = this.photoGallery.getCurrentPhoto();
				
				for (Album album : this.photoGallery.getCurrentUser().observableAlbumList) {
					if (album.getName().equals(albumName.get())) {
						destination = album;
						success = true;
						destination.createPhoto(photo.getName(), photo.getLocation(), photo.date.getTimeInMillis());
						destination.closeAlbum();
						break;
					}
				}
				if (!success) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("errror: could not find specified album");
					alert.showAndWait();
				}
			}
		}
		return success;
	}
	
	/**
	 * Move photo from one album to another
	 */
	public void movePhoto() {
		// copy photo, if it succeeds, remove the photo from the current album
		if (this.copyPhoto()) {
			this.photoGallery.getCurrentAlbum().removePhoto(this.photoGallery.getCurrentPhoto());
			this.blankImage();
		}
	}
	
	/**
	 * Set the albums to be sorted by their name.
	 */
	public void photoByName() {
		this.photoGallery.setPhotoSortby("name");
		this.photoGallery.sortPhotos();
	}
	
	/**
	 * Set the albums to be sorted by their date.
	 */
	public void photoByDate() {
		this.photoGallery.setPhotoSortby("date");
		this.photoGallery.sortPhotos();
	}
	
	/**
	 * Sets the albums to be sorted in ascending order
	 */
	public void photoAscending() {
		this.photoGallery.setPhotoOrder("ascending");
		this.photoGallery.sortPhotos();
	}
	
	/**
	 * Sets the albums to be sorted in descending order
	 */
	public void photoDescending() {
		this.photoGallery.setPhotoOrder("descending");
		this.photoGallery.sortPhotos();
	}
	
	/**
	 * Deletes the selected photo
	 */
	public void deletePhoto() {
		if (this.photoGallery.getCurrentPhoto() != null) {
			this.photoGallery.getCurrentAlbum().removePhoto(this.photoGallery.getCurrentPhoto());
			this.blankImage();
			photoListView.getSelectionModel().select(null);
			photoListView.refresh();
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("error: no photo is currently selected");
			alert.showAndWait();
		}
	}
	
	/**
	 * Used to search for images by tags
	 */
	public void searchByTerms() {
		// TODO add regex support? (boolean option?)
        // create a TextInputDialog
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Search by tag");
        textInputDialog.setHeaderText("Use `tag_name=tag_value` or can join two using `AND` or `OR` like "
        		+ "`tag_name1=tag_value1 OR tag_name2=tag_value2`.\n"
        		+ "Results will be displayed in a temporary album called `_search results: ...`. Rename that album"
        		+ "to save.");
        textInputDialog.setGraphic(null);
        Optional<String> tagsRaw = textInputDialog.showAndWait();
        String res = tagsRaw.get();
        // invalid search (nothing to search)
        if (res.length() == 0 || res.indexOf("=") == -1) return;
        String[] resParts = res.split("=");
        String[] resTagNames = { resParts[0] };
        String[] resTagValues = { resParts[1] };
        boolean and = false;
        if (resParts.length == 3) { // split middle into left and right side
        	int length, split;
        	if ((split = res.indexOf("AND")) == -1) {
        		if ((split = res.indexOf("OR")) == -1) {
        			return; // TODO invalid format (two '=' but no AND or OR)
        		} else {
        			length = 2;
        			and = false;
        		}
        	} else {
        		length = 3;
        	}
        	String midLeft = resParts[1].substring(0, split);
        	String midRight = resParts[1].substring(split + length);
        	resTagNames = new String[2];
        	resTagValues = new String[2];
        	resTagNames[0] = resParts[0];
        	resTagNames[1] = midRight;
        	resTagValues[0] = midLeft;
        	resTagValues[1] = resParts[2];
        }
//        System.out.print("search: ");
//        for (int i = 0; i < resTagNames.length; i++) {
//        	System.out.print(resTagNames[i] + "=" + resTagValues[i] + ", ");
//        }
//        System.out.println();
//        String[] tagsList = tagsRaw.get().split(" ");
//        boolean addAll = false;
//        if (tagsList.length == 0) {
//            addAll = true;
//            String[] tmp = { "f" };
//            tagsList = tmp;
//        }
        HashSet<Photo> resPhotos = new HashSet<>();
//        for (Album a : this.photoGallery.getCurrentUser().getAlbumList()) {
        for (Album a : this.photoGallery.getCurrentUser().observableAlbumList) {
            for (Photo p : a.getPhotoList()) {            	
//                for (String s : p.getObservableTagList()) {
            	boolean continuePhoto = false;
            	boolean andTrue = false;
                for (String tagN : p.tags.keySet()) {
                	for (int i = 0; i < resTagNames.length; i++) {
                		if (!tagN.equals(resTagNames[i])) continue;
                		if (!p.tags.get(tagN).equals(resTagValues[i])) continue;
                		if (and && !andTrue) {
                			if (!andTrue) {
                    			// first check is true, wait for second
                    			andTrue = true;
                    			continue;
                			}
                		}
                		resPhotos.add(p);
                		continuePhoto = true;
                		break;
                		
                	}
                	if (continuePhoto) break;
//                    for (String tag : tagsList) {
//                    	if (addAll || tag.equals(s)) resPhotos.add(p);
//                    }
                }
            }
        }
//        System.out.println("size: " + resPhotos.size());
        
        String albumName = "_search results: " + tagsRaw.get();
        // TODO check if album exists
        Album createdAlbum = null;
        try {
            createdAlbum = this.photoGallery.getCurrentUser().createAlbum(albumName);
            for (Album a : this.photoGallery.getCurrentUser().getAlbumList()) {
                if (a.getName().equals(albumName)) {
                    createdAlbum = a; 
                    break; 
                }
            }
            // TODO create get album (by name) method)
        } catch (Exception e) {
//            System.out.println("TODO search already exists");
        }
        if (createdAlbum == null) {
//            System.out.println("could not find created album: " + albumName);
            return;
        }
        ArrayList<Photo> pl = new ArrayList<>(resPhotos);
        createdAlbum.setTemp(true);
        createdAlbum.setPhotoList(pl);
        // TODO make it temporary
        
        // create temp album with results
//      System.out.println(tags);
//      if (albumName.isPresent()) {
//          this.photoGallery.getCurrentUser().renameAlbum(this.photoGallery.getCurrentAlbum().getName(), albumName.get());
//          // update albumListView
//          albumListView.setItems(this.photoGallery.getCurrentUser().getObservableAlbumList());
//      }
        
        
        // TODO added this to select created album
        // close current album
        if (this.photoGallery.getCurrentAlbum() != null) {
        	this.photoGallery.getCurrentAlbum().closeAlbum();
        }
        // make createdAlbum the current album
        this.photoGallery.setCurrentAlbum(createdAlbum);
        // open createdAlbum
        this.photoGallery.getCurrentAlbum().openAlbum();
        // select createdAlbum
        albumListView.getSelectionModel().select(createdAlbum);
        // display the photos in createdAlbum
        photoListView.setItems(this.photoGallery.getCurrentAlbum().observablePhotoList);
		photoListView.setCellFactory(new Callback<ListView<Photo>,
			ListCell<Photo>>() {
				@Override
				public ListCell<Photo> call (ListView<Photo> photoListView) {
					return new createThumbnail();
				}
		});
    }
	
	/**
	 * Search for images by date
	 * 
	 * @param e ActionEvent
	 */
	public void searchByDate(ActionEvent e) {
		// TODO
		
		// open up the search stage
		Stage searchStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/photos/view/searchByDatepicker.fxml"));
		GridPane root = null;
		try {
			root = (GridPane)loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		SearchByDateController searchByDateController = loader.getController();
		searchByDateController.start(this);
		
		Scene scene = new Scene(root);
		searchStage.setScene(scene);
		searchStage.setTitle("Search by date range");
		searchStage.setResizable(false);
		searchStage.showAndWait();
		
		if (this.startDate != null && this.endDate != null) {
			HashSet<Photo> resPhotos = new HashSet<>();
//	        for (Album a : this.photoGallery.getCurrentUser().getAlbumList()) {
			for (Album a : this.photoGallery.getCurrentUser().observableAlbumList) {
	            for (Photo p : a.getPhotoList()) {
	            	String[] parts = p.getDate().split(" ");
	            	String[] monthNums = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	            	int monthNum = -1;
	            	for (int i = 0; i < monthNums.length; i++) {
	            		if (parts[1].startsWith(monthNums[i])) {
	            			monthNum = i + 1;
	            			break;
	            		}
	            	}
	            	if (monthNum == -1) {
//	            		System.out.println("invalid date");
	            		return;
	            	}
	            	String testDate = parts[parts.length - 1] + "-" + monthNum + "-" + parts[2];
//	            	System.out.println(testDate);
	            	
	            	String[] startParts = (new String(startDate.toString())).split("-");
	            	String[] endParts = (new String(endDate.toString())).split("-");
	            	String[] testParts = testDate.split("-");
	            	boolean add = true;
	            	boolean low = true;
	            	boolean high = true;
	            	for (int i = 0; i < testParts.length; i++) {
	            		int ti = Integer.parseInt(testParts[i]);
	            		int li = Integer.parseInt(startParts[i]);
	            		int gi = Integer.parseInt(endParts[i]);
	            		if ((low && ti < li) || (high && ti > gi)) {
	        				add = false;
	            			break;
	            		}
	            		if (ti != li) low = false;
	            		if (ti != gi) high = false;
	            	}
	            	if (add) {
	            		resPhotos.add(p);
	            	}
	            }
	        }
			
//	        System.out.println("size: " + resPhotos.size());
	        
	        String albumName = "_search dates: " + startDate + " to " + endDate;
	        // TODO check if album exists
	        Album createdAlbum = null;
	        try {
	            createdAlbum = this.photoGallery.getCurrentUser().createAlbum(albumName);
	            for (Album a : this.photoGallery.getCurrentUser().getAlbumList()) {
	                if (a.getName().equals(albumName)) {
	                    createdAlbum = a; 
	                    break; 
	                }
	            }
	            // TODO create get album (by name) method)
	        } catch (Exception e1) {
//	            System.out.println("TODO search already exists");
	        }
	        if (createdAlbum == null) {
//	            System.out.println("could not find created album: " + albumName);
	            return;
	        }
	        ArrayList<Photo> pl = new ArrayList<>(resPhotos);
	        createdAlbum.setPhotoList(pl);
	        createdAlbum.setTemp(true);
	        
	        // TODO added this to select created album
	        // close current album
	        if (this.photoGallery.getCurrentAlbum() != null) {
	        	this.photoGallery.getCurrentAlbum().closeAlbum();
	        }
	        // make createdAlbum the current album
	        this.photoGallery.setCurrentAlbum(createdAlbum);
	        // open createdAlbum
	        this.photoGallery.getCurrentAlbum().openAlbum();
	        // select createdAlbum
	        albumListView.getSelectionModel().select(createdAlbum);
	        // display the photos in createdAlbum
	        photoListView.setItems(this.photoGallery.getCurrentAlbum().observablePhotoList);
			photoListView.setCellFactory(new Callback<ListView<Photo>,
				ListCell<Photo>>() {
					@Override
					public ListCell<Photo> call (ListView<Photo> photoListView) {
						return new createThumbnail();
					}
			});
		}
	}
	
	/**
	 * Opens the admin menu
	 * 
	 * @param e ActionEvent
	 */
	public void openAdminMenu(ActionEvent e) {
		Stage adminStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/photos/view/admin.fxml"));
		GridPane root = null;
		try {
			root = (GridPane)loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		AdminController adminController = loader.getController();
		adminController.start(photoGallery);
		
		Scene scene = new Scene(root);
		adminStage.setScene(scene);
		adminStage.setTitle("Admin screen");
		adminStage.setResizable(false);
		adminStage.showAndWait();
	}
}
