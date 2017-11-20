const exec = require('child_process').exec;

function execPromise(command) {
    return new Promise(function (resolve, reject) {
        let child = exec(command, (error, stdout, stderr) => {
            if (error !== null)
                reject(error);
            else
                resolve(stdout);
        });
    });
}

exports.getIPAsync = async () => {
    let discoverRoborio = await execPromise("cd.. & cd.. & gradlew discoverRoborio");
    let ipList = discoverRoborio.match(/\d{1,3}\.\d{1,3}.\d{1,3}.\d{1,3}/g)
    if(ipList.length != 1)
        throw {message: "Failed to parse discoverRoborio task result for an IP address"};
    
    return ipList[0];
}