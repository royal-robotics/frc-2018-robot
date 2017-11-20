let ipc, noElectron = false;
try {
    ipc = require('electron').ipcRenderer;
    console.log(ipc.test);
}
catch (e) {
    noElectron = true;
}
