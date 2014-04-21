package net.simonbasle.model;

import java.util.List;

public interface Document {
	public int getId();
	public String getText();
	public boolean isStarred();
	public List<Pic> getPictures();
}
