package helpers;

import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Path;
import java.nio.file.Paths;

public class HelperImages {
    public static ImageFromDockerfile DhcpClient() {
        Path dockerFileDir = Paths.get(System.getProperty("user.dir"), "images", "client", "Dockerfile");

        return new ImageFromDockerfile()
                .withFileFromPath("Dockerfile", dockerFileDir);
    }
}
