<template>
<div>
  <topbar :numIds="id" :countIds="countId"></topbar>
  <div class="pure-g">

    <!-- <div class="pure-u-1-2">
      <div v-for="mid in id" class="chart-card" @click="viewChart(mid, 'line')">
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <lineChart v-if="!loading" :id="mid" :dataSet="dataMap[mid]" :variable="varMap[mid]"></lineChart>
      </div>
    </div> -->
    <div class="pure-u-1-1">
      <div class="chart-card" @click="viewChart(id[0], 'line')" name='score'>
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <lineChart v-if="!loading" :id="id[0]" :dataSet="dataMap[id[0]]" :variable="varMap[id[0]]"></lineChart>
      </div>
    </div>

    <div class="pure-u-1-2">
      <!-- <img v-show="loading" src="../assets/1392662591224_1140x0.gif"> -->
      <div class="chart-card" @click="viewChart(id[1], 'line')" name='age'>
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <lineChart v-if="!loading" :id="id[1]" :dataSet="dataMap[id[1]]" :variable="varMap[id[1]]"></lineChart>
      </div>
    </div>
    <div class="pure-u-1-2">
      <div class="chart-card" @click="viewChart(id[2], 'line')" name='credit_query_times'>
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <lineChart v-if="!loading" :id="id[2]" :dataSet="dataMap.credit_query_times" :variable="varMap.credit_query_times"></lineChart>
      </div>
    </div>
  </div>
  <div class="pure-g">
    <div class="pure-u-1-3">
      <div class="chart-card" @click="viewChart(id[3], 'line')">
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <lineChart v-if="!loading" :id="id[3]" :dataSet="dataMap.credit_limit" :variable="varMap.credit_limit"></lineChart>
      </div>
    </div>
    <div class="pure-u-1-3">
      <div class="chart-card" @click="viewChart(countId[0], 'bar')">
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <barChart v-if="!loading" :id="countId[0]" :dataSet="dataMap.personal_live_join" :variable="varMap.personal_live_join" :nameMap="this.nameMap[countId[0]]"></barChart>
      </div>
    </div>
  </div>
</div>
</template>

<script>
import topbar from '@/components/TopBar.vue'
import lineChart from '@/components/lineChart.vue'
import barChart from '@/components/barChart.vue'
// import * as d3 from 'd3'
// import axios from 'axios'
import * as getData from '@/service/data.js'
import {mapMutations} from 'vuex'
export default {
  name: 'DashBoard',
  created () {
    // console.log(66666)
    var PromiseList = []
    var CatePromiseList = []
    // this.numUrls.forEach(function (d) {
    //   PromiseList.push(getData.getNumData(d))
    // })
    // Promise.all(PromiseList).then((receivedData) => {
    //   receivedData.forEach((d, i) => {
    //     // console.log(d.newJsonData)
    //     this.dataMap[this.id[i]] = d.newJsonData
    //     this.varMap[this.id[i]] = d.variable
    //   })
    // })

    // get & parse numerical data
    this.numUrls.forEach(function (d) {
      PromiseList.push(getData.getResponse(d))
    })
    Promise.all(PromiseList).then((response) => {
      response.forEach((d, i) => {
        var res = getData.parseNumData(d.data)
        this.dataMap[this.id[i]] = res.newJsonData
        this.varMap[this.id[i]] = res.variable
      })
    })

    // get & parse categorocal data
    this.CateUrls.forEach(function (d) {
      CatePromiseList.push(getData.getResponse(d))
    })
    Promise.all(CatePromiseList).then((response) => {
      response.forEach((d, i) => {
        var res = getData.parseCateData(d.data)
        this.dataMap[this.countId[i]] = res.newJsonData
        this.varMap[this.countId[i]] = res.category
      })
    })

    // getData.getCateData(url4).then((receivedData) => {
    //   this.dataMap[this.countId[0]] = receivedData.newJsonData
    //   this.varMap[this.countId[0]] = receivedData.category
    //   // console.log(this.dataMap[this.countId[0]])
    // })

    localStorage.setItem('numIds', JSON.stringify(this.id))
    localStorage.setItem('countIds', JSON.stringify(this.countId))

    // set personal_live_join nameMap
    this.nameMap[this.countId[0]] = {
      '1,': '父母',
      '2,': '配偶及子女',
      '1,2,': '父母、配偶及子女',
      '4,': '其他',
      '3,': '朋友',
      '2,4,': '配偶及子女、其他',
      '1,4,': '父母、其他',
      '1,2,4,': '父母、配偶子女、其他',
      'others': '不属于以上情况'
    }
    // console.log(this.nameMap.personal_live_join)

    setTimeout(() => {
      this.loading = false
    }, 1000)
  },
  data () {
    return {
      numUrls: [
        '/monitor/model_xyb_monitor_percentile_score',
        '/monitor/model_xyb_monitor_percentile_age',
        '/monitor/model_xyb_monitor_percentile_credit_query_times',
        '/monitor/model_xyb_monitor_percentile_credit_limit'],
      CateUrls: ['/monitor/model_xyb_monitor_count_personal_live_join'],
      id: ['score', 'age', 'credit_query_times', 'credit_limit'],
      type: '',
      countId: ['personal_live_join'],
      dataMap: [],
      nameMap: [],
      subChartEnabled: false,
      varMap: [],
      loading: true
    }
  },
  methods: {
    ...mapMutations(['saveData', 'saveId']),
    viewChart: function (id, type) {
      // store data
      // this.saveData({
      //   id: id,
      //   dataSet: this.dataMap[id],
      //   variable: this.varMap[id],
      //   type: type
      // })
      // console.log(this.$store.state.charts)

      sessionStorage.setItem(id, JSON.stringify(this.dataMap[id]))
      sessionStorage.setItem(id + 'Var', this.varMap[id])
      sessionStorage.setItem(id + 'type', type)
      sessionStorage.setItem(id + 'nameMap', JSON.stringify(this.nameMap[id]))

      // localStorage.clear()
      this.$router.push({
        name: 'Page',
        params: {
          id
        }
      })
      // console.log(this.dataSet)
    }
  },
  components: {
    topbar,
    lineChart,
    barChart
  }
}
</script>

<style lang="css">
</style>
