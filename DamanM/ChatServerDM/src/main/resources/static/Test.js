var session = "";
function myRequest(obj) {
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

function sendMessage() {
    var m = document.getElementById("input").value;
    document.getElementById("input").value = "";
    myRequest({url: "send", method: "PUT", body: m})
            .then(data => {
                session = data;
            })
            .catch(error => {
                output("Error: " + error);
            });
}

function test() {
    myRequest({url: "hello"})
            .then(data => {
                output("Result: " + data);
            })
            .catch(error => {
                output("Error: " + data);
            });
}

function logIn() {
    let username = prompt("What is your username?");
    myRequest({url: "login?username=" + username})
            .then(data => {
                output("Result: " + data);
            })
            .catch(error => {
                output("Error: " + data);
            });
}

function output(message) {
    document.getElementById("output").innerHTML += message + "<br>";
}

