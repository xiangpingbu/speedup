<template>
  <div>
    <div class="col-lg-4 col-md-4 col-sm-6" v-for="project in projects">
      <div class="panel-grey">
        <div class="panel-body">
          <div class="media">
            <div class="media-left media-top">
              <a href="#">
                <i class="fa fa-folder-o fa-5" aria-hidden="true"></i>
              </a>
            </div>
            <div class="media-body">
              <h4 class="media-heading f1-28-t">
                <router-link to='/'>{{project.project_name}}</router-link>
                <!--<router-link to='/'></router-link>-->
              </h4>
              <ul class="ul-no-dots f1-15-r">
                <li>
                  <div>Created on:</div>
                  <div>{{project.create_date}}</div>
                  <div></div>
                </li>
                <!--<li>-->
                <!--<div>Scope:</div>-->
                <!--<div>Public</div>-->
                <!--</li>-->
                <li>
                  <div>Task:</div>
                  <div>{{project.project_task}}</div>
                </li>
                <li>
                  <div>Hardware:</div>
                  <div>MAAS CLOUD</div>
                </li>
                <li>
                  <div>Description:</div>
                  <div>{{project.project_desc}}</div>
                </li>
              </ul>
              <button type="button" class="btn btn-primary pull-right" @click="viewDetails(project.id)">
                Open Project
                <i class="fa fa-angle-double-right"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'ProjectCard',
    props: ['projects'],
    created () {
    },
    data () {
      return {
//        projects: this.props.projects
      }
    },
    methods: {
      viewDetails (id) {
        this.$router.push({
          name: 'project',
          params: {
            id
          }
        })
      }
    }
  }
</script>

<style lang="css">
  .panel-body {
    padding: 15px 20px 15px 20px;
  }

  .panel-grey {
    border: 1px solid rgba(0, 0, 0, .075);
    margin-bottom: 15px;
    background-color: #fcfcfc;
  }

  .panel-grey .panel-body {
    padding: 15px 20px 15px 20px;
  }

  .media:first-child {
    margin-top: 0;
  }

  .media, .media-body {
    overflow: hidden;
    zoom: 1;
  }

  .panel-grey .media .media-left {
    padding-right: 15px;
  }

  .media-body, .media-left, .media-right {
    display: table-cell;
    vertical-align: top;
  }

  .media-body, .media-left, .media-right {
    display: table-cell;
    vertical-align: top;
  }

  .media-body {
    width: 10000px;
  }

  .media, .media-body {
    overflow: hidden;
    zoom: 1;
  }

  .panel-grey .media .media-heading {
    max-width: 1000px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
  .f1-28-t {
    font-family: Roboto,sans-serif;
    font-size: 28px;
    font-weight: 100;
  }
  .media-heading {
    margin-top: 0;
    margin-bottom: 5px;
  }

  .panel-grey .media a {
    color: #404040;
  }

  .card-head a {
  font-size: 20px;
  margin-left: 10px;
  }

   li {
  list-style-type: none;
  display: table-row;
  }

   .media li div:first-child {
  font-weight: 600;
  width: 25%;
  }

   .media li div:last-child {
  font-size: 13px;
  line-height: 20px;
  }

   .media li div {
  border-bottom: 1px solid #eee;
  display: table-cell;
  width: 50%;
  padding: 6px 2px 6px 2px;
  vertical-align: top;
  }

  .panel-grey .media ul {
    display: table;
    width: 100%;
    margin-bottom: 15px;
    margin-top: 15px;
  }

  .fa-5 {
  font-size: 3em;
  }

  .ul-no-dots {
    list-style: none;
    padding-left: 0;
  }

  .f1-15-r {
  font-family: Roboto, sans-serif;
  font-size: 15px;
  }
</style>
