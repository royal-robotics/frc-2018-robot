$(() => {
    ipc.on('connected', (ev, isConnected) => connectionChange(isConnected));
    connectionChange(NetworkTables.isRobotConnected());
    redrawMain();
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

function redrawMain() {
    console.log(keyNames);
    $("#state").empty();
    if (Object.keys(stateValues).length === 0) {
        $("#state").html("No state variables available");
        return;
    }

    let stateElements = document.createElement("ul");
    Object.keys(stateValues).forEach((key) => {
        let element = document.createElement("li");
        element.style["padding-bottom"] = "10px";
        element.innerHTML = keyNames[key] + ": " + stateValues[key];
        stateElements.appendChild(element);
    });

    $("#state").append(stateElements);
}