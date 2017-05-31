<template>
<div class="home ibox-content">
    <div id="upload">
        <form id="myAwesomeDropzone" class="dropzone" method="POST" enctype="multipart/form-data">
            <div class="dropzone-previews"></div>
            <input type="hidden" id="111" value="333" />
            <button type="submit" class="btn btn-primary pull-right">Submit</button>
        </form>
    </div>
</div>
</template>

<script>
import Dropzone from '@/../static/js/dropzone'
// const Dropzone = require('@/../static/js/dropzone')
const host = 'http://localhost:8091'

export default {
    name: 'home',
    data() {
        return {}
    },
    methods: {
        initDropzone() {
            const self = this
            Dropzone.options.myAwesomeDropzone = {
                url: host + "/tool/upload",
                paramName: "file",
                autoProcessQueue: false,
                uploadMultiple: true,
                parallelUploads: 100,
                maxFiles: 100,
                method: "post",

                // Dropzone settings
                init: function() {
                    var myDropzone = this;

                    this.element.querySelector("button[type=submit]").addEventListener("click", function(e) {
                        e.preventDefault();
                        e.stopPropagation();
                        myDropzone.processQueue();
                    });
                    this.on("sendingmultiple", function() {});
                    this.on("successmultiple", function(files, response) {
                        if (response.success) {
                            $("#modelName").val(response.data);
                            self.$router.push('/uploaded')
                        } else {
                            alert(response.data)
                        }
                    });
                    this.on("errormultiple", function(files, response) {});
                }
            };
        }
    },
    created() {
        this.initDropzone()
    }
}
</script>

<style scoped>

</style>
