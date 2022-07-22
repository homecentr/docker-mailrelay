import helpers.MailRelayConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.LogUtils;

import helpers.DockerImageTagResolver;

import io.homecentr.testcontainers.containers.GenericContainerEx;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class MailRelayContainerShould {
    private static final Logger logger = LoggerFactory.getLogger(MailRelayContainerShould.class);

    private static GenericContainerEx _container;
    private static Path _configFilePath;

    @BeforeClass
    public static void setUp() throws IOException {
        _configFilePath = Files.createTempFile("mailrelay", "json");

        MailRelayConfig
                .forSendGrid("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")
                .writeToFile(_configFilePath);

        _container = new GenericContainerEx<>(new DockerImageTagResolver())
                .withFileSystemBind(_configFilePath.toAbsolutePath().toString(), "/config/mailrelay.json")
                .withExposedPorts(2525)
                .waitingFor(Wait.forListeningPort());

        _container.start();

        LogUtils.followOutput(DockerClientFactory.instance().client(), _container.getContainerId(), new Slf4jLogConsumer(logger));
    }

    @AfterClass
    public static void cleanUp() throws IOException {
        Files.deleteIfExists(_configFilePath);
        _container.close();
    }

    @Test
    public void sendMail() throws Exception {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "localhost");
        prop.put("mail.smtp.port", _container.getMappedPort(2525));
        prop.put("mail.smtp.auth", "false");
        prop.put("mail.smtp.starttls.enable", "false");

        Session session = Session.getInstance(prop);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("sender@homecentr.io"));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse("test@lholota.com")
        );
        message.setSubject("Testing MailRelay");
        message.setText("Hello, world!");

        Transport.send(message);
    }
}