import helpers.DhcpdConfig;
import helpers.DockerImageTagResolver;
import io.homecentr.testcontainers.containers.GenericContainerEx;
import io.homecentr.testcontainers.containers.wait.strategy.WaitEx;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class DhcpContainerRunningAsRootShould {
    private static final Logger logger = LoggerFactory.getLogger(DhcpContainerRunningAsRootShould.class);

    private static GenericContainerEx _container;

    @BeforeClass
    public static void setUp() throws IOException {
        Network network = Network.builder().build();
        DhcpdConfig config = DhcpdConfig.createFromNetwork(network);

        _container = new GenericContainerEx<>(new DockerImageTagResolver())
                .withNetwork(network)
                .withEnv("PUID", "0")
                .withEnv("PGID", "0")
                .withFileSystemBind(config.getAbsolutePath(), "/config/dhcpd.conf")
                .waitingFor(WaitEx.forLogMessage("(.*)Socket/fallback/fallback-net(.*)", 1));

        _container.start();
        _container.followOutput(new Slf4jLogConsumer(logger));
    }

    @AfterClass
    public static void cleanUp() {
        _container.close();
    }

    @Test
    public void startDhcpdAsRootUid() throws Exception {
        int uid = _container.getProcessUid("/usr/sbin/dhcpd -4 -f -cf /config/dhcpd.conf -lf /leases/dhcpd.leases -user root -group root");

        assertEquals(0, uid);
    }

    @Test
    public void startDhcpdAsRootGid() throws Exception {
        int gid = _container.getProcessGid("/usr/sbin/dhcpd -4 -f -cf /config/dhcpd.conf -lf /leases/dhcpd.leases -user root -group root");

        assertEquals(0, gid);
    }
}