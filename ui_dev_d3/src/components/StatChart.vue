<template lang='html'>
  <div >
    <p style="text-align:center; color: #e3e3e3;">{{id}}</p>
    <div :id="id"></div>
  </div>
</template>

<script>
import * as d3 from 'd3'
import * as c3 from 'c3'

// sort data by date
function sortDate (a, b) {
  return a.date > b.date ? 1 : a.date < b.date ? -1 : 0
}

export default {
  name: 'barChart',
  props: ['id', 'dataSet', 'subChartEnabled', 'variable', 'nameMap'],
  created () {
    this.$nextTick(() => {
      // generate data for charts
      // var newDataFields = []
      // var jsonMap = {}
      // this.dataSet.forEach(function (d) {
      //   var keyList = d3.keys(d)
      //   keyList.forEach(function (key) {
      //     var newObj = jsonMap[key]
      //     if (!newObj) {
      //       newDataFields.push(key)
      //       jsonMap[key] = 1
      //     }
      //   })
      // })
      // newDataFields.splice(newDataFields.indexOf('date'), 1)
      this.dataSet.sort(sortDate)
      var newDataFields = d3.keys(this.dataSet[0])
      newDataFields.splice(newDataFields.indexOf('date'), 1)

      var keyField = 'date'
      // var valueField = 'hits'
      var jsonData = []
      var newMap = {}
      this.dataSet.forEach((d) => {
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

      // set legend 'others', including 'missing' & invalid input
      // var names = d3.keys(this.nameMap)
      // var sums = []
      // this.dataSet.forEach((d) => {
      //   d['hits'] = 0
      //   var sum = 0
      //   d3.keys(d).forEach((value) => {
      //     if (!names.includes(value) && (value !== 'date')) {
      //       d.others += d[value]
      //     }
      //
      //     if ((value !== 'date') && (value !== 'others')) {
      //       sum += d[value]
      //     }
      //   })
      //   sums.push(sum)
      //
      //   if (d.others === 0) {
      //     delete d['hits']
      //   }
      // })

      var chart = c3.generate({
        bindto: '#' + this.id,
        padding: {
          left: 80,
          right: 50
        },
        data: {
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
            extent: this.dataSet.length < 5 ? [this.dataSet[0].date, this.dataSet[this.dataSet.length - 1].date] : [this.dataSet[this.dataSet.length - 5].date, this.dataSet[this.dataSet.length - 1].date],
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
    })
  }
}
</script>

<style lang='css'>
  @import '../css/c3.css';
</style>
