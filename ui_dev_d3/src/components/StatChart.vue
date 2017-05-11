<template lang='html'>
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

// sort data by date
function sortDate(a, b) {
  return a.date > b.date ? 1 : a.date < b.date ? -1 : 0
}

export default {
  name: 'StatChart',
  props: ['id', 'dataSet', 'subChartEnabled', 'variable', 'nameMap'],
  watch: {
    'dataSet' (data) {
      this.draw()
    }
  },
  methods: {
    draw() {
      if (!this.dataSet) return
      const dataSet = JSON.parse(JSON.stringify(this.dataSet))
      // newDataFields.splice(newDataFields.indexOf('date'), 1)
      dataSet.sort(sortDate)
      var newDataFields = d3.keys(dataSet[0])
      newDataFields.splice(newDataFields.indexOf('date'), 1)
      const colorMap = {}
      newDataFields.forEach(function(d, i) {
        colorMap[d] = ConfigInfo.data_colors[i]
      })

      var keyField = 'date'
      // var valueField = 'hits'
      var jsonData = []
      var newMap = {}
      dataSet.forEach((d) => {
        var date = d[keyField]
        var newObj = newMap[date]
        // input unique date as keys
        if (!newObj) {
          newObj = {
            date: date,
            hits: 0,
            count: 0
          }
          newMap[date] = newObj
        }

        // sum up the counts
        if (d3.keys(newMap).includes(d.date)) {
          newMap[date]['hits'] += d.count
          newMap[date]['count'] += d.count
        }

        // json input to c3 chart
        jsonData = d3.values(newMap)
      })
      // console.log(this.dataSet)

      var chart = c3.generate({
        bindto: '#' + this.id,
        padding: {
          left: 80,
          right: 50
        },
        data: {
          colors: colorMap,
          type: 'spline',
          // json: this.dataSet,
          json: jsonData,
          types: {
            count: 'bar'
          },
          keys: {
            x: 'date',
            value: ['hits', 'count']
            // value: newDataFields
          },
          // names: this.nameMap,
          // order: 'desc',
          // labels: {
          //   format: (v, id, i, j) => {
          //     var tv = d3.values(this.dataSet[i])
          //     tv.forEach((d, index) => {
          //       if (typeof (d) !== 'number') {
          //         tv.splice(index, 1)
          //       }
          //     })
          //     if (v === Math.max(...tv)) {
          //       return sums[i]
          //     }
          //   }
          // },
          // groups: [names],
          order: 'desc'
        },
        // tooltip: {
        //   grouped: false
        // },
        // bar: {
        //   width: {
        //     ratio: 0.8
        //   }
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
            extent: jsonData.length < 5 ? [jsonData[0].date, jsonData[jsonData.length - 1].date] : [jsonData[jsonData.length - 5].date, jsonData[jsonData.length - 1].date],
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
              text: 'count',
              position: 'outer-top'
            }
          }
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
      if (this.subChartEnabled) {
        chart.resize({
          height: 600
        })
      }

      // test group
      // setTimeout(() => {
      //   var tg = []
      //   tg.push(newDataFields)
      //   chart.groups(tg)
      // }, 2000)
    }
  }
}
</script>

<style lang='css'>
</style>
