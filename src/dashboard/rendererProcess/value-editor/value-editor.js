// Value editor logic
let tunables = []; //TODO: Make this private to this module
let subtypes = {};

$(() => {
    $("#filter").on("change", filterchange);

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

        if(hasKey) {
            let oldValue = subtypes[keyPart1];
            oldValue.push(keyPart2);
            subtypes[keyPart1] = oldValue;
        } else {
            subtypes[keyPart1] = [keyPart2];
        }
        console.log(subtypes);
        if(isNew && tunables[keyShort] !== undefined)
            console.log("Warning: new tunable value already defined");
        
        tunables[keyShort] = value;
        updateTunablesList(isNew); // Only update filter if this is a new value
    }, /*call immediately with all tunables*/true);
});

function filterchange() {
    updateTunablesList(false);
}

function updateTunablesList(changefilter) {
    //Clearing and reinserting all the DOM elements isn't great, it's fast enough it works for now though
    $("#value-list").empty();
    if (changefilter) {
        let filter = $("#filter");
        let options = ""
        for(let key in subtypes){
            console.log(key);
            options = options + " <option class='tunable-value' value='" + key + "'>" + key + "</option>";
        }
        filter.html(options); 
    }

    let selected = $("#filter").val();
    let selectedValue = subtypes[selected];
    let mappedValue = selectedValue.map(function(value) {
        return selected + "/" + value;
    });

    for(let key in tunables) {
        // True if either the key starts with the filter or the All filter is selected
        let filtered = mappedValue.includes(key) || selected == "";
        if (filtered) {
            let value = tunables[key];
            let type = typeof(value);
    
            let div = null;
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
}
