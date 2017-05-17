<template lang='html'>
  <div >
    <p style="text-align:center; color: #e3e3e3;">{{id}}</p>
    <div :id="id"></div>
    <Loading v-if="loading"/>
  </div>
</template>

<script>
import * as d3 from 'd3'
import * as c3 from 'c3'
import ConfigInfo from '@/config/config.js'
import * as getData from '@/service/data.js'

// sort data by date
function sortDate(a, b) {
  return a.date > b.date ? 1 : a.date < b.date ? -1 : 0
}

export default {
  name: 'BarChart',
  props: ['id', 'subChartEnabled', 'nameMap'],
  data () {
    return {
      loading: true,
      dataMap: {}
    }
  },
  created () {
    this.draw()
  },
  methods: {
    draw() {
      var self = this
      // get data from url
      var urlStr = ConfigInfo.url_prefix + 'count_' + this.id
      getData.getResponse(urlStr).then((response) => {
        var res = getData.parseCateData(response.data)
        var dataSet = res.newJsonData

        // generate data for charts
        var newDataFields = []
        var jsonMap = {}
        dataSet.forEach(function(d) {
          var keyList = d3.keys(d)
          keyList.forEach(function(key) {
            var newObj = jsonMap[key]
            if (!newObj) {
              newDataFields.push(key)
              jsonMap[key] = 1
            }
          })
        })

        newDataFields.splice(newDataFields.indexOf('date'), 1)
        dataSet.sort(sortDate)
        // console.warn(dataSet)
        // console.log(newDataFields)
        // newDataFields.sort(sortNumber)

        const colorMap = {}
        newDataFields.forEach(function(d, i) {
          colorMap[d] = ConfigInfo.data_colors[i]
        })

        // set legend 'others', including 'missing' & invalid input
        var names = d3.keys(this.nameMap)
        var sums = []
        dataSet.forEach((d) => {
          d['others'] = 0
          var sum = 0
          d3.keys(d).forEach((value) => {
            if (!names.includes(value) && (value !== 'date')) {
              d.others += d[value]
            }

            if ((value !== 'date') && (value !== 'others')) {
              sum += d[value]
            }
          })
          sums.push(sum)

          if (d.others === 0) {
            delete d['others']
          }
        })

        var chart = c3.generate({
          bindto: '#' + this.id,
          padding: {
            left: 80,
            right: 50
          },
          data: {
            colors: colorMap,
            type: 'bar',
            json: dataSet,
            keys: {
              x: 'date',
              value: names
              // value: newDataFields
            },
            names: this.nameMap,
            // order: 'desc',
            labels: {
              format: (v, id, i, j) => {
                var tv = d3.values(dataSet[i])
                tv.forEach((d, index) => {
                  if (typeof(d) !== 'number') {
                    tv.splice(index, 1)
                  }
                })
                if (v === Math.max(...tv)) {
                  return sums[i]
                }
              }
            },
            groups: [names],
            order: 'desc'
          },
          // tooltip: {
          //   grouped: false
          // },
          bar: {
            width: {
              ratio: 0.8
            }
          },
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
          },
          onrendered: function () {
            self.loading = false
          }
        })
        // console.log(chart3.zoom())
        if (this.subChartEnabled) {
          chart.resize({
            height: 600
          })
        }
        sessionStorage.setItem(this.id, JSON.stringify(dataSet))
      })
    }
  }
}
</script>

<style lang='css'>
</style>
