package com.hearthproject.oneclient.api.modpack;

public class Info {
	private final String key;
	private Object value;

	public Info(String key, Object value) {
		this.key = key;
		this.value = value;
	}
}