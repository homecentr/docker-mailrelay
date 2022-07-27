export const getMandatoryVariable = (name) => {
    if (!process.env.hasOwnProperty(name) || process.env[name] === "") {
        throw new Error(`The variable ${name} has not been set.`)
    }

    return process.env[name];
}