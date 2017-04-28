/**
 * Created by lifeng on 2017/4/27.
 */
define(['jquery', 'd3', 'i-checks', 'select2'], function ($, d3) {
    function variableSelect() {
        $("#variableSelect").html("");
        // d3.select("#variableSelect").append()

        var selectArea = d3.select("#variableSelect");
        var head = selectArea.append("div").attr("class", "row wrapper border-bottom white-bg page-heading")
        var content = head.append("div").attr("class", "col-lg-10");
        content.append("h1").text("variable Select");

        var ol = content.append("ol").attr("class", "breadcrumb");
        ol.append("li").append("span").append("a").attr("id", "saveAll").text("保存数据");
        // var ol = content.append("ol").attr("class", "breadcrumb");
        // ol.append("li").append("span").text(model_name);
        // ol.append("li").append("strong").text(branch);
        // ol.append("li").append("span").append("a").attr("id", "saveAll").text("保存所有");
        // ol.append("li").append("span").append("a").attr("id", "loadAll").text("读取所有");

        var div = selectArea.append("div").attr("class", "ibox-content");
        div.append("div").text("模型信息");
        var table = div.append("table").attr("class", "table table-striped");
        var tbody = table.append("tbody");
        var tr = tbody.append("tr");
        tr.append("th").text("model");
        tr.append("td").text("loGit");
        tr.append("th").text("Pseudo R-squared");
        tr.append("td").text("0.041");

        tr = tbody.append("tr");
        tr.append("th").text("model");
        tr.append("td").text("loGit");
        tr.append("th").text("Pseudo R-squared");
        tr.append("td").text("0.041");
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
        tr.append("td").text("Coef");
        tr.append("td").text("Std.Err");
        tr.append("td").text("z");
        tr.append("td").text("P>|z|");
        tr = tbody.append("tr");
        select = tr.append("td");
        //body的checkbox
        select.append("div").append("input")
            .attr("type", "checkbox")
            .attr("class", "selected-body-checks")
            .attr("name", "input[]");
        tr.append("td").text("11");
        tr.append("td").text("11");
        tr.append("td").text("11");
        tr.append("td").text("11");
        tr.append("td").text("11");

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
        tr.append("td").text("Coef");
        tr.append("td").text("Std.Err");
        tr.append("td").text("z");
        tr.append("td").text("P>|z|");

        tr = tbody.append("tr");
        select = tr.append("td");
        //body的checkbox
        select.append("div").append("input")
            .attr("type", "checkbox")
            .attr("class", "backup-body-checks")
            .attr("name", "input[]");
        tr.append("td").text("11");
        tr.append("td").text("11");
        tr.append("td").text("11");
        tr.append("td").text("11");
        tr.append("td").text("11");

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
    }

    return {variableSelect: variableSelect}

});