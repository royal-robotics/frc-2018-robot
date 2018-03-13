let stateValues = {};
let keyNames = {"/SmartDashboard/Controls/ClimberEnabled" : "Climb Enabled", "/SmartDashboard/DriveController/Gear" : "Gear", "/SmartDashboard/Intake/State" : "Intake State", "/SmartDashboard/Lift/Position" : "Lift Position"}; 

(function addKeyListeners(keys) {
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


const noRoutine = "NoRoutine";
let autoRoutines = [];
let autoSelected = "";
let autoSelectedAck = "";

$(() => {
    NetworkTables.addKeyListener("/SmartDashboard/AutoRoutines/RoutinesList", (key, value, isNew) => {
        autoRoutines = JSON.parse(value);
        console.assert(typeof(routines) == "array", "Expecting a json seralized array");
        renderAutoList();
    });

    $("#autoRoutines").on('change', () => {
        $("#autoRoutines").removeClass("auto-select");
        $("#autoRoutines").removeClass("auto-ack");
        $("#autoRoutines").addClass("auto-no-ack");

        console.log($("#autoRoutines").val());
        autoSelected = $("#autoRoutines").val();
        NetworkTables.putValue("/SmartDashboard/AutoRoutines/SelectedRoutine", $("#autoRoutines").val());
        renderAutoList();
    });

    var selectedRoutineAck = false;
    NetworkTables.addKeyListener("/SmartDashboard/AutoRoutines/SelectedRoutineAck", (key, value, isNew) => {
        console.log($("#autoRoutines").val() + " === " + value);
        autoSelectedAck = value;
        renderAutoList();
    });

});

function renderAutoList() {
    if(autoSelected === "") {
        $("#autoRoutines").addClass("auto-select");
    } else {
        $("#autoRoutines").removeClass("auto-select");

        console.log(autoSelected);

        if($("#autoRoutines").val() == autoSelectedAck) {
            $("#autoRoutines").addClass("auto-ack");
            $("#autoRoutines").removeClass("auto-no-ack");
        } else {
            $("#autoRoutines").addClass("auto-no-ack");
            $("#autoRoutines").removeClass("auto-ack");
        }
    }

    $("#autoRoutines").empty();
    createOption(noRoutine);

    autoRoutines.forEach(routine => {
        createOption(routine);
    });
    
    function createOption(name) {
        let isSelected = name == autoSelected;
        let option = $(`<option ${isSelected ? "selected" : ""}>${name}</option>`);
        $("#autoRoutines").append(option);
    }
}