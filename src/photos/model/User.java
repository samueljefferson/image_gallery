package photos.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * User contains user data and an ArrayList of Albums
 */
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;

	HashSet<String> albumNames = new HashSet<String>();
	ArrayList<Album> albumList = new ArrayList<Album>();
	public transient ObservableList<Album> observableAlbumList = FXCollections.observableArrayList();


	/**
	 * Constructor for new users
	 * 
	 * @param username user name for user
	 * @param password password for user
	 */
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Creates an album of stock photos for the selected user
	 * 
	 * @param user the user
	 */
	public static void createStockAlbum(User user) {
		Album stock = user.createAlbum("Stock Photos");
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
		try {
			User.writeUser(user);
		} catch (IOException e) {
		}
	}

	/**
	 * Creates a new album
	 * 
	 * @param albumName the name of the album
	 * @return the album
	 */
	public Album createAlbum(String albumName) {
		// check if the album name is already used
		if (!albumNames.contains(albumName)) {
			Album album = new Album(albumName);
			albumNames.add(albumName);
			observableAlbumList.add(album);
			return album;
		} else {
			throw new IllegalArgumentException("error: " + albumName + " already exists");
		}
		
	}
	
	/**
	 * Deletes an album
	 * 
	 * @param albumName name of album to be deleted
	 */
	public void deleteAlbum(String albumName) {
		boolean success = false;
		
		for (Album album : this.observableAlbumList) {
			if (album.getName().equals(albumName)) {
				observableAlbumList.remove(album);
				albumNames.remove(albumName);
				success = true;
				for (Album album1 : this.albumList) {
					if (album1.getName().equals(albumName)) {
						this.albumList.remove(album1);
					}
				}
				break;
			}
		}
		if (!success) {
			throw new IllegalArgumentException("error: unable to delete " + albumName + ", it doesn't exists");
		}
	}
	
	/**
	 * Renames an album
	 * 
	 * @param album the album that is being renamed
	 * @param newAlbumName New name for album
	 */
	public void renameAlbum(Album album, String newAlbumName) {
		
		if (albumNames.contains(newAlbumName)) {
			throw new IllegalArgumentException("error: unable to rename album to " + newAlbumName + ", there is already another album with this name");
		}
		for (Album old : this.albumList) {
			if (old.getName().equals(album.getName())) {
				old.setName(newAlbumName);
			}
		}
		albumNames.remove(album.getName());
		albumNames.add(newAlbumName);
		album.setName(newAlbumName);
		if (album.isTemp()) album.setTemp(false);
	}
	
	/**
	 * Gets the username
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Gets the password
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Gets the ArrayList of albums that the user has
	 * 
	 * @return ArrayList of albums
	 */
	public ArrayList<Album> getAlbumList() {
		return albumList;
	}
	
	/**
	 * Sets the ArrayList of albums that the user has
	 * 
	 * @param albumList ArrayList of albums
	 */
	public void setAlbumList(ArrayList<Album> albumList) {
		this.albumList = albumList;
	}
	
	/**
	 * Gets the ObservableList of albums that the user has
	 * 
	 * @return ObservableList of albums
	 */
	public ObservableList<Album> getObservableAlbumList() {
		return this.observableAlbumList;
	}

	/**
	 * Writes specified user to a dat file.
	 * 
	 * @param user the user to be written
	 * @throws IOException Exception
	 */
	public static void writeUser(User user) throws IOException {
		// set current album to null
		
		// update ArrayList of Albums with ObservableList of Albums
		user.setAlbumList(new ArrayList<Album>(user.observableAlbumList));
		
		// Serializable stuff
		String storeDir = "dat";
		String storeFile = user.username + ".dat";
		
		@SuppressWarnings("resource")
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				new FileOutputStream(storeDir + File.separator + storeFile)
				);
		objectOutputStream.writeObject(user);
	}
	
	/**
	 * Finds the user's dat file from it's username and reads it.
	 * 
	 * @param username the username of the user that is to be read
	 * @return the user after it has been read
	 * @throws IOException Exception
	 * @throws ClassNotFoundException Exception
	 */
	public static User readUser(String username) throws IOException, ClassNotFoundException {
		// Serializable stuff
		String storeDir = "dat";
		String storeFile = username + ".dat";
		
		@SuppressWarnings("resource")
		ObjectInputStream objectInputStream = new ObjectInputStream(
				new FileInputStream(storeDir + File.separator + storeFile)
				);
		User user = (User)objectInputStream.readObject();
		// use ArrayList to update observable list
		user.observableAlbumList = FXCollections.observableArrayList(user.getAlbumList());
		return user;
	}
	
	/**
	 * Returns a String containing the users username and password
	 */
	public String toString() {
		return this.username + ", " + this.password;
	}
	
}
