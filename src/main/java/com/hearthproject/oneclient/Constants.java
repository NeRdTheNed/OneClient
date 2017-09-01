package com.hearthproject.oneclient;

import com.hearthproject.oneclient.fx.controllers.InstallLocation;
import com.hearthproject.oneclient.json.JsonUtil;
import com.hearthproject.oneclient.util.OperatingSystem;
import com.hearthproject.oneclient.util.files.FileUtil;
import com.hearthproject.oneclient.util.logging.OneClientLogging;
import javafx.application.Platform;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Constants {

	public static File TEMPDIR;
	public static File INSTANCEDIR;
	public static File LOGFILE;
	public static File ICONDIR;

	public static final String LIBRARIES_BASE = "https://libraries.minecraft.net/";
	public static final String RESOURCES_BASE = "http://resources.download.minecraft.net/";
	public static final String MAVEN_CENTRAL_BASE = "http://central.maven.org/maven2/";

	public static String[] INITIALIZE_DIRS = new String[] { "configs", "mods" };

	public static boolean CUSTOM_RUN = false;

	private static File RUN_DIR = null;

	public static void earlySetup(Runnable runnable) throws IOException {
		StaticSettings staticSettings = getSettings();
		if (staticSettings == null || !new File(staticSettings.installLocation).exists()) {
			Platform.runLater(() -> {
				try {
					InstallLocation.getInstallDir(file -> {
						RUN_DIR = file;
						setUpDirs();
						try {
							saveSettings(new StaticSettings(file.getAbsolutePath()));
						} catch (IOException e) {
							e.printStackTrace();
						}
						runnable.run();
						return true;
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} else {
			RUN_DIR = new File(staticSettings.installLocation);
			setUpDirs();
			runnable.run();
		}

	}

	public static void setUpDirs() {
		TEMPDIR = FileUtil.findDirectory(getRunDir(), "temp");
		INSTANCEDIR = FileUtil.findDirectory(getRunDir(), "instances");
		LOGFILE = FileUtil.findDirectory(getRunDir(), "log.txt");
		ICONDIR = FileUtil.findDirectory(Constants.TEMPDIR, "images");
		System.setProperty("logFilename", LOGFILE.toString());
		OneClientLogging.init();
	}

	public static File getRunDir() {
		return RUN_DIR;
	}

	public static String getVersion() {
		return Constants.class.getPackage().getImplementationVersion();
	}

	public static File getDefaultDir() {
		return new File(FileSystemView.getFileSystemView().getDefaultDirectory(), "OneClient");
	}

	public static File getInstallConfig() {
		return new File(OperatingSystem.getApplicationDataDirectory(), "settings.json");
	}

	public static StaticSettings getSettings() throws IOException {
		File config = getInstallConfig();
		if (config.exists()) {
			return JsonUtil.GSON.fromJson(org.apache.commons.io.FileUtils.readFileToString(config, StandardCharsets.UTF_8), StaticSettings.class);
		}
		return null;
	}

	public static void saveSettings(StaticSettings staticSettings) throws IOException {
		org.apache.commons.io.FileUtils.writeStringToFile(getInstallConfig(), JsonUtil.GSON.toJson(staticSettings), StandardCharsets.UTF_8);
	}

	public static class StaticSettings {
		public String installLocation;

		public StaticSettings(String installLocation) {
			this.installLocation = installLocation;
		}

		public StaticSettings() {
		}
	}

}