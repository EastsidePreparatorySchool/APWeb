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
    
};

function test () {
    request({url: "hello"})
        .then(data => {
            output("Result: " + data);
        })
        .catch(error => {
            ouput("Error: " + error);
        });
}

function sendMessage() {
    var textInput = document.getElementById("input").value;
    document.getElementById("input").value = "";

    request({url: "putmsg", method:"PUT", body: textInput})
        .then(data => {
            output("Result: " + data);
        })
        .catch(error => {
            ouput("Error: " + error);
        });
}

function getNewMessages() {
    request({url: "getnewmessages"})
        .then(data => {
            output(data);
        })
        .catch(error => {
            ouput("Error: " + error);
        });
        }

function output (message) {
    document.getElementById("output").innerHTML += message;
}

setInterval(getNewMessages, 100);
    