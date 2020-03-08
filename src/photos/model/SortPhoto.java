package photos.model;

import java.util.Comparator;

/**
 * Comparator class for sorting photos
 */
public class SortPhoto implements Comparator<Photo> {
	PhotoGallery photoGallery;
	
	/**
	 * Constructor for SortPhoto, sets the photoGallery
	 * 
	 * @param photoGallery the photoGallery
	 */
	public SortPhoto(PhotoGallery photoGallery) {
		this.photoGallery = photoGallery;
	}

	/**
	 * Compare function for the Comparator
	 */
	@Override
	public int compare(Photo p1, Photo p2) {
		if (this.photoGallery.getPhotoOrder().equals("ascending")) {
			if (this.photoGallery.getPhotoSortby().equals("name")) {
				return p1.getName().compareTo(p2.getName());
			} else {
				return p1.getDate().compareTo(p2.getDate());
			}
		} else {
			if (this.photoGallery.getPhotoSortby().equals("name")) {
				return p2.getName().compareTo(p1.getName());
			} else {
				return p2.getDate().compareTo(p1.getDate());
			}
		}
	}
}
