maxIndex = 2;
minIndex = 1;
woeIndex = 10;
binNumIndex = 0;
cateIndex = 11;

categoricalIndex = 1;


function outputDateMap() {
    $("#output").click(function () {
            var row = $("#rowNum").val();
            var data = {};
            for (var i = 0; i < row; i++) {
                var name = $('#merge_' + i).attr("name");
                var innerList = [];
                data[name] = innerList;

                var childTrs = $('#tbody_' + i).children("tr");
                for (var innerRow = 0; innerRow < childTrs.length; innerRow++) {
                    var innerDate = {};
                    innerList.push(innerDate);
                    var category_t = $(childTrs.get(innerRow)).children("td").get(cateIndex).innerHTML;
                    if (category_t.indexOf("F") >= 0) {
                        var max = $(childTrs.get(innerRow)).children("td").get(maxIndex).innerHTML;
                        var min = $(childTrs.get(innerRow)).children("td").get(minIndex).innerHTML;
                        innerDate["max"] = max;
                        innerDate["min"] = min;
                    } else {
                        var ca = $(childTrs.get(innerRow)).children("td").get(categoricalIndex).innerHTML;

                        innerDate[name] = ca.split('|');
                    }
                    var woe;
                    try {
                        woe = $(childTrs.get(innerRow)).children("td").get(woeIndex).innerHTML;
                    } catch (e) {
                        woe = $(childTrs.get(innerRow)).children("td").get(woeIndex - 1).innerHTML;
                    }
                    var binNum = $(childTrs.get(innerRow)).children("td").get(binNumIndex).innerHTML;
                    innerDate["woe"] = woe;
                    innerDate["binNum"] = binNum;
                    innerDate["category_t"] = category_t;
                }
            }
            console.log(data);
            $.ajax({
                url: "http://localhost:8091/tool/apply",
                type: 'post',
                data: {
                    "data": JSON.stringify(data)
                },
                async: true,
                success: function (result) {

                }
            });
        }
    )
}

function changeTd() {
    $("td[name='woe']").click(function () {
        if (!$(this).is('.input')) {
            $(this).addClass("input")
                .html('<input type="text" style="width:60px" value="' + $(this).text() + '"/>')
                .find('input').focus().blur(function () {
                $(this).parent().removeClass('input').html($(this).val() || 0);
            });
        }
    });

    $("td[name='min_bound']").click(function () {
        if (!$(this).is('.input')) {
            $(this).addClass("input")
                .html('<input type="text" style="width:80px" value="' + $(this).text() + '"/>')
                .find('input').focus().blur(function () {
                $(this).parent().removeClass('input').html($(this).val() || 0);
            });
        }
    });

    $("td[name='max_bound']").click(function () {
        if (!$(this).is('.input')) {
            $(this).addClass("input")
                .html('<input type="text" style="width:80px" value="' + $(this).text() + '"/>')
                .find('input').focus().blur(function () {
                $(this).parent().removeClass('input').html($(this).val() || 0);
            });
        }
    });
}

$("#prev").click(function () {
    $('#data').html("");
    // $('#data').html($('#preDefine').html());
    // $('#preDefine').load("/html/model/xyb.html");
    $('#preDefine').css('display','block');


});

$("#init").click(function () {
    $('#preDefine').html("");
    init();
});


