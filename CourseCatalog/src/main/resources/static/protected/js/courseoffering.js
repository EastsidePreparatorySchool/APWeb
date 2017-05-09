
function insertCourseOfferings(parentID, coString) {
    //This method takes in a parent div and then a JSON string of students
    output("insertCourseOfferings");
    var myTable = "<table><tr><th style='width: 100px; color: red;text-align: left;'>ID</th>";
    myTable += "<th style='width: 200px; color: red; text-align: left;'>Name</th>";

    var cos = JSON.parse(coString);
    if (cos === undefined || cos === null) {
        outputTo(parentID, "no data<br>");
        return;
    }

    for (var i = 0; i < cos.length; i++) {
        var tableRow = "<tr>" + "<td>" + cos[i].id + "</td>"
                + "<td>" + cos[i].name + "</td>";
        myTable += tableRow;
    }

    myTable += "</table>";

    outputTo(parentID, myTable);
}

function insertCourseOfferingsFull(parentID, courseOfferings) {
    //This method takes in a parent div and then a JSON string of course offerings
    var myTable = "<table><tr><th style='width: 50px; color: red;text-align: left;'>ID</th>";
    myTable += "<th style='width: 100px; color: red; text-align: left;'>Course ID</th>";
    myTable += "<th style='width: 200px; color: red; text-align: left;'>Name</th>";
    myTable += "<th style='width: 50px; color: red; text-align: left;'>Year ID</th>";
    myTable += "<th style='width: 300px; color: red; text-align: left;'>Description</th>";
    myTable += "<th style='width: 100px; color: red; text-align: left;'>Extra Info</th>";
    myTable += "<th style='width: 50px; color: red; text-align: left;'>Grade Levels</th><tr>";

    var courseOfferingsJSONString = JSON.parse(courseOfferings);
    var array = new Array();
    for (var i = 0; i < courseOfferingsJSONString.length; i++) {
        var obj = {
            id: courseOfferingsJSONString[i].id,
            courseId: courseOfferingsJSONString[i].courseId,
            name: courseOfferingsJSONString[i].name,
            yearId: courseOfferingsJSONString[i].yearId,
            description: courseOfferingsJSONString[i].description,
            info: courseOfferingsJSONString[i].info,
            gradeLevels: courseOfferingsJSONString[i].gradeLevels
        };
        array.push(obj);
    }

    for (var i = 0; i < array.length; i++) {
        var tableRow = "<tr>";
        var id = "<td>" + array[i].id;
        +"</td>";
        var courseId = "<td>" + array[i].courseId;
        +"</td>";
        var name = "<td>" + array[i].name;
        +"</td>";

        var yearId = "<td>" + array[i].yearId;
        +"</td>";
        var description = "<td>" + array[i].description;
        +"</td>";
        var info = "<td>" + array[i].info;
        +"</td>";
        var gradeLevels = "<td>" + array[i].gradeLevels;
        +"</td>";
        tableRow += id;
        tableRow += courseId;
        tableRow += name;
        tableRow += yearId;
        tableRow += description.substring(0,50)+"...";
        tableRow += info;
        tableRow += gradeLevels;
        tableRow += "</tr>";
        myTable += tableRow;
    }

    myTable += "</table>";

    outputTo(parentID, myTable);
}
