const path = require("path");
const nodemailer = require("nodemailer");
const { DockerComposeEnvironment } = require("testcontainers");

describe("MailRelay container should", () => {
    var mailRelayContainer;
    var composeEnvironment;

    beforeAll(async () => {
        const composeFilePath = path.resolve(__dirname, "..");

        composeEnvironment = await new DockerComposeEnvironment(composeFilePath, "docker-compose.yml")
            .withBuild()
            .up();

        mailRelayContainer = composeEnvironment.getContainer("image_1");
    });

    afterAll(async () => {
        await composeEnvironment.down();
    });

    it("Listen on configured port", async () => {
        // Arrange
        const transporter = nodemailer.createTransport({
            host: 'localhost',
            port: mailRelayContainer.getMappedPort(2525)
        });

        // Act
        await transporter.sendMail({
            from: "sender@test.com",
            to: "recipient@example.com",
            subject: 'Test Email Subject',
            html: 'Example HTML Message Body'
        });
    });
});