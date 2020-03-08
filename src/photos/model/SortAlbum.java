package photos.model;

import java.util.Comparator;

/**
 * Comparator class for sorting albums
 */
public class SortAlbum implements Comparator<Album> {
	PhotoGallery photoGallery;
	
	/**
	 * Constructor for SortAlbum, sets the photoGallery
	 * 
	 * @param photoGallery photo gallery
	 */
	public SortAlbum(PhotoGallery photoGallery) {
		this.photoGallery = photoGallery;
	}
	
	/**
	 * Compare function for the Comparator
	 */
	@Override
	public int compare(Album a1, Album a2) {
		if (this.photoGallery.getAlbumOrder().equals("ascending")) {
			if (this.photoGallery.getAlbumSortby().equals("name")) {
				return a1.getName().compareTo(a2.getName());
			} else {
				return a1.getDate().compareTo(a2.getDate());
			}
		} else {
			if (this.photoGallery.getAlbumSortby().equals("name")) {
				return a2.getName().compareTo(a1.getName());
			} else {
				return a2.getDate().compareTo(a1.getDate());
			}
		}
	}
}
