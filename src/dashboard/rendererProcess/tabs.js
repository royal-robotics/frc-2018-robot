// Tab logic
$(() => {
    $("#tabs > li").each(function(index) {
        this.onclick = () => loadTab(this);
        if($(this).hasClass("active"))
            loadTab(this);
    });

    jQuery.ajax({
        url: "rendererProcess/camera/camera-save.js",
        dataType: 'script',
        success: function() {
            console.log("success");
        },
        async: true
    })
});

function loadTab(tab) {
    var path = $(tab).children("a").attr("name");
    $("#main").load(path + ".html");

    $("#tabs > li").each(function(index) {
        if(tab == this)
            $(this).addClass("active")
        else
            $(this).removeClass("active")
    });
}


function loadTabState(tab) {
    var path = $(tab).children("a").attr("name");
}