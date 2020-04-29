function ajaxGo() {
    show.info("Send post ajax request!");
    var ajax = new XMLHttpRequest();
    ajax.open('post', 'info');
    ajax.send("say=" + say.value);
    ajax.onreadystatechange = () => {
        if (ajax.readyState != 4) return;
        show.info("[response state code " + ajax.status + "]" + ajax.responseText);
    };
}
var ws;
function websocketGo() {
    if (ws != null) {
        ws.send(say.value);
        return;
    }
    ws = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/infows");
    ws.onopen = () => {
        show.info("websocket open!");
        wsTest.value = "点我发送websocket消息！Click me to send msg with websocket!"
    };

    ws.onmessage = (e) => {
        var msg = e.data;
        show.info("[from server]" + msg);
    };

    ws.onclose = () => {
        show.info("websocket close!");
        ws = null;
        wsTest.value = "点我连接到websocket！Click me to connect websocket!";
    };

    ws.onerror = (e) => {
        show.info("websocket connect error!");
        console.log(e);
    }
}

window.onload = () => {
    let show = document.createElement('div');
    show.id = "show";
    document.body.appendChild(show);
    show.info = (msg) => {
        let div = document.createElement('div');
        div.innerHTML = msg;
        show.appendChild(div);
    }
}