package com.hearthproject.oneclient.fx.nodes;

import com.hearthproject.oneclient.DownloadTask;
import com.hearthproject.oneclient.api.modpack.DownloadManager;
import com.hearthproject.oneclient.api.modpack.Instance;
import com.hearthproject.oneclient.api.modpack.curse.Curse;
import com.hearthproject.oneclient.api.modpack.curse.CurseInstaller;
import com.hearthproject.oneclient.api.modpack.curse.data.CurseFullProject;
import com.hearthproject.oneclient.util.MiscUtil;
import com.hearthproject.oneclient.util.files.ImageUtil;
import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class FeaturedTile extends StackPane {
	public final GaussianBlur blurEffect = new GaussianBlur(0);

	public final Instance instance;

	@FXML
	public Hyperlink modpackText;
	@FXML
	public ImageView imageView;
	@FXML
	public Text statusText;
	@FXML
	public JFXButton installButton;
	@FXML
	public StackPane nodePane;
	@FXML
	public ComboBox<CurseFullProject.CurseFile> files;

	public FeaturedTile(Instance instance) {
		if (instance == null)
			throw new NullPointerException("Missing Instance!");
		this.instance = instance;
		URL loc = Thread.currentThread().getContextClassLoader().getResource("gui/contentpanes/featured_tile.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(loc);
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		new Thread(() -> {
			Image image = ImageUtil.downloadAndOpenImage((String) instance.tempInfo.get("icon-url"), instance.getName());
			if (image != null) {
				MiscUtil.runLaterIfNeeded(() -> imageView.setImage(image));
			}
		}).start();
		imageView.setEffect(blurEffect);

		statusText.setText(instance.getGameVersion());
		statusText.setFill(Color.web("#FFFFFF"));
		nodePane.setOpacity(0F);

		if (instance.getInstaller() instanceof CurseInstaller) {
			CurseInstaller installer = (CurseInstaller) instance.getInstaller();
			MiscUtil.setupLink(modpackText, instance.getName(), Curse.getCurseForge(installer.projectId).toString());
			files.setItems(FXCollections.observableArrayList(installer.getFiles()));
			files.getSelectionModel().selectFirst();
			installer.setFile(files.getValue());
			files.valueProperty().addListener((v, a, b) -> installer.setFile(b));
		}

		DownloadTask task = DownloadManager.createDownload(instance.getName(), instance::install);

		installButton.setOnAction(event -> task.start());
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
		installButton.disableProperty().bind(instance.installingProperty());
		imageView.disableProperty().bind(instance.installingProperty());
		instance.installingProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				statusText.setText("Installing...");
			} else {
				statusText.setText(instance.getGameVersion());
			}
		});
	}

}
