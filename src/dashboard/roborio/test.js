const roborio = require("./roborio");

(async function() {
    try {
        let ip = (await (roborio.getIPAsync()));    
    } catch (e) {
        console.log(e.message);
    }
}());