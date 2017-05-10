<template lang='html'>
  <div>
    <p style="text-align:center; color: #e3e3e3;">{{id}}</p>
    <div :id="id"></div>
  </div>
</template>

<script>
// import '../js/test_line.js'
import * as d3 from 'd3'
import * as c3 from 'c3'

function sortNumber (a, b) {
  return a - b
}

function sortDate (a, b) {
  return a.date > b.date ? 1 : a.date < b.date ? -1 : 0
}

export default {
  name: 'lineChart',
  props: ['id', 'dataSet', 'subChartEnabled', 'variable'],
  created () {
    this.$nextTick(() => {
      // create new data fields
      var newDataFields = d3.keys(this.dataSet[0])
      newDataFields.splice(newDataFields.indexOf('date'), 1)
      newDataFields.sort(sortNumber)

      // sort dataSet by date in acsending order
      this.dataSet.sort(sortDate)

      // create new name maps
      var newDataNames = []
      var nameMap = {}
      newDataFields.forEach(function (d, i) {
        newDataNames[i] = d + '% value'
        nameMap[d] = newDataNames[i]
      })

      var chart = c3.generate({
        bindto: '#' + this.id,
        size: {
        },
        padding: {
          left: 80,
          right: 50
        },
        data: {
          type: 'spline',
          json: this.dataSet,
          keys: {
            x: 'date',
            // value: ['p50', 'p80', 'p10', 'p20', 'p30', 'p40', 'p60', 'p70', 'p90', 'p95', 'p99']
            value: newDataFields
          },
          names: nameMap
          // xFormat: '%Y%m%d'
        },
        // tooltip: {
        //   grouped: false
        // },
        grid: {
          x: {
            show: true
          },
          y: {
            show: true
          }
        },
        axis: {
          x: {
            type: 'timeseries',
            extent: this.dataSet.length < 5 ? [this.dataSet[0].date, this.dataSet[this.dataSet.length - 1].date] : [this.dataSet[this.dataSet.length - 5].date, this.dataSet[this.dataSet.length - 1].date],
            // extent: [this.dataSet[0].date, this.dataSet[this.dataSet.length - 1].date],
            tick: {
              format: '%Y-%m-%d'
              // culling: {
              //   max: 5
              // }
            },
            label: {
              text: 'Date',
              position: 'outer-right'
            }
          },
          y: {
            label: {
              // text: this.variable,
              position: 'outer-top'
            }
          }
        },
        point: {
          r: 3
        },
        zoom: {
          enabled: this.subChartEnabled,
          onzoomstart: function (event) {
            console.log('onzoomstart', event)
          },
          onzoomend: function (domain) {
            console.log('onzoomend', domain)
          }
        },
        subchart: {
          show: this.subChartEnabled
        },
        legend: {
          hide: (!this.subChartEnabled)
        }
      })
      // console.log(chart3.zoom())
      if (this.subChartEnabled) {
        chart.resize({
          height: 600
        })
      }
    })
  }
}
</script>

<style lang='css'>
</style>
