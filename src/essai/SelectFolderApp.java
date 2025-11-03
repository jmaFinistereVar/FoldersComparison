package essai;
import java.io.File;
import java.nio.file.Path;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SelectFolderApp extends Application {
	Path sourcePath = null;
	Path destPath = null;
	Path logFile = null;
	ComparatorEventHandler launchCompareButtonEventHandler;
	Button launchCompareButton = new Button();

	ProgressBar progressBar = new ProgressBar(0); 
	VBox mainLayout = new VBox(15);
	FoldersComparator fc;
	private boolean avecChat = false;
	private static int LINE_LAYOUT_WIDTH = 30;

	private static final String PREF_KEY_SOURCE = "source_path";
	private static final String PREF_KEY_DEST = "dest_path";
	private static final String PREF_KEY_LOG_FILE = "logFile";


	private final Preferences prefs = Preferences.userNodeForPackage(SelectFolderApp.class);





	private void selecFolder(Stage primaryStage, Button button,
			FolderType folderType) {
		button.setOnAction(e -> {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Sélectionner un dossier");
			if (folderType == folderType.LOG) {
				directoryChooser.setTitle("Sélectionner un fichier");
			}


			File selectedDirectory = directoryChooser.showDialog(primaryStage);

			if (folderType == FolderType.LOG) {
				logFile = Path.of(selectedDirectory.getAbsolutePath());
				logFile = logFile.resolve("log.txt");
				prefs.put(PREF_KEY_LOG_FILE, logFile.toString());
				/*if (!Files.isRegularFile(logFile)) {
					throw new IllegalArgumentException("Not a writable file");
				}*/
			} else {
				launchCompareButton.setText(getText(selectedDirectory, folderType));
			}
			if ((sourcePath != null) && (destPath != null) && (logFile != null)) {
				launchCompareButton.setDisable(false);
			}
			updateHandler();

		});
	}

	private String getText(File selectedDirectory, FolderType folderType) {

		return switch (folderType) {
		case SOURCE -> {
			StringBuilder sb = new StringBuilder("Lancer la comparaison des dossiers \n\t");
			sourcePath = Path.of(selectedDirectory.getAbsolutePath());
			prefs.put(PREF_KEY_SOURCE, sourcePath.toString());

			sb.append(getText(sourcePath));
			sb.append("\n\t");
			sb.append(getText(destPath));
			yield  sb.toString();
		}
		case DEST -> {
			StringBuilder sb = new StringBuilder("Lancer la comparaison des dossiers \n\t");
			sb.append(getText(sourcePath));
			sb.append("\n\t");
			destPath = Path.of(selectedDirectory.getAbsolutePath());
			prefs.put(PREF_KEY_DEST, destPath.toString());

			sb.append(getText(destPath));
			yield  sb.toString();
		}
		case LOG -> {

			throw new IllegalArgumentException();
		}
		};
	}

	private String getText(Path path) {
		return path == null ? "Aucun fichier sélectionné" : path.toString();
	}

	@Override
	public void start(Stage primaryStage) {

		if (null != prefs.get(PREF_KEY_SOURCE, null)) {
			sourcePath = Path.of(prefs.get(PREF_KEY_SOURCE, null));
		}
		if (null != prefs.get(PREF_KEY_DEST, null)) {
			destPath = Path.of(prefs.get(PREF_KEY_DEST, null));
		}
		if (null != prefs.get(PREF_KEY_LOG_FILE, null)) {
			logFile = Path.of(prefs.get(PREF_KEY_LOG_FILE, null));
		}

		primaryStage.setTitle("Sélectionner un dossier");

		Button sourceButton = new Button("Choisir un dossier source");
		progressBar.setPrefWidth(300);
		if ((sourcePath == null) || (destPath == null) || (logFile == null)) {
			launchCompareButton.setText("Selectionner des dossiers pour lancer la comparaison");
			launchCompareButton.setDisable(true);
		} else {
			StringBuilder sb = new StringBuilder("Lancer la comparaison des dossiers \n\t");
			sb.append(getText(sourcePath));
			sb.append("\n\t");
			sb.append(getText(destPath));

			launchCompareButton.setText(sb.toString());
			updateHandler();

		}



		selecFolder(primaryStage, sourceButton, FolderType.SOURCE);

		Button destButton = new Button("Choisir un dossier destination");
		selecFolder(primaryStage, destButton, FolderType.DEST);

		Button logButton = new Button("Choisir un fichier de log");
		selecFolder(primaryStage, logButton, FolderType.LOG);

		CheckBox cb = new CheckBox("Avec chat");
		cb.setOnAction(ev -> {
			avecChat = cb.isSelected();
			updateHandler();
		});
		cb.setSelected(false);


		HBox firstLineLayout = new HBox(LINE_LAYOUT_WIDTH);
		firstLineLayout.getChildren().addAll(sourceButton, destButton, logButton);

		HBox secondLineLayout = new HBox(LINE_LAYOUT_WIDTH);
		secondLineLayout.getChildren().addAll(launchCompareButton, cb);


		mainLayout.setStyle("-fx-padding: 20; -fx-alignment: center;");
		mainLayout.getChildren().addAll(firstLineLayout,secondLineLayout, progressBar);



		Scene scene = new Scene(mainLayout, 400, 150);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void updateHandler() {
		launchCompareButtonEventHandler = new ComparatorEventHandler(sourcePath, destPath, logFile, progressBar, avecChat);
		launchCompareButton.setOnAction(launchCompareButtonEventHandler);
	}



	public static void main(String[] args) {
		launch(args);


	};

}
