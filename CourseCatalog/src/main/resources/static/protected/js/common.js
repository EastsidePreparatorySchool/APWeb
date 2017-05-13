var student_name = null;
var student_login = null;
var student_id = null;

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

function requestWithHeader(obj, header, value) {
    return new Promise((resolve, reject) => {
        let xhr = new XMLHttpRequest();
        xhr.setRequestHeader(header, value);
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

function requestWithJSON(obj) {
    return new Promise((resolve, reject) => {
        let xhr = new XMLHttpRequest();
        xhr.open(obj.method || "GET", obj.url);
        xhr.setRequestHeader("Content-type", "application/json");


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



function submitForm(tourl, oFormElement) {
    request({url: tourl, method: "POST", body: new FormData(oFormElement)})
            .then(data => {
            })
            .catch(error => {
                output("Error: " + error + "<br>");
            });
    return false;
}




function output(message) {
    document.getElementById("output").innerHTML += message + "<br>";
}

function outputTo(divid, message) {
    document.getElementById(divid).innerHTML += message;
}


function eraseDiv(divToErase) {
    document.getElementById(divToErase).innerHTML = "";
}


function checkTimeout() {
    //output ("checking liveness");
    request({url: "/protected/checktimeout", method: "get"})
            .then(data => {
                //output(data);
            })
            .catch(error => {
                location.assign("/expired.html");
            });

}

function logout() {
    request({url: "/logout", method: "post"})
            .then(data => {
                //output(data);
                location.assign("/login.html");

            })
            .catch(error => {
                location.assign("/login.html");
            });

}

function updateName() {
    var span = document.getElementById("nameSpan");
    if (span !== null) {
        request({url: "/protected/name", method: "get"})
                .then(data => {
                    //output("Name: "+data);
                    span.innerHTML = data;
                    student_name = data.substring(0, data.indexOf("("));
                    student_login = data.substring(data.indexOf("(")+1, data.indexOf(","));
                    student_id = Number(data.substring(data.indexOf(",")+2, data.length-1));
                    console.log(student_name);
                    console.log(student_login);
                    console.log(student_id);
                    
                })
                .catch(error => {
                    output("Error: " + error);
                });
    }
}




if (!(location.href.toLowerCase().endsWith("login.html") || location.href.toLowerCase().endsWith("expired.html"))) {
    setInterval(checkTimeout, 1000);
    updateName();
}


