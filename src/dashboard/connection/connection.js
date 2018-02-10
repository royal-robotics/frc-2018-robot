// Connection logic
function checkConnection() {
    if(!NetworkTables.isRobotConnected())
        ipc.send('attempt-connect');
}

$(() => {
    $("#connection-container").load("connection/connection.html", () => {
        checkConnection();
        setInterval(checkConnection, 1000)
        NetworkTables.addRobotConnectionListener(connectionStatus, /*Call Immediately*/ true);
    });
});

function connectionStatus(connected) {
    createAlert(connected);
}

function createAlert(connected) {
    var alert = document.createElement("div");
    alert.classList.add("alert");
    alert.classList.add("alert-dismissible");
    alert.classList.add(connected ? "alert-success" : "alert-warning");

    var closeButton = document.createElement("button");
    closeButton.classList.add("close");
    closeButton.innerHTML = "&times;"
    
    const connectedMessage = "Connected to roboRIO!";
    const disconnectedMessage = "Failed to connect to roboRIO!";
    var messageTitle = document.createElement("strong");
    messageTitle.appendChild(document.createTextNode("Connection Status: "));
    alert.appendChild(messageTitle);
    alert.appendChild(document.createTextNode(connected ? connectedMessage : disconnectedMessage));
    alert.appendChild(closeButton);
    
    $("#alert-container").html(alert);
}

function dismissAlert() {

}