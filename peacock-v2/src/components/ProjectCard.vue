<template>
  <div class="col-lg-4 col-md-4 col-sm-6 card">
    <div class="media">
      <div class="card-icon pull-left">
        <a href="#">
          <i class="fa fa-folder-o fa-5" aria-hidden="true"></i>
        </a>
      </div>
      <div class="card-body">
        <h4 class="card-head">
          <router-link to='/'>{{Title}}</router-link>
        </h4>
        <ul class="ul-no-dots f1-15-r">
          <li>
            <div>Created on:</div>
            <div>{{CreatedTime}}</div>
          </li>
          <li>
            <div>Scope:</div>
            <div>Public</div>
          </li>
          <li>
            <div>Task:</div>
            <div></div>
          </li>
          <li>
            <div>Hardware:</div>
            <div></div>
          </li>
          <li>
            <div>Models:</div>
            <div></div>
          </li>
          <li>
            <div>Description:</div>
            <div></div>
          </li>
        </ul>
        <button type="button" class="btn btn-primary pull-right" @click="viewDetails(id)">
          Open Project
          <i class="fa fa-angle-double-right"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProjectCard',
  props: ['id'],
  created () {
    console.log(this.id)
  },
  data () {
    var date = new Date()
    return {
      Title: 'ProjectName',
      dataMap: {},
      CreatedTime: date.toLocaleDateString()
      // Scope: '',
      // Task: '',
      // Models: 0,
      // Description: ''
    }
  },
  methods: {
    viewDetails (id) {
      this.$router.push({
        name: 'source',
        params: {
          id
        }
      })
    }
  }
}
</script>

<style lang="css">
.card {
  position: relative;
  /*background-color: #191970;*/
  margin: 10px;
  padding: 10px;
  /*border: 1px solid #292929;*/
  border: 1px solid #c7c7c7;
  font-size: 1.1rem;
  /*text-align: center;*/
  /*cursor: pointer;*/
}
.card-icon {
  /*display: inline-block;*/
  float: left;
}
.card-body {
  margin: 10px;
}
.card-head a {
  font-size: 20px;
  margin-left: 10px;
}
.card li {
  list-style-type:none;
  display: table-row;
}
.card .media li div:first-child {
  font-weight: 600;
  width: 25%;
}
.card .media li div:last-child {
  font-size: 13px;
  line-height: 20px;
}
.card .media li div {
  border-bottom: 1px solid #eee;
  display: table-cell;
  width: 50%;
  padding: 6px 2px 6px 2px;
  vertical-align: top;
}
.fa-5 {
  font-size: 4em;
}
.ul-no-dots {
    /* list-style: none; */
    margin-left: 10px;
}
.f1-15-r {
    font-family: Roboto,sans-serif;
    font-size: 15px;
}
</style>
