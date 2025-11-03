package essai;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class FileWithSha {

	@Override
	public int hashCode() {
		return Objects.hash(relativePath, sha);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileWithSha other = (FileWithSha) obj;
		return Objects.equals(relativePath, other.relativePath) && Objects.equals(sha, other.sha);
	}

	private Path relativePath;
	private Path absolutePath;



	private final String sha;
	public String getSha() {
		return sha;
	}

	private static String algo= "SHA-256";
	private Path parentPath;


	FileWithSha(Path path, Path parentPath, boolean avecChat) {
		this.parentPath = parentPath;
		this.relativePath = parentPath.relativize(path);
		this.absolutePath = parentPath.resolve(path);
		String tempSha = null;
		try {
			if (avecChat) {
				tempSha = FileSha.hashFile(path, algo);
				
			} else {
				tempSha = "";
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sha = tempSha;
		if (relativePath.normalize().equals(Path.of("2014 Certificat medical sport.pdf").normalize())) {
			System.out.println("sha = " + sha);
		}

	}

	public Path getAbsolutePath() {
		return absolutePath;
	}

	public Path getRelativePath() {
		return relativePath;
	}


	public Path getParentPath() {
		return parentPath;
	}

}
