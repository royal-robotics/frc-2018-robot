let ipc, noElectron = false;
try {
    ipc = require('electron').ipcRenderer;
    console.log(ipc);
}
catch (e) {
    noElectron = true;
}

$(document).ready(function() {
    console.log("hello wolrd!");
});