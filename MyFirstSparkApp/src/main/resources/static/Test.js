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




function simpleSyncRequest(obj) {
    let xhr = new XMLHttpRequest();
    xhr.open(obj.method || "GET", obj.url, false);

    xhr.send(obj.body);
    
    if (xhr.status >= 200 && xhr.status < 300) {
        output("result: " + xhr.response);
    } else {
        output("error" + xhr.statusText);
    }
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


function trial () {
    simpleSyncRequest({url: "myButton"});

/*
        .then(data => {
            testOut("Result: " + data);
        })
        .catch(error => {
            testOut("Error: " + error);
        });
*/
}


function output (message) {
    document.getElementById("output").innerHTML += message + "<br>";
}
function testOut (message) {
    document.getElementById("output").innerHTML += message + "<br>";
}

output("ready");
    