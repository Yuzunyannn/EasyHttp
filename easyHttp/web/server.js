function main(driver) {
    driver.mapping("/img/*.png", driver.folder("img"));
    driver.mapping("/js/*.js", driver.folder("js"));
    driver.mapping("/css/*.css", driver.folder("css"));
    driver.mapping("/home", home);
    driver.mapping("/info", info);
    driver.mapping("/login", login);
    driver.mapping("/infows", infows);
}

var home = {
    handle: function (request, response) {
        return content.file("home.html");
    }
}

var info = {
    handle: function (request, response) {
        var say = request.getParameter("say");
        response.setCookie(http.cookie("say", say));
        var s = request.getSession();
        if ("whatever".equals(s.getAttribute("usernmae"))) return funny(say);
        else return "请点击测试登陆按钮，登陆后可以看到其他内容！";
    }
}

var login = {
    handle: function (request, response) {
        var j = JSON.stringify({ ok: 1 });
        var s = request.getSession();
        s.setAttribute("usernmae", "whatever");
        return content.json("{\"code\":1}");
    }
}

function funny(str) {
    var something = ["やばいですね～", "你好", "hi", "hello"];
    return something[parseInt(Math.random() * something.length)];
}

var infows = {
    accpet: function () {
        return true;
    },

    onConnect: function (ws) {

    },
    onRecv: function (ws, msg) {
        ws.send(funny(msg.toString()));
    },
    onClose: function (ws) {

    },
}