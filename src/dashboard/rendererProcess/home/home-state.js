let stateValues = {};

function addKeyListeners(keys) {
    console.log("Adding key listeners");
    keys.forEach(element => {
        NetworkTables.addKeyListener(element, (key, value, isNew) => {
            console.log(key + " value is " + value);
            stateValues[key] = value;
            if (typeof(redrawMain) !== undefined) {
                redrawMain();
            }
        }, true);
    });
}(["Controls/ClimberEnabled", "DriveController/Gear", "Intake/State", "Lift/Position"]);
