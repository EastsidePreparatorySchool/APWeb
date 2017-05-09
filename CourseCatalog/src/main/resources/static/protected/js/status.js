function getMySelections() {
    request({url: "/protected/getMyRequests"})
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

getMySelections();
