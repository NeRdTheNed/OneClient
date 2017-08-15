package com.hearthproject.oneclient.util.launcher;

import com.hearthproject.oneclient.fx.SplashScreen;
import com.hearthproject.oneclient.json.JsonUtil;
import com.hearthproject.oneclient.json.models.launcher.ModPack;
import com.hearthproject.oneclient.json.models.launcher.ModPackList;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;

public class PackUtil {

	public static ModPackList packs;


	public static ModPackList loadModPacks() throws IOException {
		if (packs != null) {
			return packs;
		}
		SplashScreen.updateProgess("Downloading modpacks.json file", 40);
		String jsonStr = IOUtils.toString(new URL("http://hearthproject.uk/files/modpacks.json"));
		SplashScreen.updateProgess("Reading modpacks", 80);
		return packs = JsonUtil.GSON.fromJson(jsonStr, ModPackList.class);
	}

}
