package com.hearthproject.oneclient.json.models.launcher;

public class Instance {

	public String name;

	public String minecraftVersion;

	public String modLoader;

	public String modLoaderVersion;

	public String icon;

	public long lastLaunch;

	public Instance(String name) {
		this.name = name;
		icon = "";
	}

}