var session = "";


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
;







function sendMessage() {
    var s = document.getElementById("input").value;
    document.getElementById("input").value = "";

    request({url: "putmessage", method: "PUT", body: s})
            .then(data => {
                session = data;
            })
            .catch(error => {
                output("Error: " + error);
            });
}


function submitForm(oFormElement) {
    request({url: "postmessage", method: "POST", body: new FormData(oFormElement)})
            .then(data => {
                session = data;
            })
            .catch(error => {
                output("Error: " + error);
            });
    return false;
}


function getNewMessages() {
    request({url: "getnewmessages"})
            .then(data => {
                output(data);
            })
            .catch(error => {
                output("Error: " + error);
            });
}



function output(message) {

    document.getElementById("output").innerHTML += message;
}


setInterval(getNewMessages, 100);



