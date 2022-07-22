package helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MailRelayConfig {
    private String content;

    public static MailRelayConfig forSendGrid(String apiKey){
        String template = "{\n" +
                "            \"smtp_server\":   \"smtp.sendgrid.net\",\n" +
                "            \"smtp_port\":     587,\n" +
                "            \"smtp_starttls\": true,\n" +
                "            \"smtp_username\": \"apikey\",\n" +
                "            \"smtp_password\": \"$PASS$\",\n" +
                "            \"smtp_max_email_size\": 10485760,\n" +
                "            \"smtp_login_auth_type\": false,\n" +
                "            \"local_listen_ip\": \"0.0.0.0\",\n" +
                "            \"local_listen_port\": 2525,\n" +
                "            \"allowed_hosts\": [\"*\"],\n" +
                "            \"timeout_secs\": 30\n" +
                "        }";

        String content = template.replace("$PASS$", apiKey);

        return new MailRelayConfig(content);
    }

    private MailRelayConfig(String content){
        this.content = content;
    }

    public void writeToFile(Path path) throws IOException {
        Files.writeString(path, this.content);
    }
}
