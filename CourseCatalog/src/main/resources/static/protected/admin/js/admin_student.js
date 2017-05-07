function getStudents() {
    request({url: "/protected/getStudents", method: "GET", body: ""})
            .then(data => {
                if (data != null) {
                    eraseDiv("output");

                    insertStudents("output", data);
                } else {
                    output("no data");
                }
            })
            .catch(error => {
                output("Error: " + error);
            });
}


function getStudentSelections() {
    request({url: "/protected/getAllRequests", method: "GET", body: ""})
            .then(data => {
                eraseDiv("selections");
                if (data !== undefined && data !== null) {

                    outputTo("selections", data + "<br>");
                } else {
                    outputTo("selections", "no data<br>");
                }
            })
            .catch(error => {
                output("Error: " + error);
            });
}
