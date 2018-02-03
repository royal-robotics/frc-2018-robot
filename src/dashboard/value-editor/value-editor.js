// Value editor logic
var tunables = []; //TODO: Make this private to this module
var subtypes = {"Drive": "Velocity", "Arm": "Position"}; 

$(() => {
    console.log("test");
    $("#filter").on("change", filterchange);

    //Setup tunable listeners, makes sure we have all the tunables and their current values
    NetworkTables.addGlobalListener((key, value, isNew) => {
        const stSmartDashboard = "/SmartDashboard/";
        if(!key.startsWith(stSmartDashboard) || key.startsWith("/SmartDashboard/DB") || key.startsWith("Auth Select"))
            return;

        let keyShort = key.substr(stSmartDashboard.length);
        var keyPart = keyShort.split("/");
        subtypes[keyPart[0]] = keyPart[1];
        console.log(subtypes);
        if(isNew && tunables[keyShort] !== undefined)
            console.log("Warning: new tunable value already defined");
        
        tunables[keyShort] = value;
        updateTunablesList(true);
    }, /*call immediately with all tunables*/true);
});

function filterchange() {
    var selected = $("#filter").val();
    console.log(selected);
    tunables = [];
    tunables.push(subtypes [selected]);
    updateTunablesList(false);
}

function updateTunablesList(changefilter) {
    //Clearing and reinserting all the DOM elements isn't great, it's fast enough it works for now though
    $("#value-list").empty();
    if (changefilter){
        var filter = $("#filter");
        var options = ""
        for(var key in subtypes){
            console.log(key);
            options = options + " <option class='tunable-value' value='" + key + "'>" + key + "</option>";
        }
        console.log(options);
        filter.html(options); 
    }
    var selected = $("#filter").val();
    console.log(selected);
    for(let key in tunables) {
        let value = tunables[key];
        let type = typeof(value);

        var div = null;
        switch(type) {
            case "string":
                div = $("#tunable-string").clone();
                let inputSt = div.find(".tunable-value");
                inputSt.change(function() { console.log($(this).val())});
                inputSt.attr("value", value);
                break;
            case "number":
                div = $("#tunable-number").clone();
                let inputNu = div.find(".tunable-value");
                inputNu.change(function() {
                    console.log($(this).val());
                    console.log("/SmartDashboard/" + key);
                    NetworkTables.putValue("/SmartDashboard/" + key, parseFloat($(this).val()));
                });
                inputNu.attr("value", value);
                break;
            case "boolean":
                div = $("#tunable-boolean").clone();
                let inputBo = div.find(".tunable-value");
                value ? inputBo.attr("checked", "") : inputBo.removeAttr("checked");
                inputBo.change(function() { console.log($(this).prop("checked")); });
                break;
        }
        
        div.id = key; //consider prefixing this id with a unique identifier
        div.find(".tunable-key").append(document.createTextNode(key));
        $("#value-list").append(div);
    }
}
