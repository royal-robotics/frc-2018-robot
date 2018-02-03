// Sets function to be called when robot dis/connects
if(!noElectron) {
    //Configure jquery so the load function is async
    $.ajaxPrefilter(function( options, original_Options, jqXHR ) {
        options.async = true;
    });
    
    // ipc.on("ip-found", (ev, mesg) => {
    //     console.log("ip-found");
    //     ipc.send('connect', mesg);
    // });

    ipc.on('connected', (ev, mesg) => {
        console.log("Connected!?!  " + mesg);
    });
}
