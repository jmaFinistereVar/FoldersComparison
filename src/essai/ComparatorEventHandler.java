package essai;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ProgressBar;

public class ComparatorEventHandler implements EventHandler<ActionEvent> {
	
	private Path sourcePath;
	private Path destPath;
	private Path logFile;

	private ProgressBar progressBar;
	ComparatorResults cr;
	private boolean avecChat;

	ComparatorEventHandler(Path sourcePath, Path destPath, Path logFile, ProgressBar progressBar
			, boolean avecChat) {
		this.sourcePath = sourcePath;
		this.destPath = destPath;
		this.progressBar = progressBar;
		this.avecChat = avecChat;
		this.logFile = logFile;
		
	}
	
	@Override
	public void handle(ActionEvent event) {
		FoldersComparator fc = new FoldersComparator(sourcePath, destPath,avecChat);

		Task<Void> task = new Task<>() {
			@Override
			protected Void call() throws Exception {
				try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();){
					fc.dts(executorService);
					while (!fc.areTasksDone()) {
						Thread.sleep(2000); // Simule un traitement
						updateProgress(fc.getProgress(), 100);
						updateMessage("Progression : " + fc.getProgress() + "%");
					}

				}


				return null;
			}
		};



		// 🔹 Lier la barre et le label à la tâche
		progressBar.progressProperty().bind(task.progressProperty());
		//status.textProperty().bind(task.messageProperty());

		// éventuellement : réactiver un bouton à la fin
	    task.setOnSucceeded(e -> {
	    	cr = fc.getResults();
			
			try (PrintWriter writer = new PrintWriter(logFile.toAbsolutePath().toString())) {
				writer.println("SOURCE FOLDER");
				writer.println(cr.getSourceParent().toAbsolutePath());
				writer.println("DEST FOLDER");
				writer.println(cr.getDestParent().toAbsolutePath());
				
				writer.println("FICHIERS CONTENUS DANS SOURCE NON CONTENUS DANS DEST");
				cr.getAbsolutePathsInSourceWhichAreNotInDest().forEach(writer::println);
				writer.println("\n");

				writer.println("FICHIERS CONTENUS DANS DEST NON CONTENUS DANS SOURCE");
				cr.getAbsolutePathsInDestWhichAreNotInSource().forEach(writer::println);
				writer.println("\n");

				writer.println("FICHIERS DIFFERENTS DANS LES DEUX DOSSIERS");
				cr.getFilesWhichAreDifferent().forEach(writer::println);
				
				
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
	    });
	    task.setOnFailed(e -> {
	        task.getException().printStackTrace();
	    });

	    // 🔹 lancer le task en arrière-plan (sans join)
	    Thread t = new Thread(task);
	    t.setDaemon(true);
	    t.start();
	    
		
		
		

	}

}
