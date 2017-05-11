<template lang="html">
  <div>
    <topbar :numIds="numIds" :countIds="countIds"></topbar>
    <div class="chart-card">
      <LineChart v-if="type==='line'" :id="id" :dataSet="dataSet" :subChartEnabled="true" :variable="variable" />
      <BarChart v-if="type==='bar'" :id="id" :dataSet="dataSet" :subChartEnabled="true" :variable="variable" :nameMap="nameMap" />
      <PsiLineChart v-if="type==='psi'" :id="id" :dataSet="dataSet" :subChartEnabled="true" />
      <StatChart v-if="type==='stat'" :id="id" :dataSet="dataSet" :subChartEnabled="true" />
    </div>
  </div>
</template>

<script>
import topbar from '@/components/TopBar.vue'
import LineChart from '@/components/LineChart.vue'
import BarChart from '@/components/BarChart.vue'
import PsiLineChart from '@/components/PsiLineChart.vue'
import StatChart from '@/components/StatChart.vue'
// import {mapMutations} from 'vuex'
export default {
  name: 'Page',
  // props: ['dataSet'],
  // ...mapMutations(['saveData', 'saveId']),
  created() {
    this.numIds = JSON.parse(localStorage.getItem('numIds'))
    this.countIds = JSON.parse(localStorage.getItem('countIds'))
    this.psiIds = JSON.parse(localStorage.getItem('psiIds'))
    // this.numIds = this.$store.state.charts
    // console.log(this.$store.state.charts)
    this.update()
    // this.dataSet = this.$store.state.charts.dataSet
    // this.chartTitle = this.$store.state.charts.chartTitle
  },
  methods: {
    update() {
      this.type = sessionStorage.getItem(this.id + 'type')
      this.$nextTick(() => {
        this.dataSet = JSON.parse(sessionStorage.getItem(this.id))
        this.variable = sessionStorage.getItem(this.id + 'Var')
        if (this.type === 'bar') {
          this.nameMap = JSON.parse(sessionStorage.getItem(this.id + 'nameMap'))
        }

        this.loading = false
        // console.log(this.dataSet)
      })
    }
  },
  computed: {
    id() {
      return this.$route.params.id
    }
  },
  data() {
    return {
      numIds: [],
      countIds: [],
      psiIds: [],
      // id: this.$route.params.id,
      type: '',
      dataSet: {},
      variable: '',
      nameMap: {},
      loading: true
    }
  },
  components: {
    topbar,
    LineChart,
    BarChart,
    PsiLineChart,
    StatChart
  }
}
</script>

<style lang="css">
</style>
