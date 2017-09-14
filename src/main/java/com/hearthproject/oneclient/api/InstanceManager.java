package com.hearthproject.oneclient.api;

import com.hearthproject.oneclient.Constants;
import com.hearthproject.oneclient.fx.SplashScreen;
import com.hearthproject.oneclient.fx.contentpane.ContentPanes;
import com.hearthproject.oneclient.fx.nodes.InstanceTile;
import com.hearthproject.oneclient.json.JsonUtil;
import com.hearthproject.oneclient.util.MiscUtil;
import com.hearthproject.oneclient.util.files.FileUtil;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InstanceManager {

	protected static Map<String, Instance> instances = new HashMap<>();

	public static Collection<Instance> getInstances() {
		return instances.values();
	}

	public static Instance getInstance(String name) {
		return instances.get(name);
	}

	public static void addInstance(Instance instance) {
		//TODO check its unique
		for (Instance i : instances.values()) {
			int j = 2;
			while (instance.getName().equals(i.getName())) {
				instance.setName(instance.getName() + "i" + j);
			}
		}
		instances.put(instance.getName(), instance);
		save();
		init(instance);
	}

	public static void save() {
		instances.values().forEach(Instance::save);
	}

	public static void init(Instance instance) {
		File instanceDir = instance.getDirectory();
		for (String dir : Constants.INITIALIZE_DIRS) {
			FileUtil.findDirectory(instanceDir, dir);
		}
	}

	public static void load() {
		SplashScreen.updateProgess("Loading instances", 10);
		instances.clear();
		Arrays.stream(Constants.INSTANCEDIR.listFiles()).filter(File::isDirectory).forEach(dir -> {
			Instance instance = load(dir);
			if (instance != null)
				instances.put(instance.getName(), instance);
		});
	}

	private static Instance load(File dir) {

		//Todo legacy loading??? probably a lot of work this time around.

		return JsonUtil.read(new File(dir, "instance.json"), Instance.class);
	}

	public static void setInstanceInstalling(Instance instance, boolean installing) {
		MiscUtil.runLaterIfNeeded(() -> {
			ContentPanes.INSTANCES_PANE.refresh();
			for (InstanceTile tile : ContentPanes.INSTANCES_PANE.instanceTiles) {
				if (tile.instance.getName().equals(instance.getName())) {
					tile.setInstalling(installing);
				}
			}
		});
	}

	public static void removeInstance(Instance instance) {
		save();
	}

}