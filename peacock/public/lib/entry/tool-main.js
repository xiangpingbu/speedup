require.config({
    paths: {
        "jquery": "../inspinia/js/jquery-2.1.1",
        "d3": "../d3",
        // "dropzone": "../inspinia/js/plugins/dropzone/dropzone-amd-module.js",
        "tool": "/public/js/tool",
        "tool_button": "/public/js/tool_button",
        "tool_variable_select": "/public/js/tool_variable_select",
        "i-checks": "../inspinia/js/plugins/iCheck/icheck.min",
        "select2": "https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.3/js/select2.min"
        // "upload_init": "/public/js/upload_init"
    },

    shim: {
        'i-checks': {
            deps: ['jquery'],
            exports: 'i-checks'
        }
    }
});


require(['jquery', "tool", "tool_button","tool_variable_select"],
    function ($, tool, tool_button,tool_variable_select) {
        block = {
            "upload": $("#upload"),
            "analyze": $("#analyze"),
            "dataframe": $("#dataframe"),
            "variableSelect":$("#variableSelect")
        };
        init();
        window.onhashchange = function () {
            // var hashCode = location.hash;
            init();
        };
        function init() {
            $(".spinner").css('display', 'none');
            var url = window.location.href;
            var ut = url.split("#")[1];
            console.log(ut);
            if (!ut || ut.length == 0) {
                display("upload");
                // $('#upload').css("display", "block");
            } else if (ut == 'uploaded') {
                display("dataframe");
                tool_button.getTable();
            } else if (ut == 'bar') {
                display("analyze");
                tool.init();
            } else if (ut == 'select') {
                display("variableSelect");
                tool_variable_select.variableSelect();
            }
        }
    });

function display(name) {
    for (var b in block){
            block[b].css("display","none");
    }
    block[name].css("display","block");
}


//
