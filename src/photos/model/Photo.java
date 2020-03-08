package photos.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Photo contains photo data and methods.  In order to view the tags,
 * openPhoto() must be called when the photo is opened
 */
public class Photo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String location;
	public Calendar date = Calendar.getInstance();
	private String caption = null;
	public HashMap<String, String> tags = new HashMap<String, String>();
	public transient ObservableList<String> observableTagList = FXCollections.observableArrayList();
	public HashSet<String> usedTags = new HashSet<String>();

	/**
	 * Constructor for Photo
	 * 
	 * @param name name of the photo
	 * @param location location of the photo file
	 * @param date date the photo was last modified
	 */
	public Photo(String name, String location, long date) {
		this.name = name;
		this.location = location;
		this.date.setTimeInMillis(date);
		this.date.set(Calendar.MILLISECOND, 0);	// this was specified in project specifications
	}
	
	/**
	 * Adds a tag to the HashMap of tags
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public void addTag(String key, String value) {
		if (key.isEmpty() || value.isEmpty()) {
			throw new IllegalArgumentException("error: missing key or value");
		}
		if (tags.containsKey(key)) {
			throw new IllegalArgumentException("error: there is already a tag with the key " + key);
		} else {
			tags.put(key, value);
			this.observableTagList.add(key + ", " + value);
		}
	}
	
	/**
	 * Removes the tag with the matching key
	 * 
	 * @param key the key of the tag to be removed
	 */
	public void removeTag(String key) {
		if (this.tags.containsKey(key)) {
			this.tags.remove(key);
			String check;
			for (String str : this.observableTagList) {
				check = str.split(",")[0];
				if (check.equals(key)) {
					this.observableTagList.remove(str);
					break;
				}
			}
		} else {
			throw new IllegalArgumentException("error: no tag found with key " + key);
		}
	}
	
	
	/**
	 * Called when a photo is viewed.  This method loads the HashMap tags into
	 * an ObservableList.
	 */
	public void openPhoto() {
		// when the photo is open after the program restarts 
		// this.observableTagList will be null because its transient so it
		// needs to be reset
		this.observableTagList = FXCollections.observableArrayList();
		for (String key : this.tags.keySet()) {
			this.observableTagList.add(key + ", " + tags.get(key));
		}
	}
	
	/**
	 * Called when a different photo is viewed, the user logs off, or the
	 * program is closed.  This method updates the HashMap tags from the
	 * ObservableList
	 */
	public void closePhoto() {
		// TODO
		// might be optional
		
	}
	
	/**
	 * Gets the ObservableList of tags in the format key, value
	 * 
	 * @return the ObservableList of tags
	 */
	public ObservableList<String> getObservableTagList() {
		return this.observableTagList;
	}
	
	/**
	 * Returns the date of the photo as a String
	 * 
	 * @return date of the photo as a String
	 */
	public String getDate() {
		return this.date.getTime().toString();
	}
	
	/**
	 * Gets the location of the photo
	 * 
	 * @return the location of the photo
	 */
	public String getLocation() {
		return this.location;
	}
	
	/**
	 * Get the name of the photo
	 * 
	 * @return name of the photo
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the photo
	 * 
	 * @param name name of the photo
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the caption
	 * 
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Set the caption
	 * 
	 * @param caption the caption
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	/**
	 * toString method
	 */
	public String toString() {
		return this.name;
	}
}
