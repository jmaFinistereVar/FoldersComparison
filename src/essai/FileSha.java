package essai;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileSha {
	
	public static String hashFile(Path path, String algo)
            throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algo);

        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(path.toFile()), md)) {
            byte[] buffer = new byte[8192]; // 8 Ko
            while (dis.read(buffer) != -1) {
                // lecture continue, DigestInputStream met à jour le hash automatiquement
            }
        }

        byte[] hashBytes = md.digest();
        return bytesToHex(hashBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
