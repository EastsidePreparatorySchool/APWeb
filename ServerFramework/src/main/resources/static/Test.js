var initials = "";


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







function putMessage() {
    var s = document.getElementById("input").value;
    document.getElementById("input").value = "";

    request({url: "protected/put", method: "PUT", body: s})
            .then(data => {
                output(data + "<br>");
            })
            .catch(error => {
                output("Error: " + error);
            });
}


function postMessage(oFormElement) {
    request({url: "protected/post", method: "POST", body: new FormData(oFormElement)})
            .then(data => {
                output(data + "<br>");
            })
            .catch(error => {
                output("Error: " + error);
            });
    return false;
}




function get () {
    request({url: "protected/get"})
            .then(data => {
                output(data + "<br>");
            })
            .catch(error => {
                output("Error: " + error);
            });

}

function login() {
    let init = window.prompt("Please enter your initials");
    request({url: "login", method: "put", body: init})
            .then(data => {
                 output("Hello " + data + "<br>");
            })
            .catch(error => {
                output("Error: " + error);
            });
}



function output(message) {
    document.getElementById("output").innerHTML += message;
}


