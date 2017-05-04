<template>
<div>
  <topbar :numIds="id" :countIds="countId"></topbar>
  <div class="pure-g">

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
        <lineChart v-if="!loading" :id="id[2]" :dataSet="dataMap[id[2]]" :variable="varMap[id[2]]"></lineChart>
      </div>
    </div>
  </div>

  <div class="pure-g">
    <div class="pure-u-1-3">
      <div class="chart-card" @click="viewChart(id[3], 'line')">
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <lineChart v-if="!loading" :id="id[3]" :dataSet="dataMap[id[3]]" :variable="varMap[id[3]]"></lineChart>
      </div>
    </div>
    <div class="pure-u-1-3">
      <div class="chart-card" @click="viewChart(id[4], 'line')">
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <lineChart v-if="!loading" :id="id[4]" :dataSet="dataMap[id[4]]" :variable="varMap[id[4]]"></lineChart>
      </div>
    </div>
    <div class="pure-u-1-3">
      <div class="chart-card" @click="viewChart(id[5], 'line')">
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <lineChart v-if="!loading" :id="id[5]" :dataSet="dataMap[id[5]]" :variable="varMap[id[5]]"></lineChart>
      </div>
    </div>
  </div>

  <div class="pure-g">
    <div class="pure-u-1-2" v-for="mid in countId">
      <div class="chart-card" @click="viewChart(mid, 'bar')">
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <barChart v-if="!loading" :id="mid" :dataSet="dataMap[mid]" :variable="varMap[mid]" :nameMap="nameMap[mid]"></barChart>
      </div>
    </div>
  </div>
    <!-- <div class="pure-u-1-2">
      <div class="chart-card" @click="viewChart(countId[1], 'bar')">
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <barChart v-if="!loading" :id="countId[1]" :dataSet="dataMap[countId[1]]" :variable="varMap[countId[1]]" :nameMap="this.nameMap[countId[1]]"></barChart>
      </div>
    </div>
    <div class="pure-u-1-2">
      <div class="chart-card" @click="viewChart(countId[2], 'bar')">
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <barChart v-if="!loading" :id="countId[2]" :dataSet="dataMap[countId[2]]" :variable="varMap[countId[2]]" :nameMap="this.nameMap[countId[2]]"></barChart>
      </div>
    </div> -->

  <!-- <div class="pure-g">
    <div class="pure-u-1-2">
      <div class="chart-card" @click="viewChart(countId[0], 'bar')">
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <barChart v-if="!loading" :id="countId[0]" :dataSet="dataMap.personal_live_join" :variable="varMap.personal_live_join" :nameMap="this.nameMap[countId[0]]"></barChart>
      </div>
    </div>

    <div class="pure-u-1-2">
      <div class="chart-card" @click="viewChart(countId[3], 'bar')">
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <barChart v-if="!loading" :id="countId[3]" :dataSet="dataMap[countId[3]]" :variable="varMap[countId[3]]" :nameMap="this.nameMap[countId[3]]"></barChart>
      </div>
    </div>
  </div> -->
  <div class="pure-g">
    <div class="pure-u-1-3" v-for="mid in psiId">
      <div class="chart-card" @click="viewChart('psi_' + mid, 'psi')">
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <psiLineChart v-if="!loading" :id="'psi_' + mid" :dataSet="dataMap['psi_' + mid]"></psiLineChart>
      </div>
    </div>

    <!-- <div class="pure-u-1-2">
      <div class="chart-card" @click="viewChart(psiId[0], 'psi')">
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <psiLineChart v-if="!loading" :id="psiId[0]" :dataSet="dataMap[psiId[0]]" :variable="varMap[psiId[0]]"></psiLineChart>
      </div>
    </div>
    <div class="pure-u-1-2">
      <div class="chart-card" @click="viewChart(psiId[1], 'psi')">
        <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
        <psiLineChart v-if="!loading" :id="psiId[1]" :dataSet="dataMap[psiId[1]]" :variable="varMap[psiId[1]]"></psiLineChart>
      </div>
    </div> -->
  </div>
</div>
</template>

<script>
import topbar from '@/components/TopBar.vue'
import lineChart from '@/components/lineChart.vue'
import barChart from '@/components/barChart.vue'
import psiLineChart from '@/components/psiChart.vue'
// import * as d3 from 'd3'
// import axios from 'axios'
import ConfigInfo from '@/config/config.js'
import * as getData from '@/service/data.js'
import {mapMutations} from 'vuex'
export default {
  name: 'DashBoard',
  created () {
    var NumPromiseList = []
    var CatePromiseList = []
    var PsiPromiseList = []

    // get numerical urls
    this.id.forEach((d) => {
      var urlStr = ConfigInfo.url_prefix + this.type[0] + '_' + d
      NumPromiseList.push(getData.getResponse(urlStr))
    })
    // get categorocal urls
    this.countId.forEach((d) => {
      var urlStr = ConfigInfo.url_prefix + this.type[1] + '_' + d
      CatePromiseList.push(getData.getResponse(urlStr))
    })
    // get psi urls
    this.psiId.forEach((d) => {
      var urlStr = ConfigInfo.url_prefix + this.type[2] + '_' + d
      PsiPromiseList.push(getData.getResponse(urlStr))
    })

    // get & parse numerical data
    // this.numUrls.forEach(function (d) {
    //   NumPromiseList.push(getData.getResponse(d))
    // })
    Promise.all(NumPromiseList).then((response) => {
      response.forEach((d, i) => {
        var res = getData.parseNumData(d.data)
        this.dataMap[this.id[i]] = res.newJsonData
        this.varMap[this.id[i]] = res.variable
      })
    })

    // get & parse categorocal data
    // this.CateUrls.forEach(function (d) {
    //   CatePromiseList.push(getData.getResponse(d))
    // })
    Promise.all(CatePromiseList).then((response) => {
      response.forEach((d, i) => {
        var res = getData.parseCateData(d.data)
        this.dataMap[this.countId[i]] = res.newJsonData
        this.varMap[this.countId[i]] = res.category
      })
    })

    // get & parse psi data
    // this.PsiUrls.forEach(function (d) {
    //   PsiPromiseList.push(getData.getResponse(d))
    // })
    Promise.all(PsiPromiseList).then((response) => {
      response.forEach((d, i) => {
        var res = getData.parsePsiData(d.data)
        this.dataMap['psi_' + this.psiId[i]] = res.newJsonData
      })
    })

    // store id lists for top bar
    localStorage.setItem('numIds', JSON.stringify(this.id))
    localStorage.setItem('countIds', JSON.stringify(this.countId))

    // this.countId.forEach((d) => {
    //   this.nameMap[d] = ConfigInfo[d]
    // })

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

    this.nameMap[this.countId[1]] = {
      '1': '硕士及以上',
      '2': '本科',
      '3': '专科',
      '4': '其他',
      'others': '不属于以上情况'
    }

    this.nameMap[this.countId[2]] = {
      '1': '男',
      '2': '女',
      'others': '其他'
    }

    this.nameMap[this.countId[3]] = {
      '1': '自有商业按揭房',
      '2': '自有无按揭购房',
      '3': '自有公积金按揭购房',
      '4': '自建房',
      '5': '租房',
      '6': '亲戚住房',
      '7': '宿舍',
      '8': '其他',
      'others': '不属于以上情况'
    }
    // console.log(this.nameMap.personal_live_join)
    // console.log(this.$route.params.model)

    setTimeout(() => {
      this.loading = false
    }, 1000)
  },
  data () {
    return {
      id: ['score', 'age', 'credit_query_times', 'credit_limit', 'personal_year_income', 'credit_utilization'],
      countId: ['personal_live_join', 'personal_education', 'client_gender', 'personal_live_case'],
      psiId: ['score', 'age', 'credit_query_times', 'credit_limit', 'personal_year_income', 'credit_utilization'],
      type: ['percentile', 'count', 'psi'],
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
    barChart,
    psiLineChart
  }
}
</script>

<style lang="css">
</style>
