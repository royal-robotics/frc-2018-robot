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

    let picker = $(`#${id}`).colorpicker({
        parts:  ['bar', 'hsv', 'rgb', 'map', 'footer'],
        layout: {
            bar:        [0, 0, 1, 4],
            map:		[1, 0, 1, 5],
            rgb:        [2, 0, 1, 1],
            hsv:        [2, 1, 1, 1],
        },
        "closeOnOutside": false,
        "okOnEnter": true,
        "position": {
            "of": $(`#${id}`).parent(),
        },
        "select": function(ev, data) {
            var h = Math.round(data.hsv.h * 255);
            var s = Math.round(data.hsv.s * 255);
            var v = Math.round(data.hsv.v * 255);

            NetworkTables.putValue(`${key}/h`, h);
            NetworkTables.putValue(`${key}/s`, s);
            NetworkTables.putValue(`${key}/v`, v);

            var rgb = HSVtoRGB(data.hsv.h, data.hsv.s, data.hsv.v);
            // http://www.w3.org/TR/AERT#color-contrast
            var o = Math.round(((parseInt(rgb.r) * 299) + (parseInt(rgb.g) * 587) + (parseInt(rgb.b) * 114)) / 1000);

            $(`#${id}`).parent().find("span").css("color", `rgb(${rgb.r}, ${rgb.g}, ${rgb.b})`);    
            $(`#${id}`).parent().find("span").css("background-color", (o > 125) ? 'black' : 'white');
            $(`#${id}`).parent().find("span").text(`H: ${h} S: ${s} V: ${v}`);
        }
    });

    $(`#${id}`).parent().click(() => {
        picker.colorpicker('open');
    });
}

function HSVtoRGB(h, s, v) {
    var r, g, b, i, f, p, q, t;
    if (arguments.length === 1) {
        s = h.s, v = h.v, h = h.h;
    }
    i = Math.floor(h * 6);
    f = h * 6 - i;
    p = v * (1 - s);
    q = v * (1 - f * s);
    t = v * (1 - (1 - f) * s);
    switch (i % 6) {
        case 0: r = v, g = t, b = p; break;
        case 1: r = q, g = v, b = p; break;
        case 2: r = p, g = v, b = t; break;
        case 3: r = p, g = q, b = v; break;
        case 4: r = t, g = p, b = v; break;
        case 5: r = v, g = p, b = q; break;
    }
    return {
        r: Math.round(r * 255),
        g: Math.round(g * 255),
        b: Math.round(b * 255)
    };
}