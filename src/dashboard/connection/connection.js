// Connection logic
function attemptConnection(){
    var connected = NetworkTables.isRobotConnected();
    ipc.send('attempt-connect');
    console.log(connected);
}

$(document).ready(function() {
    $("#connection-container").load("connection/connection.html", () => {
        setInterval(attemptConnection, 10000)
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
