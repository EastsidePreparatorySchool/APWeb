function getCourseStudentsFirst() {
    //document.getElementById("courseInput").value = "";
    var courseID = document.getElementById("courseInput").value;
    output("requesting students for course ...");

    request({url: "protected/getCourseStudentsFirst", method: "GET", body: courseID})
            .then(data => {
                output("success");
                output(data);
                insertStudents("courseOutput", data);
            })
            .catch(error => {
                output("Error: " + error );
            });
}

function getCourseStudentsAll() {
    //document.getElementById("courseInput").value = "";
    var courseID = document.getElementById("courseInput").value;
    output("requesting students for course ...");

    request({url: "protected/getCourseStudentsAll", method: "GET", body: courseID})
            .then(data => {
                output("success");
                output(data);
                insertStudents("courseOutput", data);
            })
            .catch(error => {
                output("Error: " + error );
            });
}

function getCourseOfferings() {
    output("requesting all course offerings  ...");

    request({url: "protected/getCourseOfferings", method: "GET"})
            .then(data => {
                output("success");
                output(data);
            })
            .catch(error => {
                output("Error: " + error);
            });
}
