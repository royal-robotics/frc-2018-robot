const fs = require('fs');
var exec = require('child_process').exec
let Client = require('ssh2-sftp-client');

var basePath = "./"
var motionProfilePath = basePath + "motionProfiles";

fs.readdir(motionProfilePath, async function(err, files) {
    var generateJarPath = basePath + "build/libs/generateMotionProfile.jar";

    for(var i = 0; i < files.length; i++) {
        var file = files[i];
        if(file.endsWith(".json")) {
            var child = exec(`java -jar ${generateJarPath} ${motionProfilePath}/${file}`);
            await promiseFromChildProcess(child);
            console.log("Generated: " + file);
        }
    }

    console.log();

    let sftp = new Client();
    try {
        await sftp.connect({
// 172.22.11.2 for at compitentions?
// 10.25.22.1 for wireless
            host: '10.25.22.2',
            port: '22',
            username: 'lvuser',
            password: '',
            readyTimeout: 2000
        });
    }
    catch (e) {
        console.log("failed to connect to roborio!");
        process.exit();
    }

    fs.readdir(motionProfilePath + "/generated", async function(err, generatedFiles) {
        for(var i = 0; i < generatedFiles.length; i++) {
            var file = generatedFiles[i];
            if(file.endsWith(".bin")) {
                await sftp.put(motionProfilePath + "/generated/" + file, file);
                console.log("Sent: " + file);
            }
        }
        
        process.exit();
    });
});


function promiseFromChildProcess(child) {
    return new Promise(function (resolve, reject) {
        child.addListener("error", reject);
        child.addListener("exit", resolve);
    });
}