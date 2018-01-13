

// Sets function to be called when robot dis/connects
if(!noElectron) {
    //Configure jquery so the load function is async
    $.ajaxPrefilter(function( options, original_Options, jqXHR ) {
        options.async = true;
    });


    // NetworkTables.addRobotConnectionListener((connected) => {
    //     if(connected)
    //         console.log("Connected!");
    //     else
    //         console.log("Disconnected");
    // }, true);

    // NetworkTables.addKeyListener('/SmartDashboard/test/getRobotValue', (key, value) => {
    //     document.getElementById("test").innerHTML = value;
    // });
    
    ipc.on("ip-found", (ev, mesg) => {
        console.log("ip-found: " + mesg)
        ipc.send('connect', mesg);
    });
}



//NetworkTables.addGlobalListener(onValueChanged, true);//Sets function to be called when any NetworkTables key/value changes
//NetworkTables.putValue('/SmartDashboard/testput', "put value");
//NetworkTables.getValue('/SmartDashboard/testget', "default value");

