package com.niyo;

public abstract class ServiceCaller {
	
	private static final ServiceCaller EmptyCaller = new ServiceCaller() {
		public void success(Object data) {}
		public void failure(Object data, String description) {}
	};

	public abstract void success(Object data);
	public abstract void failure(Object data, String description);

	public static ServiceCaller getEmptyCaller() {
		return EmptyCaller;
	}

}
