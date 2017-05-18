<template lang="html">
  <div >
    <p style="text-align:center; color: #e3e3e3;">{{id}}</p>
    <div :id="id"></div>
    <Loading v-if="!dataSet"/>
  </div>
</template>

<script>
import * as d3 from 'd3'
import * as c3 from 'c3'
import ConfigInfo from '@/config/config.js'

function sortDate(a, b) {
  return a.date > b.date ? 1 : a.date < b.date ? -1 : 0
}

export default {
  name: 'PsiLineChart',
  props: ['id', 'dataSet', 'subChartEnabled', 'variable'],
  watch: {
    'dataSet' (data) {
      this.draw()
    }
  },
  methods: {
    draw() {
      const dataSet = JSON.parse(JSON.stringify(this.dataSet))
      // create new data fields
      var newDataFields = d3.keys(dataSet[0])
      newDataFields.splice(newDataFields.indexOf('date'), 1)

      // sort dataSet by date in acsending order
      dataSet.sort(sortDate)

      // create new name maps
      // var newDataNames = []
      // var nameMap = {}
      const colorMap = {}
      newDataFields.forEach(function(d, i) {
        colorMap[d] = ConfigInfo.data_colors[i]
      })

      var chart = c3.generate({
        bindto: '#' + this.id,
        size: {},
        padding: {
          left: 80,
          right: 50
        },
        data: {
          colors: colorMap,
          type: 'spline',
          json: dataSet,
          keys: {
            x: 'date',
            // value: ['p50', 'p80', 'p10', 'p20', 'p30', 'p40', 'p60', 'p70', 'p90', 'p95', 'p99']
            value: newDataFields
          }
          // names: nameMap
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
            extent: dataSet.length < 5 ? [dataSet[0].date, dataSet[dataSet.length - 1].date] : [dataSet[dataSet.length - 5].date, dataSet[dataSet.length - 1].date],
            // extent: [dataSet[0].date, dataSet[dataSet.length - 1].date],
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
            tick: {
              format: function(d) {
                // fix the psi y-axis label to '0.01'
                var s = d3.formatSpecifier('f')
                s.precision = d3.precisionFixed(0.01)
                var f = d3.format(s)

                return f(d)
              }
            },
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
          onzoomstart: function(event) {
            console.log('onzoomstart', event)
          },
          onzoomend: function(domain) {
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
    }
  }
}
</script>

<style lang="css">
</style>
