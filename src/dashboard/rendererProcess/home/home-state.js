let stateValues = {};
let keyNames = {"/SmartDashboard/Controls/ClimberEnabled" : "Climb Enabled", "/SmartDashboard/DriveController/Gear" : "Gear", "/SmartDashboard/Intake/State" : "Intake State", "/SmartDashboard/Lift/Position" : "Lift Position"}; 

(function addKeyListeners(keys) {
    console.log("Adding key listeners");
    keys.forEach(element => {
        NetworkTables.addKeyListener(element, (key, value, isNew) => {
            if (key == "/SmartDashboard/Lift/Position") {
                value = value.toPrecision(3);
            }
            stateValues[key] = value;
            if (typeof(redrawMain) !== undefined) {
                redrawMain();
            }
        }, true);
    });
})(["/SmartDashboard/Controls/ClimberEnabled", "/SmartDashboard/DriveController/Gear", "/SmartDashboard/Intake/State", "/SmartDashboard/Lift/Position"]);
