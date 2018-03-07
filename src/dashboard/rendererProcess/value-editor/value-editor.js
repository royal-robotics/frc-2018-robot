// Value editor logic
$(() => {
    $("#filter").on("change", filterchange);    

    function filterchange() {
        console.log("Filter changed");
        updateTunablesList(false);
    }
    updateTunablesList(true);
});

function updateTunablesList(changefilter) {
    //Clearing and reinserting all the DOM elements isn't great, it's fast enough it works for now though
    $("#value-list").empty();
    if (changefilter) {
        let filter = $("#filter");
        let options = "<option value=''>All</option>";
        for(let key in subtypes){
            options = options + " <option class='tunable-value' value='" + key + "'>" + key + "</option>";
        }
        filter.html(options);
    }

    let selected = $("#filter").val();
    let selectedValue = subtypes[selected];
    let mappedValue = [];
    if(selectedValue)
        mappedValue = selectedValue.map(function(value) {
            return selected + "/" + value;
    });

    let allKeys = [];
    for(let key in tunables) {
        // True if either the key starts with the filter or the All filter is selected
        console.log(key);
        let filtered = selected == "" || mappedValue.includes(key);
        if (filtered) {
            allKeys.push(key);
        }
    }

    console.log(allKeys[0]);
    let sortedKeys = allKeys.sort();
    console.log(sortedKeys[0]);

    for (let key in sortedKeys) {
        let keyValue = sortedKeys[key];
        let value = tunables[keyValue];
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
                    console.log("/SmartDashboard/" + keyValue);
                    NetworkTables.putValue("/SmartDashboard/" + keyValue, parseFloat($(this).val()));
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

        div.id = keyValue; //consider prefixing this id with a unique identifier
        div.find(".tunable-key").append(document.createTextNode(keyValue));
        $("#value-list").append(div);
    }
}
