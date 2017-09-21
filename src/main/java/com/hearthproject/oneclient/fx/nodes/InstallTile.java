package com.hearthproject.oneclient.fx.nodes;

import com.hearthproject.oneclient.DownloadTask;
import com.hearthproject.oneclient.api.DownloadManager;
import com.hearthproject.oneclient.api.Instance;
import com.hearthproject.oneclient.api.curse.CurseInstaller;
import com.hearthproject.oneclient.api.curse.data.CurseFullProject;
import com.hearthproject.oneclient.util.MiscUtil;
import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class InstallTile extends HBox implements Comparable<InstallTile> {
	@FXML
	protected ImageView imageView;
	@FXML
	protected Hyperlink title;
	@FXML
	protected JFXButton buttonInstall;
	@FXML
	protected StackPane nodePane;
	@FXML
	protected ComboBox<CurseFullProject.CurseFile> comboFile;
	@FXML
	protected VBox left, right;

	protected Instance instance;

	public InstallTile(Instance instance) {
		this.instance = instance;

		URL loc = Thread.currentThread().getContextClassLoader().getResource("gui/contentpanes/install_tile.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(loc);
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		MiscUtil.setupLink(title, instance.getName(), instance.tempInfo.get("websiteUrl").toString());
		new Thread(() -> MiscUtil.runLaterIfNeeded(() -> imageView.setImage(instance.getImage()))).start();
		if (instance.getInstaller() instanceof CurseInstaller) {
			CurseInstaller installer = (CurseInstaller) instance.getInstaller();
			comboFile.setVisible(true);
			comboFile.setItems(FXCollections.observableArrayList(installer.getFiles()));
			comboFile.getSelectionModel().selectFirst();
			installer.setFile(comboFile.getValue());
			comboFile.valueProperty().addListener((v, a, b) -> installer.setFile(b));
		}
		Label downloads = info("Downloads: %s", MiscUtil.formatNumbers((double) instance.tempInfo.get("downloads")));
		Label gameVersions = info("Versions: %s", instance.tempInfo.get("gameVersions"));
		right.setAlignment(Pos.BASELINE_RIGHT);
		right.getChildren().addAll(gameVersions, downloads);
		//		right.getChildren().addAll(((List<CurseProject.Category>)instance.info.get("categories")).stream().map(CurseProject.Category::getNode).collect(Collectors.toList()));
		left.getChildren().addAll(info("By %s", instance.tempInfo.get("authors")));

		DownloadTask task = DownloadManager.createDownload(instance.getName(), instance::install);
		buttonInstall.setOnAction(event -> {
			task.start();
			buttonInstall.setDisable(true);
		});
		task.setOnSucceeded(event -> buttonInstall.setDisable(false));
		nodePane.setOpacity(0F);
		nodePane.hoverProperty().addListener((observable, oldValue, newValue) -> {
			FadeTransition fadeTransition = new FadeTransition(new Duration(400), nodePane);
			if (newValue) {
				fadeTransition.setFromValue(0F);
				fadeTransition.setToValue(1F);
				fadeTransition.play();
				nodePane.setOpacity(1F);
			} else {
				fadeTransition.setFromValue(1F);
				fadeTransition.setToValue(0F);
				fadeTransition.play();
				nodePane.setOpacity(0F);
			}
		});
		imageView.disableProperty().bind(instance.installingProperty());
		instance.installingProperty().addListener((observable, oldValue, newValue) -> {
			//			if (newValue) {
			//				statusText.setText("Installing...");
			//			} else {
			//				statusText.setText(instance.getGameVersion());
			//			}
		});
	}

	public Label info(String format, Object... params) {
		return info(String.format(format, params));
	}

	public Label info(String value) {
		Label l = new Label(value);
		l.setTextFill(Color.web("#FFFFFF"));
		l.setId("oc-info-label");
		return l;
	}

	private String getName() {
		return instance.name.trim();
	}

	@Override
	public int compareTo(InstallTile o) {
		return getName().compareTo(o.getName());
	}
}
