package essai;

import java.nio.file.Path;
import java.util.List;

public class ComparatorResults {



	private List<FileWithSha> sourceFiles;
	private List<FileWithSha> destFiles;
	private List<FileWithSha[]> commonFiles;

	private List<String> sourceFilesRelativePath;
	private List<String> destFilesRelativePath;
	private final Path sourceParent;
	private final Path destParent;


	ComparatorResults(List<FileWithSha> sourceFiles, List<FileWithSha> destFiles) {
		this.sourceFiles = sourceFiles;
		this.destFiles = destFiles;



		sourceFilesRelativePath = sourceFiles.stream()
				.map(FileWithSha::getRelativePath)
				.map(Path::toString).toList();

		destFilesRelativePath = destFiles.stream()
				.map(FileWithSha::getRelativePath)
				.map(Path::toString).toList();

		sourceParent = sourceFiles.get(0).getParentPath();
		destParent = destFiles.get(0).getParentPath();

		commonFiles = sourceFiles.stream()
				.filter(f -> destFilesRelativePath.contains(f.getRelativePath().toString()))
				.map(f -> {
					for (FileWithSha d: destFiles) {
						if (d.getRelativePath().equals(f.getRelativePath())) {
							return new FileWithSha[] {f, d};
						}

					}
					return null;
				}).toList();




	}

	List<String> getAbsolutePathsInSourceWhichAreNotInDest() {
		List<String> l = sourceFilesRelativePath.stream().filter(p -> !destFilesRelativePath.contains(p))
				.map(sourceParent::resolve)
				.map(Path::toAbsolutePath)
				.map(Path::toString).toList();
		if (commonFiles.size() + l.size() != sourceFilesRelativePath.size()) {
			throw new IllegalStateException();
		}
		return l;

	}

	List<String> getAbsolutePathsInDestWhichAreNotInSource() {
		List<String> l = destFilesRelativePath.stream().filter(p -> !sourceFilesRelativePath.contains(p))
				.map(sourceParent::resolve)
				.map(Path::toAbsolutePath)
				.map(Path::toString).toList();
		if (commonFiles.size() + l.size() != destFilesRelativePath.size()) {
			throw new IllegalStateException();
		}
		return l;
	}

	List<String> getFilesWhichAreDifferent() {
		return commonFiles.stream()
				.filter(p -> 
				 {
					 /*if (p[0].getSha().e p[1].getSha()) {
						 System.out.println("File " + p[0].getRelativePath());
						 System.out.println("\t " + p[0].getSha());
						 System.out.println("\t " + p[1].getSha());

					 }*/
					 return !p[0].getSha().equals(p[1].getSha());})
				.map(p -> p[0].getRelativePath())
				.map(Path::toString).toList();
	}

	public Path getSourceParent() {
		return sourceParent;
	}

	public Path getDestParent() {
		return destParent;
	}



}
