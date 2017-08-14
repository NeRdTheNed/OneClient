package com.hearthproject.oneclient.util.forge;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.OutputSupplier;
import com.hearthproject.oneclient.Constants;
import com.hearthproject.oneclient.json.JsonUtil;
import com.hearthproject.oneclient.json.models.forge.ForgeVersions;
import com.hearthproject.oneclient.util.ClasspathUtils;
import com.hearthproject.oneclient.util.logging.OneClientLogging;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.jar.JarFile;

public class ForgeUtils {

	private static ForgeVersions forgeVersions = null;

	public static InputStream VERSION_PROFILE;

	private static String forgeVer;

	//1.12-14.21.1.2443
	public static void installForge(File file, String forgeVersion) throws IOException {
		loadForgeVerions();
		forgeVer = forgeVersion;

		ForgeVersions.ForgeVersion version = getForgeVersion(forgeVersion);

		//http://files.minecraftforge.net/maven/net/minecraftforge/forge/1.12-14.21.1.2443/forge-1.12-14.21.1.2443-installer.jar
		//http://files.minecraftforge.net/maven/net/minecraftforge/forge/1.7.10-10.13.4.1614-1.7.10/forge-1.7.10-10.13.4.1614-1.7.10-installer.jar
		URL forgeInstallerURL = new URL("http://files.minecraftforge.net/maven/net/minecraftforge/forge/" + forgeVersion + "/forge-" + forgeVersion + "-installer.jar");

		//Sets the url to work with branched out forges
		if(version.branch != null && !version.branch.isEmpty()){
			forgeInstallerURL = new URL("http://files.minecraftforge.net/maven/net/minecraftforge/forge/" +  forgeVersion + "-" + version.branch + "/forge-" + forgeVersion + "-" + version.branch + "-installer.jar");
		}

		File forgeInstaller = new File(Constants.TEMPDIR, "forge-installer-" + forgeVersion + ".jar");
		if (!forgeInstaller.exists()) {
			FileUtils.copyURLToFile(forgeInstallerURL, forgeInstaller);
		}
		JarFile jarFile = new JarFile(forgeInstaller);
		OneClientLogging.log("Reading install_profile.json from " + file.getName());

		VERSION_PROFILE = jarFile.getInputStream(jarFile.getEntry("install_profile.json"));
		//Doing this seems to not let it break? Not sure why but I will roll with it
		String jsonSTR = IOUtils.toString(VERSION_PROFILE, StandardCharsets.UTF_8);
		VERSION_PROFILE = IOUtils.toInputStream(jsonSTR, StandardCharsets.UTF_8);
		ForgeInstaller.installForge(file);
	}

	public static ForgeVersions.ForgeVersion getForgeVersion(String version){
		for(ForgeVersions.ForgeVersion forgeVersion : forgeVersions.number.values()){
			if(forgeVersion.version.equalsIgnoreCase(version.split("-")[1])){
				return forgeVersion;
			}
		}
		return null;
	}


	public static void downloadForgeJar(File path)  {
		try {
			ForgeVersions.ForgeVersion version = getForgeVersion(forgeVer);
			OneClientLogging.log("Downloading forge jar to " + path.getAbsolutePath());
			URL forgeJar = new URL("http://files.minecraftforge.net/maven/net/minecraftforge/forge/" + forgeVer + "/forge-" + forgeVer + "-universal.jar");
			if(version.branch != null && !version.branch.isEmpty()){
				forgeJar = new URL("http://files.minecraftforge.net/maven/net/minecraftforge/forge/" +  forgeVer + "-" + version.branch + "/forge-" + forgeVer + "-" + version.branch + "-universal.jar");
			}
			FileUtils.copyURLToFile(forgeJar, path);
		} catch (Throwable throwable){
			OneClientLogging.log(throwable);
		}
	}

	public static void downlaodMinecraftJar(){

	}


	//http://files.minecraftforge.net/maven/net/minecraftforge/forge/json
	public static ForgeVersions loadForgeVerions() throws IOException {
		if (forgeVersions != null) {
			return forgeVersions;
		}
		String jsonStr = IOUtils.toString(new URL("http://files.minecraftforge.net/maven/net/minecraftforge/forge/json"));
		forgeVersions = JsonUtil.GSON.fromJson(jsonStr, ForgeVersions.class);
		return forgeVersions;
	}

}
