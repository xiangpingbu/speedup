/**
 * Created by lifeng on 2017/4/27.
 */
define(['jquery', 'd3', 'i-checks', 'select2'], function ($, d3) {
    /**
     *
     * @param isVariableSelect 是否进入variableSelect环节
     */
    function variableSelect(isVariableSelect) {
        var url;
        var data = {};
        //如果进入isVariableSelect环节,将从该环节的页面获取数据
        //否则将从getBar环节获取数据
        if (isVariableSelect) {
            url = host +"/tool/variable_select_manual";
            $(".selected-body.checked").each(function () {
                selected_variable.push($(this).find(".selected-body-checks").attr("name"))
            });

            $(".backup-body.checked").each(function () {
                selected_variable.push($(this).find(".backup-body-checks").attr("name"))
            });
        } else {
            $(".variable_apply.checked").each(function () {
                var selected_variable = [];
                selected_variable.push($(this).find(".apply-checks").attr("name"));
                url = host +"/tool/variable_select";
            });

        }


        $.ajax({
            url: host + "/tool/variable_select",
            type: 'post',
            data: {
                "var_list": selected_variable.join(","),
                "target": localStorage.getItem("target")
            },
            async: true,
            success: function (result) {
                // adjustTable(result, id, initList, name);

                $("#variableSelect").html("");
                // d3.select("#variableSelect").append()

                var selectArea = d3.select("#variableSelect");
                var head = selectArea.append("div").attr("class", "row wrapper border-bottom white-bg page-heading")
                var content = head.append("div").attr("class", "col-lg-10");
                content.append("h1").text("variable Select");

                var ol = content.append("ol").attr("class", "breadcrumb");
                ol.append("li").append("span").append("a").attr("id", "execute").text("保存数据");
                // var ol = content.append("ol").attr("class", "breadcrumb");
                // ol.append("li").append("span").text(model_name);
                // ol.append("li").append("strong").text(branch);
                // ol.append("li").append("span").append("a").attr("id", "saveAll").text("保存所有");
                // ol.append("li").append("span").append("a").attr("id", "loadAll").text("读取所有");

                var div = selectArea.append("div").attr("class", "ibox-content");
                div.append("div").text("模型信息");
                var table = div.append("table").attr("class", "table table-striped");
                var tbody = table.append("tbody");
                var data = result.data;

                var i = 0;
                for (let key in data["model_analysis"]) {
                    var tr;
                    if (i % 2 == 0) {
                        tr = tbody.append("tr");
                    }
                    tr.append("th").text(key);
                    tr.append("td").text(data["model_analysis"][key]);
                    i++;
                }
                // var tr = tbody.append("tr");
                // tr.append("th").text("model");
                // tr.append("td").text("loGit");
                // tr.append("th").text("Pseudo R-squared");
                // tr.append("td").text("0.041");
                //
                // tr = tbody.append("tr");
                // tr.append("th").text("Depandent Variable");
                // tr.append("td").text("data[model_analysis].target");
                // tr.append("th").text("AIC");
                // tr.append("td").text("data[model_analysis].aic");
                // tr = tbody.append("tr");
                // tr.append("th").text("nobs");
                // tr.append("td").text("data[model_analysis].nobs");
                // tr.append("th").text("BIC");
                // tr.append("td").text("data[model_analysis].bic");
                // tr = tbody.append("tr");
                // tr.append("th").text("df_resid");
                // tr.append("td").text("data[model_analysis].df_resid");
                // tr.append("th").text("likelyhood");
                // tr.append("td").text("data[model_analysis].likelyhood");
                // tr = tbody.append("tr");
                // tr.append("th").text("llnull");
                // tr.append("td").text("data[model_analysis].llnull");
                // tr.append("th").text("llr");
                // tr.append("td").text("data[model_analysis].llr");
                //必选项
                div.append("div").text("必选项");
                table = div.append("table").attr("class", "table table-striped table-bordered table-hover dataTables-example dataTable");
                tbody = table.append("tbody").attr("id", "tbody-selected");
                var thead = table.append("thead");
                tr = thead.append("tr");
                var select = tr.append("td");
                //头部的checkbox
                var ss = select.append("div");
                ss.append("input")
                    .attr("type", "checkbox")
                    .attr("class", "selected-head-checks")
                    .attr("name", "input[]");
                tr.append("td").text("name");
                tr.append("td").text("params");
                tr.append("td").text("pvalues");
                tr.append("td").text("bse");
                tr.append("td").text("tvalues");
                tr.append("td").text("conf_int0");
                tr.append("td").text("conf_int1");
                for (let obj in data["selected_var"]){
                    tr = tbody.append("tr");
                    select = tr.append("td");
                    //body的checkbox
                    var variable = data["selected_var"][obj][0];
                    var  name = variable.substring(0,variable.indexOf("_woe"));
                    select.append("div").append("input")
                        .attr("type", "checkbox")
                        .attr("class", "selected-body-checks")
                        .attr("name", name);
                    tr.append("td").text(name);
                    var columns = data["selected_var"][obj][1].split(",");
                    for (let i in columns) {
                        tr.append("td").text(columns[i]);
                    }
                }


                div.append("div").text("可选项");
                //备选的table
                table = div.append("table").attr("class", "table table-striped table-bordered table-hover dataTables-example dataTable");
                tbody = table.append("tbody");
                thead = table.append("thead");

                //3th head
                tr = thead.append("tr");
                select = tr.append("td");
                ss = select.append("div");
                //头部的checkbox
                ss.append("input")
                    .attr("type", "checkbox")
                    .attr("class", "backup-head-checks")
                    .attr("name", "input[]");
                tr.append("td").text("name");
                tr.append("td").text("ks");
                tr.append("td").text("pvalues");
                // tr = tbody.append("tr");
                // select = tr.append("td");
                // //body的checkbox
                // select.append("div").append("input")
                //     .attr("type", "checkbox")
                //     .attr("class", "backup-body-checks")
                //     .attr("name", "input[]");
                // tr.append("td").text("11");
                // tr.append("td").text("11");
                // tr.append("td").text("11");
                // tr.append("td").text("11");
                // tr.append("td").text("11");
                for (let obj in data["marginal_var"]){
                    tr = tbody.append("tr");
                    select = tr.append("td");
                    //body的checkbox
                     variable = data["marginal_var"][obj][0];
                     name = variable.substring(0,variable.indexOf("_woe"));
                    select.append("div").append("input")
                        .attr("type", "checkbox")
                        .attr("class", "backup-body-checks")
                        .attr("name", name);
                    tr.append("td").text(name);
                    var columns = data["marginal_var"][obj][1].split(",");
                    for (let i in columns) {
                        tr.append("td").text(columns[i]);
                    }
                }


                $('.selected-body-checks').iCheck({
                    checkboxClass: 'icheckbox_square-green selected-body'
                });
                $('.selected-head-checks').iCheck({
                    checkboxClass: 'icheckbox_square-green'
                }).on('ifChecked', function () {
                    $(".selected-body").iCheck('check');
                }).on('ifUnchecked', function () {
                    $('.selected-body').iCheck('uncheck');
                });

                $('.backup-body-checks').iCheck({
                    checkboxClass: 'icheckbox_square-green backup-body'
                });

                $('.backup-head-checks').iCheck({
                    checkboxClass: 'icheckbox_square-green'
                }).on('ifChecked', function () {
                    $(".backup-body").iCheck('check')
                }).on('ifUnchecked', function () {
                    $('.backup-body').iCheck('uncheck');
                });


                $("#execute").bind("click", function () {
                    variableSelect(true);
                })

            }
        });


    }

    return {variableSelect: variableSelect}

});