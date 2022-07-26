const path = require("path");
const nodemailer = require("nodemailer");
const { DockerComposeEnvironment } = require("testcontainers");
const { updateConfig, getMandatoryVariable } = require("./utils");

describe("MailRelay container should", () => {
    var container;
    var environment;

    beforeAll(async () => {
        updateConfig();

        const composeFilePath = path.resolve(__dirname, "..");

        environment = await new DockerComposeEnvironment(composeFilePath, "docker-compose.yml")
            .withBuild()
            .up();

        container = environment.getContainer("image_1");
    });

    afterAll(async () => {
        await environment.down();
    });

    it("Listen on configured port", async () => {

        const transporter = nodemailer.createTransport({
            host: 'localhost',
            port: container.getMappedPort(2525)
        });

        await transporter.sendMail({
            from: getMandatoryVariable("MAIL_FROM"),
            to: getMandatoryVariable("MAIL_TO"),
            subject: 'Test Email Subject',
            html: 'Example HTML Message Body'
        });
    });
});