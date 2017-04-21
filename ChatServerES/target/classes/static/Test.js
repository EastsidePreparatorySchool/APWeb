function request(obj) {
    return new Promise((resolve, reject) => {
        let xhr = new XMLHttpRequest();
        xhr.open(obj.method || "GET", obj.url);

        xhr.onload = () => {
            if (xhr.status >= 200 && xhr.status < 300) {
                resolve(xhr.response);
            } else {
                reject(xhr.statusText);
            }
        };
        xhr.onerror = () => reject(xhr.statusText);

        xhr.send(obj.body);
    });
}
function mySend(obj) {

    let xhr = new XMLHttpRequest();
    xhr.open(obj.method || "GET", obj.url, false);
    xhr.send(obj.body);
    if (xhr.status >= 200 && xhr.status < 300) {
        output("Result: " + xhr.response);
    } else {
        output("Error: " + xhr.statusText);
    }
}
function submitForm(oFormElement) {
    document.getElementById("post").value = "";
    request({url: "protected/postmessage", method: "POST", body: new FormData(oFormElement)})
            .then(data => {
            })
            .catch(error => {
                output("Error: " + error + "<br>");
            });
    return false;
}
function sendMsg() {
    var s = document.getElementById("input").value;
    document.getElementById("input").value = "";
    request({url: "protected/putmessage", method: "PUT", body: s})
            .then(data => {
            })
            .catch(error => {
                output("Error: " + error + "<br>");
            });
}
var initials = "";
function login() {
    initials = prompt("Halt! State your name");
    request({url: "login", method: "put", body: initials})
            .then(data => {
                output("Hello " + data + "<br>");
            })
            .catch(error => {
                output("Error " + error + "<br>");
            });
}

function getNewMessages() {
    if (initials !== "") {
        request({url: "protected/getnewmessages"})
                .then(data => {
                    output(data);
                })
                .catch(error => {
                    output("Error " + error + "<br>");
                });
    }
}
function output(message) {
    document.getElementById("output").innerHTML += message;
}
function doStress() {
    setInterval(sendMsg1000, 10);
}
function sendMsg1000() {
    var s = document.getElementById("input2").value;
    request({url: "protected/putmessage", method: "PUT", body: s})
            .then(data => {
            })
            .catch(error => {
                output("Error: " + error + "<br>");
            });
}
setInterval(getNewMessages, 100);
