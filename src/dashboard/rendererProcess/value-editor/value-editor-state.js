let tunables = []; //TODO: Make this private to this module
let subtypes = {};
let display = [];

//Setup tunable listeners, makes sure we have all the tunables and their current values
NetworkTables.addGlobalListener((key, value, isNew) => {
    const stSmartDashboard = "/SmartDashboard/";
    if(!key.startsWith(stSmartDashboard) || key.startsWith("/SmartDashboard/DB") || key.startsWith("Auth Select"))
        return;

    let keyShort = key.substr(stSmartDashboard.length);
    let keySplit = keyShort.indexOf("/");
    let keyPart1 = keyShort.substr(0,keySplit);
    let keyPart2 = keyShort.substr(keySplit + 1);
    let hasKey = subtypes.hasOwnProperty(keyPart1);

    if (isNew) {
        display.push(keyShort);
        if(hasKey) {
            let oldValue = subtypes[keyPart1];
            oldValue.push(keyPart2);
            subtypes[keyPart1] = oldValue;
        } else {
            subtypes[keyPart1] = [keyPart2];
        }
    }

    if(isNew && tunables[keyShort] !== undefined)
        console.log("Warning: new tunable value already defined");

    console.log(display);
    let areDifferent = display.includes(keyShort) && value != tunables[keyShort];
    console.log(areDifferent);
    tunables[keyShort] = value;
    if(typeof(updateTunablesList) != "undefined" && areDifferent) {
        console.log("updateTunables");
        updateTunablesList(isNew);
    }
    
}, true);
