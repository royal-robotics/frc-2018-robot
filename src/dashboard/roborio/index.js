// Finding roborio logic
const exec = require('child_process').exec;

var results = 0;
function execPromise(command, results) {
    console.log(command);
    var process; 
    process = exec(command,{},(error, stdout, stderr) => {
        if (error !== null) {
            results = undefined;
        } else {
            console.log(error);
            results = stdout;
        }
    });
    return process;
}

exports.getIPAsync = (results) => {
    let process = execPromise("cd.. & cd.. & gradlew discoverRoborio", results);
    return process;
}
