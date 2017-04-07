require.config({
    paths: {
        "jquery": "inspinia/js/jquery-2.1.1",
        "d3": "d3",
        "dropzone": "inspinia/js/plugins/dropzone/dropzone"
    }
});


require(['jquery', 'd3', 'dropzone'], function ($, d3, dropzone){
    // some code here
    alert($("#ii").val())
});

require(['../math'], function (math){
    alert(math.add(1,1));
});