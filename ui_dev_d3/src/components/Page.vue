<template lang="html">
  <div>
    <topbar :numIds="numIds" :countIds="countIds"></topbar>
    <div class="chart-card">
      <img class="loading-img" v-show = "loading" src="../assets/loading.gif">
      <lineChart v-if="(!loading)&&type==='line'" :id="id" :dataSet="dataSet" :subChartEnabled="true" :variable="variable"></lineChart>
      <barChart v-if="(!loading)&&type==='bar'" :id="id" :dataSet="dataSet" :subChartEnabled="true" :variable="variable" :nameMap="nameMap"></barChart>
      <psiLineChart v-if="(!loading)&&type==='psi'" :id="id" :dataSet="dataSet" :subChartEnabled="true"></psiLineChart>
      <statChart v-if="(!loading)&&type==='stat'" :id="id" :dataSet="dataSet" :subChartEnabled="true"></statChart>
    </div>
  </div>
</template>

<script>
import topbar from '@/components/TopBar.vue'
import lineChart from '@/components/lineChart.vue'
import barChart from '@/components/barChart.vue'
import psiLineChart from '@/components/psiChart.vue'
import statChart from '@/components/statChart.vue'
// import {mapMutations} from 'vuex'
export default {
  name: 'Page',
  // props: ['dataSet'],
  // ...mapMutations(['saveData', 'saveId']),
  created () {
    this.numIds = JSON.parse(localStorage.getItem('numIds'))
    this.countIds = JSON.parse(localStorage.getItem('countIds'))
    // this.numIds = this.$store.state.charts
    // console.log(this.$store.state.charts)
    this.update()
    // this.dataSet = this.$store.state.charts.dataSet
    // this.chartTitle = this.$store.state.charts.chartTitle
  },
  watch: {
    '$route.path' () {
      this.loading = true
      this.update()
    }
  },
  methods: {
    update () {
      this.$nextTick(() => {
        this.dataSet = JSON.parse(sessionStorage.getItem(this.id))
        this.variable = sessionStorage.getItem(this.id + 'Var')
        this.type = sessionStorage.getItem(this.id + 'type')
        if (this.type === 'bar') {
          this.nameMap = JSON.parse(sessionStorage.getItem(this.id + 'nameMap'))
        }

        this.loading = false
        // console.log(this.dataSet)
      })
    }
  },
  computed: {
    id () {
      return this.$route.params.id
    }
  },
  data () {
    return {
      numIds: [],
      countIds: [],
      // id: this.$route.params.id,
      type: '',
      dataSet: [],
      variable: '',
      nameMap: {},
      loading: true
    }
  },
  components: {
    topbar,
    lineChart,
    barChart,
    psiLineChart,
    statChart
  }
}
</script>

<style lang="css">
</style>
