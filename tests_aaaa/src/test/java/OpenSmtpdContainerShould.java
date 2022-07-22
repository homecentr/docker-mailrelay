import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.LogUtils;

import helpers.DockerImageTagResolver;

import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.containers.wait.strategy.WaitEx;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.file.Paths;
import java.util.Properties;

public class OpenSmtpdContainerShould {
    private static final Logger logger = LoggerFactory.getLogger(OpenSmtpdContainerShould.class);

    private static GenericContainerEx _container;

    @BeforeClass
    public static void setUp() {
        _container = new GenericContainerEx<>(new DockerImageTagResolver())
                .withRelativeFileSystemBind(Paths.get( "src", "test", "resources", "smtpd.conf"), "/config/smtpd.conf")
                .withRelativeFileSystemBind(Paths.get( "src", "test", "resources", "users"), "/config/users")
                .withExposedPorts(25)
                .waitingFor(WaitEx.forS6OverlayStart());

        _container.start();

        LogUtils.followOutput(DockerClientFactory.instance().client(), _container.getContainerId(), new Slf4jLogConsumer(logger));
    }

    @AfterClass
    public static void cleanUp() {
        _container.close();
    }

    @Test
    public void sendMail() throws Exception {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "localhost");
        prop.put("mail.smtp.port", _container.getMappedPort(25));
        prop.put("mail.smtp.auth", "false");
        prop.put("mail.smtp.starttls.enable", "false");

        Session session = Session.getInstance(prop);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("sender@domain.com"));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse("nobody@domain.com")
        );
        message.setSubject("Testing OpenSMTPd");
        message.setText("Hello, world!");

        Transport.send(message);
    }
}