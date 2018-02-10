
$(() => {
    var baseUrl = "http://roborio-2522-frc.local:1181";
    var streamUrl = `${baseUrl}/stream.mjpg`
    var settingsUrl = `${baseUrl}/settings.json`

    //$("#camera").css('background-image', `url("${streamUrl}")`);
    console.log(`url(${streamUrl})`);
    // $("#camera").css('width', '640px');
    // $("#camera").css('height', '480px');
});
