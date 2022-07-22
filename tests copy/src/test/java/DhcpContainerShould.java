import helpers.DhcpdConfig;
import helpers.DockerImageTagResolver;
import helpers.HelperImages;
import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.containers.wait.strategy.WaitEx;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DhcpContainerShould {
    private static final Logger logger = LoggerFactory.getLogger(DhcpContainerRunningAsRootShould.class);

    private static GenericContainerEx _serverContainer;
    private static GenericContainerEx _clientContainer;


    @BeforeClass
    public static void setUp() throws IOException {
        Network network = Network.builder()
                .driver("bridge")
                .build();

        DhcpdConfig config = DhcpdConfig.createFromNetwork(network);

        _serverContainer = new GenericContainerEx<>(new DockerImageTagResolver())
                .withNetwork(network)
                .withNetworkAliases("dhcp-server.docker")
                //.withEnv("PUID", "0")
                //.withEnv("PGID", "0")
                .withFileSystemBind(config.getAbsolutePath(), "/config/dhcpd.conf")
                .waitingFor(WaitEx.forLogMessage("(.*)fallback-net(.*)", 1));

        _clientContainer = new GenericContainerEx<>(HelperImages.DhcpClient())
                .withCommand("sleep", "1000h")
                //.withEnv("PUID", "0")
                //.withEnv("PGID", "0")
                .withNetwork(network);

        _serverContainer.start();
        _serverContainer.followOutput(new Slf4jLogConsumer(logger));

        _clientContainer.start();
        _clientContainer.followOutput(new Slf4jLogConsumer(logger));
    }

    @AfterClass
    public static void cleanUp() {
        _serverContainer.close();
        _clientContainer.close();
    }

    @Test
    public void respondToDhcpDiscovery() throws IOException, InterruptedException {
        int retryCounter = 0;
        int retryLimit = 10;
        Container.ExecResult result = null;

        while(retryCounter < retryLimit) {
             System.out.println("Attempting DHCP discovery...");

             result = _clientContainer.executeShellCommand("nmap -p 67 --script broadcast-dhcp-discover");

             if(result.getStdout().contains("IP Offered:")){
                 break;
             }

             retryCounter++;

             Thread.sleep(2000);
        }

        assertTrue(result.getStdout().contains("IP Offered:"));
    }
}
