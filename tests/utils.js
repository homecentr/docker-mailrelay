const fs = require('fs')
const path = require("path");

export const updateConfig = async () => {
    if(!process.env.hasOwnProperty("SENGRID_API_KEY")) {
        return;
    }

    const configFilePath = path.resolve(__dirname, "..", "example", "mailrelay.json");

    const content = fs.readFileSync(configFilePath).toString("utf-8");
    const replaced = content.replace(
        "$SENDGRID_API_KEY$",
        getMandatoryVariable("SENDGRID_API_KEY"));

    if (!fs.existsSync(configDirPath)) {
        fs.mkdirSync(configDirPath);
    }

    fs.writeFileSync(configFilePath, replaced);
}

export const getMandatoryVariable = (name) => {
    if (!process.env.hasOwnProperty(name) || process.env[name] === "") {
        throw new Error(`The variable ${name} has not been set.`)
    }

    return process.env[name];
}