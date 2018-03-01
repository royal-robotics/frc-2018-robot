// Tab logic
$(() => {
    $("#tabs > li").each(function(index) {
        loadTabState(this, () => {
            this.onclick = () => loadTab(this);
            if($(this).hasClass("active"))
                loadTab(this);
        });
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


function loadTabState(tab, callback) {
    var name = $(tab).children("a").attr("name");
    var path = `rendererProcess/${name}/${name}-state.js`

    //This will throw a ERR_FILE_NOT_FOUND if the state file doesn't exist
    $.ajax({
        url: path,
        dataType: 'script',
        success: callback,
        error: callback,
        async: true
    });
    
}