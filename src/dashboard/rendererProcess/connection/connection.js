// Connection logic
function checkConnection() {
    if(!NetworkTables.isRobotConnected()) {
        createAlert(false, true);
        ipc.send('attempt-connect');
    }
}

$(() => {
    $("#connection-container").load("rendererProcess/connection/connection.html", () => {
        // Don't call immediately because that causes a double connection check that can lock the connection code
        NetworkTables.addRobotConnectionListener(connectionStatus, false);
    });
});

function connectionStatus(connected) {
    createAlert(connected, false);
    if (!connected) {
        setTimeout(checkConnection, 3000);
    }
}

function createAlert(connected, attempting) {
    var alert = document.createElement("div");
    alert.classList.add("alert");
    alert.classList.add("alert-dismissible");
    alert.classList.add(connected ? "alert-success" : attempting ? "alert-info" : "alert-warning");

    var closeButton = document.createElement("button");
    closeButton.classList.add("close");
    closeButton.innerHTML = "&times;"
    
    const connectedMessage = "Connected to roboRIO!";
    const attemptingMessage = "Attempting to connect to roboRIO..."
    const disconnectedMessage = "Failed to connect to roboRIO!";
    var messageTitle = document.createElement("strong");
    messageTitle.appendChild(document.createTextNode("Connection Status: "));
    alert.appendChild(messageTitle);
    alert.appendChild(document.createTextNode(connected ? connectedMessage : attempting ? attemptingMessage : disconnectedMessage));
    alert.appendChild(closeButton);
    
    $("#alert-container").html(alert);
}

function dismissAlert() {

}
