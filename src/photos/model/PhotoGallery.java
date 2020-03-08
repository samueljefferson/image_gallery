package photos.model;

// TODO hardcode stock photos to stock user

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * PhotoGallery contains an ArrayList of users
 */
public class PhotoGallery implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private HashSet<String> usernamesSet = new HashSet<String>();
	private ArrayList<User> userList = new ArrayList<User>();
	public transient ObservableList<User> observableUserList = FXCollections.observableArrayList();
	public HashSet<String> tagSet = new HashSet<String>();
	
	
	private String albumOrder = "ascending";
	private String albumSortby = "name";
	private String photoOrder = "ascending";
	private String photoSortby = "name";
	
	// Serializable stuff
	public static final String storeDir = "dat";
	public static final String storeFile = "photoGallery.dat";
	
	String username;
	String password;
	
	User admin = null;
	User stock = null;
	
	User currentUser = null;
	Album currentAlbum = null;
	Photo currentPhoto = null;
	

	/**
	 * Constructor for PhotoGallery.  Sets up the accounts for admin and stock.
	 */
	public PhotoGallery() {
		this.admin = new User("admin", "admin");
		userList.add(this.admin);
		usernamesSet.add("admin");
		this.stock = new User("stock", "stock");
		userList.add(this.stock);
		usernamesSet.add("stock");
		// crawl through dat directory to get all the users
		this.loadUsers();
		// create observable list of users from ArrayList
		this.createObservableUserList();
		// add default tags to tagSet
		tagSet.add("location");
		tagSet.add("person");
	}
	
	/**
	 * Logs the current user into PhotoGallery.
	 * 
	 * @param username the user's name
	 * @param password the user's password
	 */
	public void login(String username, String password) {
		this.username = username;
		this.password = password;
		boolean success = false;
		
		try {
			userList.add(User.readUser(username));
		} catch (Exception e) {
		} 
		for (User user : userList) {
			if (user.getUsername().equals(this.username)  && user.getPassword().equals(this.password)) {
				currentUser = user;
				success = true;
			}
		}
		if (!success) {
			throw new IllegalArgumentException("error: unable to login to " + this.username + " with " + this.password + " password");
		}
	}
	
	/**
	 * Sorts the albums by name or date and specified order
	 */
	public void sortAlbums() {
		Collections.sort(this.getCurrentUser().observableAlbumList, new SortAlbum(this));
	}
	
	/**
	 * Sorts the photos by name or date and specified order
	 */
	public void sortPhotos() {
		Collections.sort(this.getCurrentAlbum().observablePhotoList, new SortPhoto(this));
	}
	
	/**
	 * Creates a user with the given username and password.  Then saves it to a
	 * dat file.
	 * 
	 * @param username the username
	 * @param password the password
	 * @return the created user
	 */
	public User createUser(String username, String password) {
		// check HashSet for username
		if (usernamesSet.contains(username)) {
			throw new IllegalArgumentException("error: username " + username + " is already in use");
		}
		this.usernamesSet.add(username);
		// create user and add user to observable list
		User user = new User(username, password);
		this.getObservableUserList().add(user);
		// save user dat
		try {
			User.writeUser(user);
			return user;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return user;
		
	}
	
	/**
	 * Deletes selected user
	 * 
	 * @param user the user to be deleted
	 */
	public void deleteUser(User user) {
		if (user.getUsername().equals("admin") || user.getUsername().equals("stock")) {
			throw new IllegalArgumentException("error: can't delete admin or stock account");
		} else {
			this.usernamesSet.remove(user.getUsername());
			this.userList.remove(user);
			this.createObservableUserList();
			// find the dat and delete that too
			File dir = new File(storeDir);
			for (File file : dir.listFiles()) {
				if (file.getName().split("\\.", 2)[0].equals(user.getUsername())) {
					file.delete();
				}
			}
		}
	}
	
	/**
	 * Reads all the users from that were saved in the dat directory and adds them to the userList.
	 */
	public void loadUsers() {
		// reset the userList and usernamesSet
		this.userList.clear();
		this.usernamesSet.clear();
		this.userList.add(this.admin);
		this.usernamesSet.add("admin");
		this.userList.add(this.stock);
		this.usernamesSet.add("stock");
		
		String filename;
		File dir = new File(storeDir);
		if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();
        }
		
		for (File file : dir.listFiles()) {
			filename = file.getName().split("\\.", 2)[0];
			
			if (!filename.equalsIgnoreCase("admin") && !filename.equalsIgnoreCase("stock")) {
				try {
					User user = User.readUser(filename);
					this.userList.add(user);
					this.usernamesSet.add(user.getUsername());
				} catch (Exception e) {
					e.printStackTrace();
				} 
			} 
			// update the ObservableList of users from the ArrayList of users 
			this.createObservableUserList();
		}
	}
	
	/**
	 * Create an ObservableList of users from the userList
	 */
	public void createObservableUserList() {
		this.observableUserList = FXCollections.observableArrayList(this.userList);
	}
	
	/**
	 * Getter method for the ObservableList of users
	 * 
	 * @return the ObservableList of users
	 */
	public ObservableList<User> getObservableUserList() {
		return observableUserList;
	}
	
	/**
	 * Get the order the albums are sorted by.
	 * 
	 * @return the order
	 */
	public String getAlbumOrder() {
		return this.albumOrder;
	}

	/**
	 * Set the order the albums are sorted by.
	 * 
	 * @param albumOrder the order
	 */
	public void setAlbumOrder(String albumOrder) {
		System.out.println("setting album order to " + albumOrder);
		this.albumOrder = albumOrder;
	}

	/**
	 * Get the order the photos are sorted by.
	 * 
	 * @return the order
	 */
	public String getPhotoOrder() {
		return this.photoOrder;
	}

	/**
	 * Set the order the photos are sorted by.
	 * 
	 * @param photoOrder the order
	 */
	public void setPhotoOrder(String photoOrder) {
		this.photoOrder = photoOrder;
	}
	
	/**
	 * Gets the current album
	 * 
	 * @return the current album
	 */
	public Album getCurrentAlbum() {
		return this.currentAlbum;
	}
	
	/**
	 * Sets the current album
	 * @param currentAlbum the currently selected album
	 */
	public void setCurrentAlbum(Album currentAlbum) {
		this.currentAlbum = currentAlbum;
	}
	
	/**
	 * Gets the current photo
	 * 
	 * @return the current photo
	 */
	public Photo getCurrentPhoto() {
		return this.currentPhoto;
	}

	/**
	 * Sets the current photo
	 * 
	 * @param currentPhoto the current photo
	 */
	public void setCurrentPhoto(Photo currentPhoto) {
		this.currentPhoto = currentPhoto;
	}
	
	/**
	 * Gets the admin
	 * 
	 * @return the admin
	 */
	public User getAdmin() {
		return this.admin;
	}
	
	/**
	 * Gets the stock photo account
	 * 
	 * @return the stock photo account
	 */
	public User getStock() {
		return this.stock;
	}
	
	/**
	 * Gets the current user
	 * 
	 * @return the current user
	 */
	public User getCurrentUser() {
		return this.currentUser;
	}
	
	/**
	 * Gets the ArrayList of users
	 * 
	 * @return the ArrayList of users
	 */
	public ArrayList<User> getUserList() {
		return this.userList;
	}

	/**
	 * Sets the ArrayList of users
	 * 
	 * @param userList the ArrayyList of users
	 */
	public void setUserList(ArrayList<User> userList) {
		this.userList = userList;
	}
	
	/**
	 * Gets what the albums are to be sorted by
	 * 
	 * @return a String with the characteristic the albums are being sorted by
	 */
	public String getAlbumSortby() {
		return albumSortby;
	}

	/**
	 * Sets what the albums are to be sorted by
	 * 
	 * @param albumSortby A String, either name or date
	 */
	public void setAlbumSortby(String albumSortby) {
		this.albumSortby = albumSortby;
	}

	/**
	 * Gets what the photos are to be sorted by
	 * 
	 * @return a String with the characteristic the photos are being sorted by
	 */
	public String getPhotoSortby() {
		return photoSortby;
	}

	/**
	 * Sets what the photos are to be sorted by
	 * 
	 * @param photoSortby A String, either name or date
	 */
	public void setPhotoSortby(String photoSortby) {
		this.photoSortby = photoSortby;
	}
	
}
