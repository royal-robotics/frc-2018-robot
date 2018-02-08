// Finding roborio logic
const exec = require('child_process').exec;

exports.getIP = () => {
    return new Promise(function(resolve, reject) {
        exec("cd.. & cd.. & gradlew discoverRoborio", function(error, stdout, stderr) {
            if (error != null) {
                reject(error.message);
            } else {
                let ipList = stdout.match(/\d{1,3}\.\d{1,3}.\d{1,3}.\d{1,3}/g)
                if(ipList === null || ipList.length !== 1) {
                    console.log("Error getting ip");
                    return undefined;
                }
                resolve(ipList[0]);
            }
        });
    });
}
