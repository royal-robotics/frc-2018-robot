// Finding roborio logic
const exec = require('child_process').exec;

function execPromise(command) {
    exec(command, (error, stdout, stderr) => {
        if (error !== null) {
            return undefined;
        } else {
            return stdout;
        }
    });
}

exports.getIPAsync = () => {
    let discoverRoborio = execPromise("cd.. & cd.. & gradlew discoverRoborio");

    if (discoverRoborio !== undefined) {
        // If Roborio is found
        let ipList = discoverRoborio.match(/\d{1,3}\.\d{1,3}.\d{1,3}.\d{1,3}/g)
        if(ipList == null || ipList.length != 1) {
            return undefined;
        }
        return ipList[0];
    }

    // If Roborio is not found
    return undefined;
}
