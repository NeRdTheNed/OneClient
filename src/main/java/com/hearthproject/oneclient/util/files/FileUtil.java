package com.hearthproject.oneclient.util.files;

import com.hearthproject.oneclient.util.logging.OneClientLogging;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.tukaani.xz.XZInputStream;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class FileUtil {

    public static String encode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File createDirectory(File path) {
        if (path == null)
            return null;

        if (!path.exists()) {
            path.mkdirs();
            OneClientLogging.info("Creating Directory {}", path);
        }
        return path;
    }

    public static File findDirectory(File path, String dir) {
        return createDirectory(getDirectory(path, dir));
    }

    public static File getDirectory(File path, String dir) {
        try {
            return new File(path, URLEncoder.encode(dir, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            OneClientLogging.error(e);
        }
        return null;
    }

    public static File createFile(File file) {
        if (!file.exists()) {
            createDirectory(new File(file.getParent()));
            try {
                file.createNewFile();
            } catch (IOException e) {
                OneClientLogging.error(e);
            }
        }
        return file;
    }

    public static File findFile(File parent, String file) {
        File f = new File(parent, file);
        return createFile(f);
    }

    public static File extractZip(File from, File to) {
        try {
            ZipFile zip = new ZipFile(from);
            zip.extractAll(to.toString());
        } catch (ZipException e) {
            OneClientLogging.error(e);
        }
        return to;
    }

    public static File extractXZ(File from, File to) {
        try {
            FileInputStream fin = new FileInputStream(from);
            BufferedInputStream in = new BufferedInputStream(fin);
            FileOutputStream out = new FileOutputStream(to);
            XZInputStream xzIn = new XZInputStream(in);
            final byte[] buffer = new byte[8192];
            int n;
            while (-1 != (n = xzIn.read(buffer))) {
                out.write(buffer, 0, n);
            }
            out.close();
            xzIn.close();
        } catch (Exception e) {
            OneClientLogging.error(e);
        }
        return to;
    }

    public static File extract(File from, File to) {
        if (!to.exists()) {
            if (FilenameUtils.isExtension(from.toString(), "zip")) {
                return extractZip(from, to);
            }
            if (FilenameUtils.isExtension(from.toString(), "xz")) {
                return extractXZ(from, to);
            }
        }
        return to;
    }

    public static File extractFromURL(String url, File location) {
        File zipDownload = new File(location, FilenameUtils.getName(url));
        File file = downloadFromURL(url, zipDownload);
        File directory = new File(location, FilenameUtils.getBaseName(url));
        return extract(file, directory);
    }

    public static File downloadToName(String url, File location) {
        String name = FilenameUtils.getName(url);
        return downloadFromURL(url.replace(" ", "%20"), new File(location, name));
    }

    public static File downloadFromURL(String url, File location) {
        try {
            downloadFromURL(new URL(url), location);
        } catch (MalformedURLException e) {
            OneClientLogging.error(e);
        }
        return location;
    }

    public static File downloadFromURL(URL url, File location) {
        try {
            FileUtils.copyURLToFile(url, location);
        } catch (IOException e) {
            OneClientLogging.error(e);
        }
        return location;
    }

    public static InputStream getResourceStream(String resource) {
        if (resource == null)
            return null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResourceAsStream(resource);
    }

    public static URL getResource(String resource) {
        if (resource == null)
            return null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResource(resource);
    }

    public static boolean isDirectoryEmpty(File dir) {
        return dir.isDirectory() && (dir.listFiles() == null || dir.listFiles().length == 0);
    }

    public static void upload() {

    }

    public static File getSubdirectory(File file, int n) {
        File[] files = file.listFiles(File::isDirectory);
        if (files.length > n)
            return files[n];
        return null;
    }


    public static String createMD5Hash(File file) {
        String md5 = null;
        try (FileInputStream stream = new FileInputStream(file)) {
            md5 = DigestUtils.md5Hex(stream);
            stream.close();
        } catch (IOException e) {
            OneClientLogging.error(e);
        }
        return md5;
    }
}
