
Dropzone.options.myAwesomeDropzone = {
    url:"http://localhost:8091/tool/upload",
    paramName:"file",
    autoProcessQueue: false,
    uploadMultiple: true,
    parallelUploads: 100,
    maxFiles: 100,
    method:"post",

    // Dropzone settings
    init: function() {
        var myDropzone = this;

        this.element.querySelector("button[type=submit]").addEventListener("click", function(e) {
            e.preventDefault();
            e.stopPropagation();
            myDropzone.processQueue();
        });
        this.on("sendingmultiple", function() {
        });
        this.on("successmultiple", function(files, response) {
            console.log(response.success);
            if (response.success == true) {
                window.location.href = window.location.href+"#uploaded";
                window.location.reload()
            }
        });
        this.on("errormultiple", function(files, response) {
        });
    }
};