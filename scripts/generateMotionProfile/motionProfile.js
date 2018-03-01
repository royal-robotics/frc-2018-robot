const fs = require('fs');
var exec = require('child_process').exec
let Client = require('ssh2-sftp-client');

var motionProfile = {
    "wheelbase-width": 31.25,
    "waypoints" : [
        {"x": -3.1, "y": -1.0, "angle": -45.0},
        {"x": -2.0, "y": -2.0, "angle": 0.0},
        {"x": 0.0, "y": 0.0, "angle": 0.0}
    ],
    "config": {
        "fit-method": "HERMITE_CUBIC",
        "numSamples": 10000,
        "dt": 0.01,
        "max-velocity": 150.0,
        "max-acceleration": 150.0,
        "max-jerk": 600.0
    },
    "output-center-csv" : "temp/motion-profile-center.csv",
    "output-center-bin" : "temp/motion-profile-center.bin",
    "output-left-csv" : null,
    "output-left-bin" : null,
    "output-right-csv" : null,
    "output-right-bin" : null
}

if (!fs.existsSync("./temp")){
    fs.mkdirSync("./temp");
}

fs.writeFile("./temp/motion-profile.json", JSON.stringify(motionProfile), 'utf8', function (err) {
    if (err)
        return console.log(err);

    console.log("Created motion-profile.json.");

    var child = exec('java -jar motionProfile.jar temp/motion-profile.json', function (error, stdout, stderr) {
        if(error !== null)
            return console.log('exec error: ' + error);

        console.log("Generated motion profile.");
        sendFiles();
    });
});

function sendFiles() {
    fs.readdir("./temp", async function(err, files) {
        let sftp = new Client();
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
        process.exit();
    });
}