package photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Album contains album data
 */
public class Album implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private Calendar date = Calendar.getInstance();
	private String Order = "ascending";
	private String Sortby = "name";
	private boolean temp = false;
	
	HashSet<String> photoNames = new HashSet<String>();
	ArrayList<Photo> photoList = new ArrayList<Photo>();
	public transient ObservableList<Photo> observablePhotoList = FXCollections.observableArrayList();

	
	/**
	 * Constructor for albums
	 * 
	 * @param name name of album
	 */
	public Album(String name) {
		this.name = name;
		this.date.setTime(date.getTime());
	}
	
	/**
	 * Creates an instance of Photo
	 * 
	 * @param name name of photo
	 * @param location location of file
	 * @param date date last modified
	 */
	public void createPhoto(String name, String location, long date) {
		Photo photo = new Photo(name, location, date);
		this.observablePhotoList.add(photo);
	}
	
	/**
	 * Removes a photo from the album
	 * 
	 * @param photo the photo
	 */
	public void removePhoto(Photo photo) {
		this.photoNames.remove(photo.getName());
		this.photoList.remove(photo);
		this.observablePhotoList.remove(photo);
	}
	
	
	/**
	 * This is used when the album is selected in the ListView.  This turns the ArrayList of photos into an ObservableList.
	 */
	public void openAlbum() {
		this.observablePhotoList = FXCollections.observableArrayList(this.getPhotoList());
	}
	
	/**
	 * This is used when the selected album is closed or the program exits.  This updates the ArrayList from the ObservableList.
	 */
	public void closeAlbum() {
		this.setPhotoList(new ArrayList<Photo>(this.observablePhotoList));
	}
	
	/**
	 * Getter function for photoList
	 * 
	 * @return ArrayList of photos
	 */
	public ArrayList<Photo> getPhotoList() {
		return this.photoList;
	}
	
	/**
	 * Setter function for photoList
	 * 
	 * @param photoList ArrayList of photos
	 */
	public void setPhotoList(ArrayList<Photo> photoList) {
		this.photoList = photoList;
	}
	
	/**
	 * Get the name of the album
	 * 
	 * @return name of album
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the date of the album
	 * 
	 * @return the date
	 */
	public Calendar getDate() {
		return this.date;
	}
	
	/**
	 * Set the name of the album
	 * 
	 * @param name new name of album
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the Album's name
	 */
	public String toString() {
		return this.name;
	}
	
	/**
	 * Getter function for what the album is being sorted by
	 * 
	 * @return what the album is being sorted by
	 */
	public String getSortby() {
		return Sortby;
	}
	
	/**
	 * Setter function for what the album is being sorted by
	 * 
	 * @param sortby what the album is being sorted by
	 */
	public void setSortby(String sortby) {
		Sortby = sortby;
	}

	/**
	 * Getter function for the order the album is being displayed by
	 * 
	 * @return the order
	 */
	public String getOrder() {
		return Order;
	}
	
	/**
	 * Setter function for the order the album is being displayed by
	 * 
	 * @param order the order
	 */
	public void setOrder(String order) {
		Order = order;
	}
	
	/**
	 * Set true for this album to be temporary
	 * @param temp boolean whether to be temporary
	 */
	public void setTemp(boolean temp) {
		this.temp = temp;
	}
	
	/**
	 * Whether this is a temporary album
	 * @return true if this is temporary
	 */
	public boolean isTemp() {
		return this.temp;
	}
}
