<template>
<div class="home ibox-content">
    <!-- <div id="dataframe"></div> -->
    <div id="upload">
        <form id="myAwesomeDropzone" class="dropzone" method="POST" enctype="multipart/form-data">
            <div class="dropzone-previews"></div>
            <input type="hidden" id="111" value="333" />
            <button type="submit" class="btn btn-primary pull-right">Submit</button>
        </form>
    </div>
    <div id="variableSelect">

    </div>
</div>
</template>

<script>
const Dropzone = require('@/../static/js/dropzone')
export default {
    name: 'home',
    data() {
        return {}
    },
    methods: {
        initDropzone() {
            // var host = 'http://localhost:8091'
            const host = 'http://101.71.245.166:8091'
            const self = this
            Dropzone.options.myAwesomeDropzone = {
                url: host + '/tool/upload',
                paramName: 'file',
                autoProcessQueue: false,
                uploadMultiple: true,
                parallelUploads: 100,
                maxFiles: 100,
                method: 'post',
                // Dropzone settings
                init: function() {
                    const myDropzone = this

                    this.element.querySelector('button[type=submit]').addEventListener('click', function(e) {
                        e.preventDefault()
                        e.stopPropagation()
                        myDropzone.processQueue()
                    })
                    this.on('sendingmultiple', function() {
                    })
                    this.on('successmultiple', function(files, response) {
                        console.log(response.success)
                        if (response.data) {
                            // TODO
                            // setTimeout(() => {
                            //     $('#modelName').val(response.data)
                            // })
                            self.$router.push('/uploaded')
                        }
                    })
                    this.on('errormultiple', function(files, response) {})
                }
            }
        }
    },
    created() {
        this.initDropzone()
    }
}
</script>

<style scoped>

</style>
