function getSpecificCourseOfferings(discipline, gradeLevel, courseLength) {
    //document.getElementById("courseInput").value = "";
    var courseID = document.getElementById("courseInput").value;
    output("requesting students for course ..." + courseID);

    request({url: "/protected/admin/getSpecificCourseOfferings?id="+courseID})
            .then(data => {
                output("success");
                eraseDiv("courseOutput");
                insertStudents("courseOutput", data);
            })
            .catch(error => {
                output("Error: " + error);
            });
}
