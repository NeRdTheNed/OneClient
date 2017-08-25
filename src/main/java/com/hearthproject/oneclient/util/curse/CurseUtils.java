package com.hearthproject.oneclient.util.curse;

import com.google.common.collect.Maps;
import com.hearthproject.oneclient.util.logging.OneClientLogging;
import javafx.scene.image.Image;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CurseUtils {
    public static final Map<String, Image> IMAGE_CACHE = Maps.newHashMap();
    public static final String CURSE_BASE = "https://mods.curse.com";
    public static final String CURSEFORGE_BASE = "https://minecraft.curseforge.com";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; rv:50.0) Gecko/20100101 Firefox/50.0";

    public static Filter versionFilter(String value) {
        return new Filter("filter-project-game-version", value);
    }

    public static Filter page(String value) {
        return new Filter("page", value).setPrefix("&");
    }

    public static List<CursePack> getPacks(int page) {
        Document d = CurseUtils.getHtml(CurseUtils.CURSE_BASE, "/modpacks/minecraft", versionFilter("1.12"), page(Integer.toString(page)));
        Elements packs = d.select("#addons-browse").first().select("ul > li > ul");
        return packs.stream().map(CursePack::new).collect(Collectors.toList());
    }

    public static Document getHtml(String url, String path, Filter... filters) {
        try {
            String filter = Stream.of(filters).map(f -> f.get()).collect(Collectors.joining());
            String finalURL = url + path + filter;
            OneClientLogging.log(finalURL);
            return Jsoup.connect(finalURL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Filter {
        private String key, value;
        private String prefix = "?";
        public Filter(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public Filter setPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }
        public String get() {
            if (value.isEmpty())
                return "";
            return String.format("%s%s=%s", prefix,key, value);
        }
    }

}
