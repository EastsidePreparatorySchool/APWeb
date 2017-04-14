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
function test2 () {
    mySend({url: "Sandwich"});
}
function test3 () {
    mySend({url: "shutdown"});
}
function sendMsg() {
    var s = document.getElementById("input").value;
    document.getElementById("input").value = "";
    request({url:"putmessage", method:"PUT", body:s})
            .then(data => {
            })
            .catch(error => {
                output("Error: " + error);
            });
}


function test () {
    request({url: "hello"})
        .then(data => {
            output("Result: " + data);
        })
        .catch(error => {
            ouput("Error: " + error);
        });
}

function output (message) {
    document.getElementById("output").innerHTML += message + "<br>";
}
    