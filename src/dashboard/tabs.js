$(document).ready(function() {
    $("#tabs > li").each(function(index) {
        this.onclick = () => loadTab(this);
        if($(this).hasClass("active"))
            loadTab(this);
    });
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
