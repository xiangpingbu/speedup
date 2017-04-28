<template lang="html">
  <div>
    <topbar></topbar>
      <div class="text-center" style="text-align:center">
        <span style="font-size: 1.5rem">Home Dashboard</span>
      </div>
      <div class="pure-g">
        <div class="pure-u-3-5 panel-container" style="min-height: 510px;">
          <div class="panel-header">
            <span></span>
            <div class="panel-title-container">
              <span></span>
            </div>
          </div>
          <div class="panel-content">
            <div class="dashlist"><!---->
              <div class="dashlist-section">
                <h6 class="dashlist-section-header">Starred Models</h6>
                <div class="dashlist-item">
                  <router-link to="/dash/" class="dashlist-link dashlist-link-dash-db">
                      <span class="dashlist-title">xyb_model </span>
                      <span class="dashlist-star">
                        <i class="fa fa-star"></i>
                      </span>
                  </router-link>
                </div>

                <div class="dashlist-item">
                  <a class="dashlist-link dashlist-link-dash-db" href="#">
                    <span class="dashlist-title">health_check </span>
                    <span class="dashlist-star">
                      <i class="fa fa-star"></i>
                    </span>
                  </a>
                </div>

                <div class="dashlist-item">
                  <a class="dashlist-link dashlist-link-dash-db" href="#">
                    <span class="dashlist-title">health_check </span>
                    <span class="dashlist-star">
                      <i class="fa fa-star"></i>
                    </span>
                  </a>
                </div>
              </div><!---->
            </div>

            <div class="dashlist"><!---->
              <div class="dashlist-section">
                <h6 class="dashlist-section-header">Recently viewed Models</h6>
                <div class="dashlist-item">
                  <a class="dashlist-link dashlist-link-dash-db" href="#">
                    <span class="dashlist-title">health_check </span>
                    <span class="dashlist-star">
                      <i class="fa fa-star"></i>
                    </span>
                  </a>
                </div>
              </div><!---->
            </div>
          </div>
        </div>
      </div>
  </div>
</template>

<script>
import topbar from '@/components/TopBar.vue'
export default {
  name: 'Home',
  components: {
    topbar
  }
}
</script>

<style lang="css">
</style>
