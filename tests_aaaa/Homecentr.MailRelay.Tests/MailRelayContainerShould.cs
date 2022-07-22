using Xunit;
using DotNet.Testcontainers.Containers;
using DotNet.Testcontainers.Builders;
using System.IO;
using DotNet.Testcontainers.Configurations;
using System.Threading.Tasks;
using System;
using Xunit.Abstractions;
using System.Net.Mail;

namespace Homecentr.MailRelay.Tests
{
    public class MailRelayContainerShould : IAsyncLifetime
    {
        private MemoryStream? _stdout;
        private MemoryStream? _stderr;
        private IOutputConsumer? _consumer;
        private TestcontainersContainer? _container;
        private readonly ITestOutputHelper _helper;

        public MailRelayContainerShould(ITestOutputHelper helper)
        {
            _helper = helper;
        }

        public async Task InitializeAsync()
        {
            var configFilePath = WriteConfigFile();

            _stdout = new MemoryStream();
            _stderr = new MemoryStream();

            _consumer = Consume.RedirectStdoutAndStderrToStream(_stdout, _stderr);

            _container = new TestcontainersBuilder<TestcontainersContainer>()
               .WithImage("test")//Environment.GetEnvironmentVariable("DOCKER_IMAGE_NAME"))
               .WithPortBinding("25431", "2525/tcp")
               .WithBindMount(configFilePath, "/config/mailrelay.json")
               .WithOutputConsumer(_consumer)
               .WithWaitStrategy(Wait.ForUnixContainer()) // .UntilMessageIsLogged(_stdout, "")
               .Build();

            await _container.StartAsync();
        }

        // Just use tcp connection to check if it responds (i.e. returns the first SMTP hello line)

        [Fact]
        public void SendEmail()
        {
            // Arrange
            var message = new MailMessage();
            message.Subject = "MailRelay test";
            message.Body = "Test mail";
            message.To.Add(new MailAddress("test@lholota.com")); //Environment.GetEnvironmentVariable("MAIL_TO")!));
            message.From = new MailAddress("docker.tests.mailrelay@homecentr.io");

            var smtp = new SmtpClient();

            Console.WriteLine("Waiting...");
            Console.ReadLine();

            smtp.Port = _container!.GetMappedPublicPort(2525);
            smtp.Host = "localhost";

            // Act
            smtp.SendMailAsync(message);
        }

        public async Task DisposeAsync()
        {
            await _stderr!.DisposeAsync();
            await _stdout!.DisposeAsync();

            _consumer!.Dispose();

            await _container!.DisposeAsync();
        }

        private static string WriteConfigFile()
        {
            var config = MailRelayConfig.ForSendGrid("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");
                //Environment.GetEnvironmentVariable("SendGridPassword")!);
            var configFilePath = Path.GetTempFileName();

            File.WriteAllText(configFilePath, config.Serialize());

            return configFilePath;
        }
    }
}