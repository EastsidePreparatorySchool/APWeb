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







function sendMessage() {
    // make sample object
    var obj = {"table": "customers", "limit": 20};

    var s = JSON.stringify(obj);
    console.log(s);
    console.log(JSON.parse(s).table);


    var s = document.getElementById("input").value;
    document.getElementById("input").value = "";

    request({url: "protected/putmessage", method: "PUT", body: s})
            .then(data => {
                session = data;
            })
            .catch(error => {
                output("Error: " + error);
            });
}

var count = 0;

function doStress() {
    setInterval(stress, 10);
}

function stress() {
    var s = document.getElementById("input").value;
    document.getElementById("input").value = "";

    request({url: "protected/putmessage", method: "PUT", body: String(Math.random())})
            .then(data => {
                ;
            })
            .catch(error => {
                output("Error: " + error);
            });

}

function submitForm(oFormElement) {
    request({url: "protected/postmessage", method: "POST", body: new FormData(oFormElement)})
            .then(data => {
                session = data;
            })
            .catch(error => {
                output("Error: " + error);
            });
    return false;
}




function getNewMessages() {
    if (initials !== "") {
        request({url: "protected/getnewmessages"})
                .then(data => {
            if (data !== "[]")
            output(data +"<br>");
            return;
                    let messages = JSON.parse(data);
                    for (var i = 0; i < messages.length; i++) {
                        let s = messages[i];
                        if (!s.startsWith(initials)) {
                            s = "&nbsp;&nbsp;&nbsp;&nbsp;" + s; 
                        }
                        output(s + "<br>");
                    }
                })
                .catch(error => {
                    output("Error: " + error);
                });
    }
}

function login() {
    initials = window.prompt("Please enter your initials");
    request({url: "login", method: "put", body: initials})
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


setInterval(getNewMessages, 100);



