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
    replaceImage("http://roborio-2522-frc.local:1181/stream.mjpg");
}

function disconnectFromCamera() {
    replaceImage("images/noCamera.png");
}

function replaceImage(src) {
    $("#camera").empty();
    let img = document.createElement("img");
    img.src = src;
    img.style["width"] = "467px";
    img.style["height"] = "350px";
    $("#camera").append(img);
}
