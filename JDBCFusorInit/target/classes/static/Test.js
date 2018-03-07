let initials = "";
var k;


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

static int getk() {
        int k = 0;
        try {

            String query2 = "select Picture from pictures;"; //select all pictures uploaded
            PreparedStatement ps2 = conn.prepareStatement(query2); //create prepared statement
            ResultSet rs2 = ps2.executeQuery(); //create result set
            
            //add to k while there are still pictures - find the total number
            while (rs2.next()) {
                k++;
            }
        } catch (Exception e) {
            System.err.println("Got an exception in getk" + e);
        }

        return k;
    }

    static ArrayList<byte[]> getImage(spark.Request req, spark.Response res) {
        int k = getk(); //use total number 
        byte[] data = null; //make empty byte array for photos
        ArrayList photos = new ArrayList<byte[]>(); //make arraylist of byte arrays to return
        try {
            //get all images from db
            for (int i = 1; i <= k; i++) {
                
                String query = "select Picture as file from pictures WHERE Picture_id = (?);"; //select individual images based on id

                PreparedStatement preparedStmt = conn.prepareStatement(query); //prepare statement
                preparedStmt.setInt(1, i); //set variable in statement to i so as to loop through and return all of them
                ResultSet rs = preparedStmt.executeQuery(); //execute statement
                
                //add bytes from BLOB file into array
                while (rs.next()) {
                    data = rs.getBytes("file");
                }
                photos.add(data); //add each photo to arraylist
            }

            return photos;

        } catch (Exception e) {
            System.err.println("Got an exception in getImage" + e);
        }

        return null;
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
    initials = window.prompt("Please enter your full name");
    request({url: "login", body: initials, method: "POST"})
            .then(data => {
                //still deciding whether or not to display anything here
                
                 output("Thank you for logging in, " + data + ".");
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


