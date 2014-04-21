package net.simonbasle.heaven.service;

import java.util.List;

import net.simonbasle.model.Pic;
import net.simonbasle.model.PicMeta;
import rx.Observable;

public class PictureService {
	
	public static Observable<PicMeta> findAllMetas(List<Pic> pics) {
		return Observable.empty();
	}

}
