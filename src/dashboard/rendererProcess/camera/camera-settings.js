var baseUrl = "http://roborio-2522-frc.local:1181";
var streamUrl = `${baseUrl}/stream.mjpg`;
var settingsUrl = `${baseUrl}/settings.json`;


$(() => {
    $("#detector").change(updateValues);
    $("#filters").change(updateValues);

    $("#blur-kernel").change(updateValues);
    $("#blur-sigma").change(updateValues);

    ipc.on('connected', (ev, isConnected) => updateValues());
});


function updateValues() {
    NetworkTables.putValue("/SmartDashboard/Camera/Detector", $("#detector option:selected").val());
    NetworkTables.putValue("/SmartDashboard/Camera/Filter", $("#filters option:selected").val());

    NetworkTables.putValue("/SmartDashboard/Camera/Filter/blob/blur/kernelSize", $("#blur-kernel").val());
    NetworkTables.putValue("/SmartDashboard/Camera/Filter/blob/blur/sigma", $("#blur-sigma").val());
}

$(() => {
    setupDashboardColorPicker("colorpicker-low", "/SmartDashboard/Camera/Filter/blob/color/low");
    setupDashboardColorPicker("colorpicker-high", "/SmartDashboard/Camera/Filter/blob/color/high");
});


function setupDashboardColorPicker(id, key) {
    $(`#${id}`).colorpicker({
        parts:  ['bar', 'hsv', 'rgb', 'map'],
        layout: {
            bar:        [0, 0, 1, 4],
            map:		[1, 0, 1, 5],
            rgb:        [2, 0, 1, 1],
            hsv:        [2, 1, 1, 1],
        },
        "select": function(ev, data) {
            var h = Math.round(data.hsv.h * 255);
            var s = Math.round(data.hsv.s * 255);
            var v = Math.round(data.hsv.v * 255);

            $(`#${id}`).val(`H: ${h} S: ${s} V: ${v}`);
            NetworkTables.putValue(`${key}/h`, h);
            NetworkTables.putValue(`${key}/s`, s);
            NetworkTables.putValue(`${key}/v`, v);
        }
    });
}