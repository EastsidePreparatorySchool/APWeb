
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




<<<<<<< HEAD
function simpleSyncRequest(obj) {
    let xhr = new XMLHttpRequest();
    xhr.open(obj.method || "GET", obj.url, false);

    xhr.send(obj.body);
    
    if (xhr.status >= 200 && xhr.status < 300) {
        output("result: " + xhr.response);
    } else {
        output("error" + xhr.statusText);
=======








function simpleSyncRequest(obj) {
    
    let xhr = new XMLHttpRequest();
    xhr.open(obj.method || "GET", obj.url, false);

    //output("after send");
    xhr.send(obj.body);
    //output("after send");

    if (xhr.status >= 200 && xhr.status < 300) {
        output("Result: " + xhr.response);
    } else {
        output("Error: " + xhr.statusText);
>>>>>>> 9e8da881dbce8c07685c5469b3459a32095127da
    }
}




<<<<<<< HEAD
=======










function simpleAsyncRequest(obj, callback) {
    
        let xhr = new XMLHttpRequest();
        xhr.open(obj.method || "GET", obj.url, true);
        xhr.onload = ()=>{callback(xhr)};

        //output("after send");
        xhr.send(obj.body);
        //output("after send");
   
   
    
}


function testAsync() {
    simpleAsyncRequest ({url:"hello"}, (xhr)=> {
        if (xhr.status >= 200 && xhr.status < 300) {
            output("Result: " + xhr.response);
        } else {
            output("Error: " + xhr.statusText);
        }
    });
}


>>>>>>> 9e8da881dbce8c07685c5469b3459a32095127da
function test () {
    request({url: "hello"})
        .then(data => {
            output("Result: " + data);
        })
        .catch(error => {
            ouput("Error: " + error);
        });
}


<<<<<<< HEAD
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


=======



function testSync() {
    //output("simple sync request");
    simpleSyncRequest({url: "hello"});
}


function shutdown() {
    //output("simple sync request");
    simpleAsyncRequest({url: "shutdown"}, null);
}






>>>>>>> 9e8da881dbce8c07685c5469b3459a32095127da
function output (message) {
    //output("promise request");
    document.getElementById("output").innerHTML += message + "<br>";
}
<<<<<<< HEAD
function testOut (message) {
    document.getElementById("output").innerHTML += message + "<br>";
}

output("ready");
    
=======
    
output("Ready");
>>>>>>> 9e8da881dbce8c07685c5469b3459a32095127da
