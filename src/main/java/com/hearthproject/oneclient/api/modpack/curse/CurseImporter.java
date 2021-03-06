package com.hearthproject.oneclient.api.modpack.curse;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.hearthproject.oneclient.api.modpack.IImporter;
import com.hearthproject.oneclient.api.modpack.Info;
import com.hearthproject.oneclient.api.modpack.Instance;
import com.hearthproject.oneclient.api.modpack.curse.data.CurseFullProject;
import com.hearthproject.oneclient.json.JsonUtil;
import com.hearthproject.oneclient.util.AsyncTask;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;

public class CurseImporter implements IImporter {

	private final static ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
	private final String projectID;
	private final URL url;
	private AsyncTask<CurseFullProject> data;

	public CurseImporter(String projectID) {
		this.projectID = projectID;
		url = Curse.getProjectURL(projectID);
		data = new AsyncTask<>(() -> JsonUtil.read(url, CurseFullProject.class));
		service.execute(data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Instance create() {
		String name = data.map(CurseFullProject::getName);

		List<CurseFullProject.Category> categories = data.map(CurseFullProject::getCategories);
		if (name == null)
			return null;

		Instance instance = new Instance(name, data.map(CurseFullProject::getWebSiteURL), new CurseInstaller(data.getIfPresent()),
			new Info("popularity", data.map(CurseFullProject::getPopularityScore)),
			new Info("authors", data.map(CurseFullProject::getAuthorsString)),
			new Info("categories", categories),
			new Info("downloads", data.map(CurseFullProject::getDownloads)),
			new Info("gameVersions", data.map(CurseFullProject::getVersions)),
			new Info("summary", data.map(CurseFullProject::getSummary)),
			new Info("icon-url", data.map(CurseFullProject::getIcon))
		);
		return instance;
	}


}
