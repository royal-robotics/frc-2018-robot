const fs = require('fs');
var exec = require('child_process').exec
let Client = require('ssh2-sftp-client');

if (!fs.existsSync("./temp")){
    fs.mkdirSync("./temp");
}


var basePath = "./"

var motionProfilePath = basePath + "motionProfiles";

generateProfiles();
sendFiles();



function generateProfiles() {
    fs.readdir(motionProfilePath, async function(err, files) {
        var generateJarPath = basePath + "build/libs/generateMotionProfile.jar";

        for(var i = 0; i < files.length; i++) {
            var file = files[i];
            if(file.endsWith(".json")) {
                    var child = exec(`java -jar ${generateJarPath} ${motionProfilePath}/${file}`, function (error, stdout, stderr) {
                    if(error !== null)
                        console.log('exec error: ' + error);

                    console.log("Generated motion profiles: " + file);
                });
            }
        }
    });
}

function sendFiles() {
    // This assumes the motion profiles will be put in the temp diretory.
    // However, the motion profile .json schema lets the output go anywhere.
    fs.readdir("./temp", async function(err, files) {
        let sftp = new Client();

        try {
            await sftp.connect({
                host: '10.25.22.2',
                port: '22',
                username: 'lvuser',
                password: '',
                readyTimeout: 2000
            });

            for(var i = 0; i < files.length; i++) {
                var file = files[i];
                if(file.endsWith(".bin")) {
                    await sftp.put('temp/' + file, file);
                    console.log("Sent: " + file);
                }
            }

            console.log("\ndone!\n");
        } catch (e) {
            console.log("failed to connect to roborio!");
        }
        
        process.exit();
    });
}