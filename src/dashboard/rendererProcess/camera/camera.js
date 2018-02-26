var baseUrl = "http://roborio-2522-frc.local:1181";
var streamUrl = `${baseUrl}/stream.mjpg`;
var settingsUrl = `${baseUrl}/settings.json`;


$(() => {
    ipc.on('connected', (ev, isConnected) => connectionChange(isConnected));
    connectionChange(NetworkTables.isRobotConnected());
});


function connectionChange(isConnected) {
    if(isConnected)
        connectToCamera();
    else
        disconnectFromCamera();
}

function connectToCamera() {
    console.log("Connecting to camera...");
    replaceImage("http://roborio-2522-frc.local:1181/stream.mjpg");
}

function disconnectFromCamera() {
    console.log("Disconnecting from camera...");
    replaceImage("images/noCamera.png");
}

function replaceImage(src) {
    console.log($("#camera"))
    $("#camera").empty();
    let img = document.createElement("img");
    img.src = src;
    $("#camera").append(img);
}
