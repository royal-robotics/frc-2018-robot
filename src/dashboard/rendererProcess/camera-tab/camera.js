$(() => {
    var baseUrl = "http://roborio-2522-frc.local:1181";
    var streamUrl = `${baseUrl}/stream.mjpg`;
    var settingsUrl = `${baseUrl}/settings.json`;

    //$("#camera").css('background-image', `url("${streamUrl}")`);
    console.log(`url(${streamUrl})`);
    // $("#camera").css('width', '640px');
    // $("#camera").css('height', '480px');

    if (NetworkTables.isRobotConnected()) {
        connectToCamera();
    }
});

ipc.on("connected", (ev, con) => {
    if (con) {
        connectToCamera();
    } else {
        disconnectFromCamera();
    }
});

function connectToCamera() {
    console.log("Connecting to camera...");
    replaceImage("http://roborio-2522-frc.local:1181/stream.mjpg");
}

function disconnectFromCamera() {
    console.log("Disconnecting from camera...");
    replaceImage("images/noCamera.png");
}

function replaceImage(src) {
    $("#camera").empty();
    let stream = document.createElement("img");
    stream.src = src;
    $("#camera").append(stream);
}
