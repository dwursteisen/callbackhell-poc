package net.simonbasle.hell;

public interface Callback<T> {
	public void onSuccess(T result) throws InterruptedException;
}
