require.config({
    paths: {
        "jquery": "../inspinia/js/jquery-2.1.1",
        "d3": "../d3",
        // "dropzone": "../inspinia/js/plugins/dropzone/dropzone-amd-module.js",
        "tool": "/public/js/tool",
        "tool_button": "/public/js/tool_button",
        "i-checks":"../inspinia/js/plugins/iCheck/icheck.min"
        // "upload_init": "/public/js/upload_init"
    },

    shim: {

        'i-checks': {

            deps: ['jquery'],

            exports: 'i-checks'
        }

    }
});


require(['jquery', "tool", "tool_button"],
    function ($, tool, tool_button) {
        var url = window.location.href;
        var ut = url.split("#")[1];
        if (!ut || ut.length == 0) {
            $('#upload').css("display", "block");
        } else if (ut == 'uploaded') {
            tool_button.getTable();
        } else if (ut == 'bar'){
            tool.init();
        }



        // some code here
        // tool.init();
    });


//
