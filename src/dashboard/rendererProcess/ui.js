// Sets function to be called when robot dis/connects
if(!noElectron) {
    //Configure jquery so the load function is async
    $.ajaxPrefilter(function( options, original_Options, jqXHR ) {
        options.async = true;
    });
}
