package helpers;

import org.apache.commons.net.util.SubnetUtils;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.Network;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DhcpdConfig {
    private final File _tempFile;

    public static DhcpdConfig createFromNetwork(Network network) throws IOException {
        File tempFile = File.createTempFile("dhcpd", "conf");
        SubnetUtils subnet = getSubnet(network);

        String configuration = "authoritative;\n" +
                "subnet "+ subnet.getInfo().getAddress() +" netmask "+ subnet.getInfo().getNetmask() +" {\n" +
                "    range "+ subnet.getInfo().getLowAddress() +" "+ subnet.getInfo().getHighAddress() +";\n" +
                "    option routers "+ subnet.getInfo().getLowAddress() +";\n" +
                "}";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            bw.write(configuration);
        }

        return new DhcpdConfig(tempFile);
    }

    private DhcpdConfig(File tempFile) {
        _tempFile = tempFile;
    }

    public String getAbsolutePath() {
        return _tempFile.getAbsolutePath();
    }

    public void close() {
        _tempFile.delete();
    }

    private static SubnetUtils getSubnet(Network network) {
        com.github.dockerjava.api.model.Network inspectResult = DockerClientFactory.instance().client()
                .inspectNetworkCmd()
                .withNetworkId(network.getId())
                .exec();

        String subnetCidr = inspectResult.getIpam().getConfig().get(0).getSubnet();

        return new SubnetUtils(subnetCidr);
    }
}
