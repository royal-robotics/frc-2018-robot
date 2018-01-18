// Finding roborio logic
const exec = require('child_process').exec;

function execPromise(command) {
    return new Promise(function (resolve, reject) {
        let child = exec(command, (error, stdout, stderr) => {
            if (error !== null) {
                console.log(error);
                reject(error);
            } else {
                console.log(stdout);
                resolve(stdout);
            }
        });
    });
}

exports.getIPAsync = () => {
    let discoverRoborio = execPromise("cd.. & cd.. & gradlew discoverRoborio").then(function(out) {
        // If Roborio is found
        console.log(discoverRoborio);
        let ipList = discoverRoborio.match(/\d{1,3}\.\d{1,3}.\d{1,3}.\d{1,3}/g)
        if(ipList.length != 1) {
            return undefined;
        }
        return ipList[0];
    }, function(err) {
        // If Roborio is not found
        return undefined;
    });
}
