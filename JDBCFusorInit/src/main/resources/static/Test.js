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

function showDukakis () {
    request({url: "protected/getdukakisfilms"})
            .then(data => {
                data = JSON.parse(data);
                for (var i = 0; i < data.length; i++) {
                    output("<br>"+data[i].name + ", "+  data[i].year+ "<br>");
                }
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


function submitForm(form) { //from OneNote to upload a form
    var body = new FormData(form);

    let xhr = new XMLHttpRequest();
    xhr.open("POST", "upload");
    // must not do this!!!! xhr.setRequestHeader("Content-type", "multipart/form-data");

    xhr.onload = () => {
        if (xhr.status >= 200 && xhr.status < 300) {
            output("returned " + xhr.response);
        } else {
            output("error " + xhr.statusText);
        }
    };
    xhr.onerror = () => {
        output("error " + xhr.statusText);
    };
    xhr.send(body);
    return false;
}


