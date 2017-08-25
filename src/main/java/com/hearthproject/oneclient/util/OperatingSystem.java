package com.hearthproject.oneclient.util;

import com.hearthproject.oneclient.util.logging.OneClientLogging;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;

//Based of the code from fabric-loom found here: https://github.com/FabricMC/fabric-loom
public class OperatingSystem {

	public static final String SYSTEM_ARCH = System.getProperty("os.arch").equals("64") ? "64" : "32";

	public static String getOS() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) {
			return "windows";
		} else if (osName.contains("mac")) {
			return "osx";
		} else {
			return "linux";
		}
	}

	public static String getArch() {
		if (is64Bit()) {
			return "64";
		} else {
			return "32";
		}
	}

	public static String getJavaDelimiter() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) {
			return ";";
		} else if (osName.contains("mac")) {
			return ":";
		} else {
			return ":";
		}
	}

	public static boolean is64Bit() {
		return System.getProperty("sun.arch.data.model").contains("64");
	}

	public static void openWithSystem(File file) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.OPEN)) {
				new Thread(() -> {
					try {
						desktop.open(file);
					} catch (IOException e) {
						OneClientLogging.log(e);
					}
				}).start();
			}
		}
	}

	public static long getOSTotalMemory() {
		return getOSMemory("getTotalPhysicalMemorySize", "Could not get RAM Value");
	}

	public static long getOSFreeMemory() {
		return getOSMemory("getFreePhysicalMemorySize", "Could not get free RAM Value");
	}

	//Seems to be the safest way to get memory ifo without it exploding.
	private static long getOSMemory(String methodName, String warning) {
		long ram = 0;
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		Method m;
		try {
			m = operatingSystemMXBean.getClass().getDeclaredMethod(methodName);
			m.setAccessible(true);
			Object value = m.invoke(operatingSystemMXBean);
			if (value != null) {
				ram = Long.valueOf(value.toString()) / 1024 / 1024;
			} else {
				OneClientLogging.log(warning);
				ram = 1024;
			}
		} catch (Exception e) {
			OneClientLogging.log(e);
		}

		return ram;
	}

}
