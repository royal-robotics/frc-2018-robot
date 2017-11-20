// Sets function to be called on NetworkTables connect (usually not necessary).
// NetworkTables.addWsConnectionListener(onNetworkTablesConnection, true);

// Sets function to be called when robot dis/connects
NetworkTables.addRobotConnectionListener((connected) => {
    if(connected)
        console.log("Connected!");
}, false);


//NetworkTables.addGlobalListener(onValueChanged, true);//Sets function to be called when any NetworkTables key/value changes
//NetworkTables.putValue('/SmartDashboard/testput', "put value");
//NetworkTables.getValue('/SmartDashboard/testget', "default value");

NetworkTables.addKeyListener('/SmartDashboard/test/getRobotValue', (key, value) => {
    document.getElementById("test").innerHTML = value;
});

ipc.on("ip-found", (ev, mesg) => {
    console.log("ip-found: " + mesg)
    ipc.send('connect', mesg);
});