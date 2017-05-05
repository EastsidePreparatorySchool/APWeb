function getCourseOfferings() {
    request({url: "protected/getCourseOfferings", method: "GET", body: ""})
            .then(data => {
                insertCourseOfferings("courseOutput", data);
            })
            .catch(error => {
                output("Error: " + error + "<br>");
            });
}

function insertCourseOfferings(parentID, courseOfferings) {
    //This method takes in a parent div and then a JSON string of course offerings
    var myTable = "<table><tr><th style='width: 100px; color: red;'>ID</th>";
    myTable += "<th style='width: 5px; color: red; text-align: right;'>Course ID</th>";
    myTable += "<th style='width: 25px; color: red; text-align: right;'>Year ID</th>";
    myTable += "<th style='width: 100px; color: red; text-align: right;'>Description</th>";
    myTable += "<th style='width: 25px; color: red; text-align: right;'>Extra Info</th><tr>";
    myTable += "<th style='width: 50px; color: red; text-align: right;'>Grade Levels</th><tr>";
    
    var courseOfferingsJSONString = JSON.parse(courseOfferings);
    var array = new Array();
    for (var i = 0; i < courseOfferingsJSONString.length; i++) {
        var obj = {
            id: courseOfferingsJSONString[i].id,
            courseId: courseOfferingsJSONString[i].courseId,
            yearId: courseOfferingsJSONString[i].yearId,
            description: courseOfferingsJSONString[i].description,
            info: courseOfferingsJSONString[i].info,
            gradeLevels: courseOfferingsJSONString[i].gradeLevels
        };
        array.push(obj);
    }
    
    for (var i = 0; i < array.length; i++) {
        var tableRow = "<tr>";
        var id = "<td>" + array[i].id; + "</td>";
        var courseId = "<td>" + array[i].courseId; + "</td>";
        var yearId = "<td>" + array[i].yearId; + "</td>";
        var description = "<td>" + array[i].description; + "</td>";
        var info = "<td>" + array[i].info; + "</td>";
        var gradeLevels = "<td>" + array[i].gradeLevels; + "</td>";
        tableRow += id;
        tableRow += courseId;
        tableRow += yearId;
        tableRow += description;
        tableRow += info;
        tableRow += gradeLevels;
        tableRow += "</tr>";
        myTable += tableRow;
    }
    
    myTable += "</table>";

    output(myTable, parentID);  
}