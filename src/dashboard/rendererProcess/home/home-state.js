let stateValues = {};

function addKeyListeners(keys) {
    keys.forEach(element => {
        NetworkTables.addKeyListener(element, (key, value, isNew) => {
            console.log(key + " value is " + value);
            stateValues[key] = value;
        }, true);
    });
}(["Climber/climbOn"]);

