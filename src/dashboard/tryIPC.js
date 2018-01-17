// Try to create ipc
let ipc, noElectron = false;
try {
    ipc = require('electron').ipcRenderer;
    console.log(ipc);
}
catch (e) {
    noElectron = true;
}
