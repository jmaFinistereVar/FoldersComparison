package essai;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.scene.control.ProgressBar;


public class FoldersComparator {

	private final Path source;
	private final Path dest;

	AtomicInteger internalCounter = new AtomicInteger(0);
	AtomicInteger progressBarCounter = new AtomicInteger(0);
	Future<List<FileWithSha>> fSource ;
	Future<List<FileWithSha>> fDest ;
	private boolean avecChat;


	FoldersComparator(Path source, Path dest, boolean avecChat) {
		this.source = source;
		this.dest = dest;
		this.avecChat = avecChat;
	}

	public ComparatorResults getResults() {
		try {
			return new ComparatorResults (fSource.get(), fDest.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}



	public int getProgress() {
		return progressBarCounter.get();
	}

	public boolean areTasksDone() {
		return fSource.isDone() && fDest.isDone();
	}

	public void dts (ExecutorService executorService) {

		long nbFiles = countFiles(source);


		fSource = executorService.submit(() -> getFiles(source, nbFiles, true));
		fDest = executorService.submit(() -> getFiles(dest, nbFiles, false));


		//sourceFiles = fSource.get();
		//destFiles = fDest.get();

	}

	private long countFiles(Path path) {
		long nbFiles = 0;
		try (Stream<Path> sourceStream = Files.walk(path)) {
			Predicate<Path> isDirectory = Files::isDirectory; 
			nbFiles = sourceStream.filter(isDirectory.negate())
					.count();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nbFiles;

	}

	private  List<FileWithSha> getFiles(Path path, long nbFiles, boolean count) {
		List<FileWithSha> filesInSource = null;
		try (Stream<Path> sourceStream = Files.walk(path)) {
			Predicate<Path> isDirectory = Files::isDirectory; 
			filesInSource = sourceStream.filter(isDirectory.negate())
					.map(p -> new FileWithSha(p, path, avecChat))
					.peek(p-> {
						if (count) {
							int i = internalCounter.incrementAndGet();
							int step = (int) (nbFiles / 5);
							if (i%step == 1) {
								progressBarCounter.set((i*20/step));
							}
						}
					})
					.collect(Collectors.toList());


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filesInSource.stream().sorted((f1, f2) -> f1.getRelativePath().compareTo(f2.getRelativePath())).toList();

	}





}
