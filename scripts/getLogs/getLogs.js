const fs = require('fs');
var exec = require('child_process').exec
let Client = require('ssh2-sftp-client');
var toString = require('stream-to-string');

if (!fs.existsSync("./temp")){
    fs.mkdirSync("./temp");
}

let sftp = new Client();
sftp.connect({
    host: '10.25.22.2',
    port: '22',
    username: 'lvuser',
    password: '',
    readyTimeout: 2000
}).then(async() => {
    var files = await sftp.list('/home/lvuser/')
    for(var i = 0; i < files.length; i++) {
        var file = files[i];

        if(file.name.endsWith(".txt")) {
            var name = file.name;
            var data = await sftp.get(name);
            var dataString = await toString(data);
            fs.writeFileSync("./temp/" + name, dataString)
        }
    }
    process.exit();
}).catch((err) => {
    console.log("failed to connect to roborio!");
    process.exit();
});