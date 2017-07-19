<template>
    <div class="sidebar" :class="{'toggle-hide': toggleStatus}">
        <!-- TODO use router config dynamic generate -->
        <div class="sidebar-title">
            <span class="title-name">icon | {{$route.params.id}}</span>
            <a class="toggle-btn" @click="toggleSidebar">
                <!-- TODO use icon  -->
                &lt;
            </a>
        </div>
        <div v-for="item in routes" :key="item.title">
            <div class="block-title">{{item.name}}</div>
            <div class="item">
                <router-link v-for="route in item.path" :key="route.name" :to="route.path">
                    icon |
                    <span class="item-name">{{route.name}}</span>
                </router-link>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'sidebar',
    data() {
        return {
            toggleStatus: false,
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
    methods: {
        toggleSidebar() {
            this.toggleStatus = !this.toggleStatus
            this.$root.$emit('toggleStatus', this.toggleStatus)
        }
    },
    created() {
    }
}
</script>

<style lang="scss" scoped>
.sidebar {
    position: absolute;
    width: 280px;
    background: #fafafa;
    height: calc(100vh - 51px);
    box-shadow: inset -1px 0 0 0 rgba(0, 0, 0, .075);
    * {
        white-space: nowrap;
    }
    &.toggle-hide {
        width: 60px;
        .sidebar-title {
            width: 60px;
        }
        .toggle-btn {
            transform: rotate(180deg);
        }
        .block-title, .title-name, .item-name {
            display: none;
        }
    }

    a {
        text-decoration: none;
    }

    .sidebar-title {
        line-height: 54px;
        padding-left: 15px;
        font-weight: bold;
        overflow: hidden;
        border-bottom: 1px solid rgba(0, 0, 0, .075);
        .toggle-btn {
            width: 36px;
            height: 34px;
            line-height: 30px;
            text-align: center;
            float: right;
            margin: 10px 14px;
            border: 1px solid rgba(0, 0, 0, 0);
            &:hover {
                border-radius: 2px;
                border: 1px solid rgba(0, 0, 0, .075);
            }
        }
    }

    .block-title {
        line-height: 36px;
        font-size: 12px;
        font-weight: bold;
        padding-left: 15px;
    }
    .item {
        a {
            color: #337ab7;
            padding-left: 20px;
            font-size: 15px;
            line-height: 40px;
            display: block;
            &:hover {
                background: #eee;
            }
        }
        .router-link-active {
            position: relative;
            background: #ffffff;
            padding-left: 15px;
            border-left: 5px solid #337ab7;
        }
    }
}
</style>
