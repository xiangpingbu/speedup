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

//var host = "http://localhost:8091";
var host = "http://101.71.245.166:8091";
var originUt;


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
            if (!ut || ut.length === 0) {
                display("upload");
                // $('#upload').css("display", "block");
            } else if (ut === 'uploaded') {
                display("dataframe");
                tool_button.getTable();
            } else if (ut === 'bar') {
                display("analyze");
                if (originUt != 'select') {
                    $("#analyze").html("");
                    tool.initHead();
                    tool_button.commitBranch();
                }
            } else if (ut === 'select') {

                $.ajax({
                    url: host + "/tool/if_applyed",
                    type: 'post',
                    async: false,
                    success: function (result) {
                        if (!result.success) {
                            alert("you have not applyed yet");
                            //因为没有没有执行过apply,所以不能跳转到#select页面,
                            //因此url人为地设置在bar页面
                            window.location.href =url.split("#")[0]+"#bar";
                            //url更改后会出发onhashchange方法,所以要记录原来的url
                        }
                        else{
                            display("variableSelect");
                            tool_variable_select.preInit();
                        }
                        //通过原先的url判断是否要刷新页面
                        originUt = ut;
                    }
                });

            }
        }
    });

/**
 * 根据名字展示相应的页面
 * @param name 页面对应的div
 */
function display(name) {
    for (var b in block){
            block[b].css("display","none");
    }
    block[name].css("display","block");
}


//
