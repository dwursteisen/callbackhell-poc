package net.simonbasle.heaven.service;

import net.simonbasle.model.Comment;
import net.simonbasle.model.Document;
import rx.Observable;

public class CommentService {
	
	public static Observable<Comment> findForDoc(Document doc) {
		return Observable.empty();
	}
}
