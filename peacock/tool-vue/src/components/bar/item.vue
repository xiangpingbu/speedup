<template lang="html">
  <div v-if="item">
      <!-- <ItemChart />
      <ItemTable /> -->
      <div :id="`${item.valName}_name`" class="item-top">
          <input type="checkbox" :name="item.valName" class="apply-checks" value="">
          <span style="margin-left: 10px">{{item.valName + "-------"}}</span>
          <span class="iv" :id="item.valName">{{item.iv}}</span>
      </div>
      <div class="svg-content" :id="`svg_${item.num}`">
          <svg class="axis" :id="`my_svg_${item.num}`" style="width: 500px;height: 500px;">
          </svg>
          <div class="sheet">
              <table class="table table-bordered">
                  <thead style="background-color: #f5f5f5;">
                      <tr>
                          <td style="border-bottom: 0;" v-for="(i, head) in item.table_head">{{head}}</td>
                      </tr>
                  </thead>
                  <tbody :id="`tbody_${item.num}`">
                  </tbody>
              </table>
          </div>
      </div>
      <div class="">
          <button :name="item.valName" :id="`merge_${item.num}`" class="btn btn-primary var-btn" type="button" style="maring-left: 25px" disabled="disabled">合并</button>
          <button :name="item.valName" :id="`divide_${item.num}`" class="btn btn-danger var-btn" type="button">分裂</button>
          <button :name="item.valName" :id="`save_${item.num}`" class="btn btn-warning var-btn" type="button">保存记录</button>
          <button :name="item.valName" :id="`divide_manually_${item.num}`" class="btn btn-success var-btn" type="button">手动分裂</button>
      </div>
  </div>
</template>

<script>
// import ItemChart from './ItemChart'
// import ItemTable from './ItemTable'
import * as d3 from 'd3'

var height = 500,
    width = 500,
    margin = 25;
var xScale, yScale;


var host = 'http://localhost:8091'

var svg;


var padding = {
    left: 25,
    right: 30,
    top: 5,
    bottom: 20
};


function getVarName(obj, headMap) {
    for (var v in obj) {
        if (headMap[v] == undefined) {
            return v;
        }
    }
}

/**
 * 描绘x轴
 * 需要指定画布(svg),variable的下标,variable的相关数据
 */
function renderXAxis(svg, num, data) {
    var axisLength = width - 2 * margin;
    var min = 0,
        max = 0;
    for (var obj of data) {
        if (parseFloat(min) > parseFloat(obj.woe)) {
            min = obj.woe;
        }
        if (parseFloat(max) < parseFloat(obj.woe)) {
            max = obj.woe;
        }
    }
    //设定比例
    xScale = d3.scaleLinear()
        .domain([min * 2, max * 2]) //数值范围
        .range([0, axisLength]); //该范围在屏幕内展示的长度

    //ticks表示我们希望的刻度数,会根据数值调整
    var axis = d3.axisBottom(xScale).ticks(5);

    // svg.select("#x_" + num).remove();
    svg.append("g")
        .attr("class", "x-axis")
        .attr("id", "x_" + num)
        .attr("transform", function() {
            return "translate(" + margin + "," + 475 + ")";
        }).call(axis);


    d3.selectAll("g.x-axis g.tick")
        .attr("stroke", "#777")
        .attr("stroke-dasharray", "2,2") //设置虚线
        .append("line")
        .attr("x1", 0)
        .attr("y1", 0)
        .attr("x2", 0)
        .attr("y2", -(height - 2 * margin));
}

/**
 * 描绘y轴
 */
function renderYAxis(svg, num, data) {
    var axisLength = width - 2 * margin;
    //设定比例
    yScale = d3.scaleBand()
        .range([0, axisLength]).padding(0.1);
    yScale.domain(data.map(function(d) {
        return d.bin_num;
    }));

    var axis = d3.axisLeft(yScale);

    svg.append("g")
        .attr("class", "y-axis")
        .attr("id", "y_" + num)
        .attr("transform", function() {
            return "translate(" + margin + "," + margin + ")";
        }).call(axis);


    d3.selectAll("g.x-axis")
        //            .attr("stroke", "#777")
        //            .attr("stroke-dasharray", "2,2") //设置虚线
        .append("line")
        .attr("x1", 0)
        .attr("y1", 0)
        .attr("x2", axisLength)
        .attr("y2", 0);
}


function renderTable(data, num, headMap) {
    var tbody = d3.select("#tbody_" + num);
    tbody.selectAll("tr").remove();
    var height;
    if (data.length == 1) {
        height = yScale(data[0].bin_num);
    } else {
        height = yScale(data[1].bin_num) - yScale(data[0].bin_num);
    }
    var index = 0;
    for (var obj of data) {
        //设置每一行的高度
        var tr = tbody.append("tr").attr("height", height);
        if (headMap == undefined) {
            headMap = obj;
        }
        //根据结果添加列,并设置宽度
        for (var key in headMap) {
            var td = tr.append("td");

            if (key == 'name') {
                key = getVarName(obj, headMap)
            }

            if (index == 0 || index == 1 || index == obj.length - 2) {
                if (key == 'min_boundary') {
                    td = td.attr("name", key);
                }
            } else if (index == data.length - 2) {
                if (key == 'max_boundary') {
                    td = td.attr("name", key);
                }
            }
            if (key == 'woe') {
                td = td.attr("name", key);
            }

            td.style("text-align", "center")
                .style("vertical-align", "middle").text(obj[key]);

        }
        index++;
    }
}


/**
 *
 * @param svg 画布
 * @param data 单个variable的数据
 * @param num  variable的下标
 * @param isSingle 如果是初始化所有数据,
 */
function renderBars(svg, data, num, isSingle) {
    var bar;
    if (isSingle == false) {
        bar = svg.selectAll(".MyRect");
        svg.selectAll(".MyRect").data(data)
            .enter()
            .append("rect")
            .attr("class", "MyRect")
            .attr("fill", "#000000") //设定bar的颜色
            .attr("id", function(d, i) { //绑定id
                return "index_" + num + "_" + i;
            })
            .attr("transform", "translate(" + padding.left + "," + 40 + ")") //设置偏移位置
            .attr("x", function(d) {
                return xScale(Math.min(0, d.woe)); //根据woe值设置x轴的位置
            })
            .attr("y", function(d) {
                //                console.log(yScale(d.bin_num));
                return yScale(d.bin_num); //根据bin_num的值设定y轴的值
            })
            .attr("width", function(d) {
                var width = Math.abs(xScale(d.woe) - xScale(0));
                if (parseInt(width) === 0) {
                    width = 10;
                }
                return width; //根据woe设置宽度
            })
            .attr("height", function(d) {
                return 230 / data.length; //设置高度
            });
    } else {
        svg = d3.select("#my_svg_" + num);
        renderXAxis(svg, num, data);
        renderYAxis(svg, num, data);
        svg.selectAll(".MyRect").remove();
        svg.selectAll(".MyRect").data(data)
            .enter()
            .append("rect")
            .attr("class", "MyRect")
            .attr("fill", "#000000") //设定bar的颜色
            .attr("id", function(d, i) { //绑定id
                return "index_" + num + "_" + i;
            })
            .attr("transform", "translate(" + padding.left + "," + 40 + ")") //设置偏移位置
            .attr("x", function(d) {
                return xScale(Math.min(0, d.woe)); //根据woe值设置x轴的位置
            })
            .attr("y", function(d) {
                //                console.log(yScale(d.bin_num));
                return yScale(d.bin_num); //根据bin_num的值设定y轴的值
            })
            .attr("width", function(d) {
                var width = Math.abs(xScale(d.woe) - xScale(0));
                if (parseInt(width) === 0) {
                    width = 10;
                }
                return width; //根据woe设置宽度
            })
            .attr("height", function(d) {
                return 230 / data.length; //设置高度
            });
    }
}


export default {
    mounted() {
        const {
            valName,
            iv,
            num,
            table_head,
            varData
        } = this.item;

        $(".apply-checks").iCheck({
            checkboxClass: 'icheckbox_square-green variable_apply'
        });

        this.$nextTick(() => {
            // 画出x轴
            const svg = d3.select("#my_svg_" + num);
            renderXAxis(svg, num, varData);
            // 画出y轴
            renderYAxis(svg, num, varData);
            renderBars(svg, varData, num, false);
            renderTable(varData, num, table_head);
        })

    },
    props: ['item'],
    components: {
        // ItemChart,
        // ItemTable
    }
}
</script>

<style lang="css" scoped>
.item-top {
    margin:20px 0px 5px;
    padding: 5px 10px 0;
    border-top: 1px solid #e7eaec;
}
</style>
