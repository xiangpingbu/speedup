import axios from 'axios'
import * as d3 from 'd3'

// axios.defaults.baseURL = 'http://localhost:3000/'
// axios.defaults.baseURL = '/api/'

export function getResponse (url) {
  return axios.get(url)
}

export function getNumData (url) {
  // const url = `/${_url}`
  return new Promise((resolve, reject) => {
    d3.json(url, (json) => {
      // console.warn(json)
      var tt = json.data.hits.hits
      var dt = []
      tt.forEach(function (d) {
        dt.push(d._source)
      })
      var variable = (tt && tt[0]) ? tt[0]._source.variable : ''
      var valFields = ['value']
      var dataIndexField = 'percent'
      var keyField = 'date'
      var newMap = {}
      var newJsonData = []
      // var newDataFields = []
      // var nameMap = {}
      dt.forEach(function (d) {
        var date = d[keyField]
        var newObj = newMap[date]
        if (!newObj) {
          newObj = {
            date: date
          }
          newMap[date] = newObj
        }
      //
        var dataIndex = d[dataIndexField]
        valFields.forEach(function (vField) {
          newObj[dataIndex] = d[vField]
        })
        newJsonData = d3.values(newMap)
      })
        // console.log(newJsonData)
        // return newJsonData
      resolve({newJsonData, variable})
    })
  })
}

export function parseNumData (json) {
  var tt = json.data.hits.hits
  var dt = []
  tt.forEach(function (d) {
    dt.push(d._source)
  })
  var variable = (tt && tt[0]) ? tt[0]._source.variable : ''
  var valFields = ['value']
  var dataIndexField = 'percent'
  var keyField = 'date'
  var newMap = {}
  var newJsonData = []
  // var newDataFields = []
  // var nameMap = {}
  dt.forEach(function (d) {
    var date = d[keyField]
    var newObj = newMap[date]
    if (!newObj) {
      newObj = {
        date: date
      }
      newMap[date] = newObj
    }
  //
    var dataIndex = d[dataIndexField]
    valFields.forEach(function (vField) {
      newObj[dataIndex] = d[vField]
    })
    newJsonData = d3.values(newMap)
  })

  return {newJsonData, variable}
}

export function getCateData (url) {
  // const url = `/${_url}`
  return new Promise((resolve, reject) => {
    d3.json(url, (json) => {
      // console.warn(err)
      // console.warn(json)
      var tt = json.data.hits.hits
      var dt = []
      tt.forEach(function (d) {
        dt.push(d._source)
      })
      var category = (tt && tt[0]) ? tt[0]._source.category : ''
      var valFields = ['count']
      var dataIndexField = 'value'
      var keyField = 'date'
      var newMap = {}
      var newJsonData = []
      // var newDataFields = []
      // var nameMap = {}
      dt.forEach(function (d) {
        var date = d[keyField]
        var newObj = newMap[date]
        if (!newObj) {
          newObj = {
            date: date
          }
          newMap[date] = newObj
        }
      //
        var dataIndex = d[dataIndexField]
        valFields.forEach(function (vField) {
          newObj[dataIndex] = d[vField]
        })
        newJsonData = d3.values(newMap)
      })
        // console.log(newJsonData)
        // return newJsonData
      resolve({newJsonData, category})
    })
  })
}

export function parseCateData (json) {
  var tt = json.data.hits.hits
  var dt = []
  tt.forEach(function (d) {
    dt.push(d._source)
  })
  var category = (tt && tt[0]) ? tt[0]._source.category : ''
  var valFields = ['count']
  var dataIndexField = 'value'
  var keyField = 'date'
  var newMap = {}
  var newJsonData = []
  // var newDataFields = []
  // var nameMap = {}
  dt.forEach(function (d) {
    var date = d[keyField]
    var newObj = newMap[date]
    if (!newObj) {
      newObj = {
        date: date
      }
      newMap[date] = newObj
    }
  //
    var dataIndex = d[dataIndexField]
    valFields.forEach(function (vField) {
      newObj[dataIndex] = d[vField]
    })
    newJsonData = d3.values(newMap)
  })
  return {newJsonData, category}
}

export function parsePsiData (json) {
  var tt = json.data.hits.hits
  var newJsonData = []
  tt.forEach(function (d) {
    newJsonData.push(d._source)
  })
  return {newJsonData}
}
