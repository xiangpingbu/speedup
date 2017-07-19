<template>
    <div>
        <!-- TODO：这个topbar和首页的不一样，可在Topbar再做一些处理或新加一个组件  -->
        <TopBar></TopBar>
        <div class="main">
            <router-view></router-view>
        </div>
    </div>
</template>

<script>
import TopBar from '@/components/TopBar.vue'
export default {
    name: 'project',
    components: {
        TopBar
    }
}
</script>
