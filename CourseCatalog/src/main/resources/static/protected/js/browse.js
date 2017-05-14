function getSpecificCourseOfferings() {
    output("Trying to get specific course offerings...");

    request({url: "/protected/getSpecificCourses?disc="+radioCheck("Discipline")+"&grad="+radioCheck("Grades")+"&len="+radioCheck("Duration")})
            .then(data => {
                output("success");
                eraseDiv("courseOutput");
                insertStudents("courseOutput", data);
            })
            .catch(error => {
                output("Error: " + error);
            });
}

function radioCheck(radioName) {
    radioArray = document.getElementsByName(radioName);
    console.log(radioArray);
    console.log("derp");
    for (var counter = 0; counter < radioArray.length; counter++) {
        if (radioArray[counter].checked === true) {
            console.log(radioArray[counter].value);
            return radioArray[counter].value;
        }
    }
}