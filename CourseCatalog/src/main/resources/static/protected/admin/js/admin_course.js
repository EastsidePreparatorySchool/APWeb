function getCourseStudentsFirst() {
    //document.getElementById("courseInput").value = "";
    var courseID = document.getElementById("courseInput").value;
    output("requesting students for course ..." + courseID);

    request({url: "/protected/getCourseStudentsFirst?id=" + courseID})
            .then(data => {
                output("success");
                eraseDiv("courseOutput");

                insertStudents("courseOutput", data);
            })
            .catch(error => {
                output("Error: " + error);
            });
}

function getCourseStudentsAll() {
    //document.getElementById("courseInput").value = "";
    var courseID = document.getElementById("courseInput").value;
    output("requesting students for course ..." + courseID);

    request({url: "/protected/getCourseStudentsAll?id="+courseID})
            .then(data => {
                output("success");
                eraseDiv("courseOutput");
                insertStudents("courseOutput", data);
            })
            .catch(error => {
                output("Error: " + error);
            });
}

function getCourseOfferings() {
    output("requesting all course offerings  ...");

    request({url: "/protected/getCourseOfferings", method: "GET"})
            .then(data => {
                output("success");
                eraseDiv("courseOutput");
                insertCourseOfferings("courseOutput", data);
            })
            .catch(error => {
                output("Error: " + error);
            });
}
function getCourseOfferingsFull() {
    output("requesting all course offerings  ...");

    request({url: "/protected/getCourseOfferings", method: "GET"})
            .then(data => {
                output("success");
                eraseDiv("courseOutput");

                insertCourseOfferingsFull("courseOutput", data);
            })
            .catch(error => {
                output("Error: " + error);
            });
}
