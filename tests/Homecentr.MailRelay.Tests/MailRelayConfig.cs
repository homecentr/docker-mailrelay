using System.Text.Json;
using System.Text.Json.Serialization;

namespace Homecentr.MailRelay.Tests
{
    internal class MailRelayConfig
    {
        public static MailRelayConfig ForSendGrid(string smtpPassword)
        {
            var result = new MailRelayConfig();

            result.SmtpServer = "smtp.sendgrid.net";
            result.SmtpPort = 587;
            result.SmtpUsername = "apikey";
            result.SmtpPassword = smtpPassword;
            result.SmtpStartTls = true;

            return result;
        }

        public MailRelayConfig()
        {
            ListenIp = "0.0.0.0";
            ListenPort = 2525;
            AllowedHosts = new[] { "*" };
            Timeout = 30;
            SmtpMaxEmailSize = 10485760;
        }

        /*       
        {
            "smtp_server":   "smtp.fastmail.com",
            "smtp_port":     465,
            "smtp_starttls": false,
            "smtp_username": "username@fastmail.com",
            "smtp_password": "secretAppPassword",
            "smtp_max_email_size": 10485760,
            "smtp_login_auth_type": false,
            "local_listen_ip": "0.0.0.0",
            "local_listen_port": 2525,
            "allowed_hosts": ["*"],
            "timeout_secs": 30
        }
         */

        [JsonPropertyName("smtp_server")]
        public string? SmtpServer { get; set; }

        [JsonPropertyName("smtp_port")]
        public int SmtpPort { get; set; }

        [JsonPropertyName("smtp_starttls")]
        public bool SmtpStartTls { get; set; }

        [JsonPropertyName("smtp_username")]
        public string? SmtpUsername { get; set; }

        [JsonPropertyName("smtp_password")]
        public string? SmtpPassword { get; set; }

        [JsonPropertyName("smtp_max_email_size")]
        public int SmtpMaxEmailSize { get; set; }

        [JsonPropertyName("smtp_login_auth_type")]
        public bool SmtpLoginAuthType { get; set; }

        [JsonPropertyName("local_listen_ip")]
        public string ListenIp { get; set; }

        [JsonPropertyName("local_listen_port")]
        public int ListenPort { get; set; }

        [JsonPropertyName("allowed_hosts")]
        public string[] AllowedHosts { get; set; }

        [JsonPropertyName("timeout")]
        public int Timeout { get; set; }

        public string Serialize()
        {
            return JsonSerializer.Serialize(this);
        }
    }
}