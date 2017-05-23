<template lang="html">
    <div class="side-bar">
        <!-- <a id="getBar" class="bar">getBar</a> -->
        <router-link to="/bar">getBar</router-link>
        <a id="history" class="bar">history</a>
        <a id="output" class="bar">apply</a>
        <a href="#bar" id="columnConfig" class="bar">column</a>
        <a href="#uploaded" id="prev" class="bar">prevInit</a>
        <a href="#select" id="select" class="bar">select</a>
    </div>
</template>

<script>
export default {
    created() {
        $("#getBar").click(function() {
            window.location.href = window.location.href.substr(0, window.location.href.indexOf("#")) + "#bar";
        });
    }
}
</script>

<style lang="css">  
.side-bar a {
    color: #333;
    font-size: 15px;
    line-height: 66px;
    text-align: center;
    text-decoration: none;
    padding: 0 !important;
    cursor: pointer;
}
.side-bar a:hover {
    color: #fff;
}
</style>
