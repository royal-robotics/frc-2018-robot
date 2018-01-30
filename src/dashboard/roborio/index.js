// Finding roborio logic
const execSync = require('child_process').execSync;

function execCommand(command) {
    console.log(command);
    var result;
    try {
        result = execSync(command);
    } catch (e) {
        result = undefined;
    }
    return result;
}

exports.getIP = () => {
    let buffer = execCommand("cd.. & cd.. & gradlew discoverRoborio");
    var result = buffer.toString();
    console.log(result);

    if (result !== undefined) {
        // If Roborio is found
        let ipList = result.match(/\d{1,3}\.\d{1,3}.\d{1,3}.\d{1,3}/g)
        if(ipList === null || ipList.length !== 1) {
            console.log("Error getting ip");
            return undefined;
        }
        return ipList[0];
    }

    // If Roborio is not found
    console.log("Roborio not found");
    return result;
}
