
$(document).ready(function() {
    NetworkTables.addGlobalListener(globalValueListener, true);
    globalValueListener("Hello", "value", true);
});

function globalValueListener(key, value, isNew) {
    if (isNew && !document.getElementById(key)) {
        insertValue(key, value);
    }
}


function insertValue(key, value) {
    var div = $("#value-template").clone();
    div.id = key; //consider prefixing this id with a unique identifier
    div.append(document.createTextNode(`${key} ${value}`));
    $("#value-list").append(div);
}