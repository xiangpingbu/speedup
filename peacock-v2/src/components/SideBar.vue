<template>
    <div class="sidebar">
        <!-- TODO use router config dynamic generate -->
        <div v-for="item in routes" :key="item.title">
            <div class="title">{{item.name}}</div>
            <div class="item">
                <router-link v-for="route in item.path" :key="route.name" :to="route.path">{{route.name}}</router-link>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'sidebar',
    data() {
        return {
            routes: [
                {
                    name: 'DATA',
                    path: [
                        {
                            name: 'Sources',
                            path: 'datasource'
                        },
                        {
                            name: 'Preview',
                            path: 'datapreview'
                        }
                    ]
                },
                {
                    name: 'TRAIN & TUNING',
                    path: [
                        {
                            name: 'Experiments',
                            path: 'experiment'
                        },
                        {
                            name: 'Results',
                            path: 'result'
                        }
                    ]
                },
                {
                    name: 'ANALYTICS',
                    path: [
                        {
                            name: 'Predict',
                            path: 'predict'
                        },
                        {
                            name: 'Features Analysis',
                            path: 'feature'
                        },
                        {
                            name: 'Insights and Logs',
                            path: 'insight'
                        }
                    ]
                }
            ]
        }
    },
    created() {
    }
}
</script>

<style>
.sidebar {
    position: absolute;
    width: 280px;
    background: #fafafa;
    height: calc(100vh - 51px);
}
.sidebar .item a {
    display: block;
}
.sidebar .item a.active {
    background: #ffffff;
}
</style>
