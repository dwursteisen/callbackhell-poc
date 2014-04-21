package net.simonbasle.heaven.service;

import net.simonbasle.model.User;
import rx.Observable;

public class UserService {

	public static Observable<User> find(String userId) {
		return Observable.empty();
	}

}
