<template>
  <div>
    <!-- 统计数据 -->
    <el-row :gutter="30">
      <el-col :span="6">
        <el-card>
          <div class="card-desc">
            <div class="card-title"><i class="iconfont" id="iconfont1">&#xe648;</i> &nbsp;访问量</div>
            <div class="card-count">{{ viewsCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="card-desc">
            <div class="card-title"><i class="iconfont" id="iconfont2">&#xe626;</i> &nbsp;用户量</div>
            <div class="card-count">{{ userCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="card-desc">
            <div class="card-title"><i class="iconfont" id="iconfont3">&#xe606;</i> &nbsp;文章量</div>
            <div class="card-count">{{ articleCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="card-desc">
            <div class="card-title"><i class="iconfont" id="iconfont4">&#xe616;</i> &nbsp;留言量</div>
            <div class="card-count">{{ messageCount }}</div>
          </div>
        </el-card>
      </el-col>
      <!-- 一周访问量展示 -->
      <el-card style="margin-top:1.25rem;margin-left: 12px;margin-right: 12px;width: 100%" v-loading="loading">
        <div class="e-title">一周访问量</div>
        <div id="chartLineBox" style="height:350px;"></div>
      </el-card>
    </el-row>
    <el-row :gutter="30" style="margin-top:1.25rem">
      <!-- 文章浏览量排行 -->
      <el-col :span="16">
        <el-card v-loading="loading">
          <div class="e-title">文章浏览量排行</div>
          <div style="height:350px" id="articleViewData">
          </div>
        </el-card>
      </el-col>
      <!-- 分类数据统计 -->
      <el-col :span="8" v-loading="loading">
        <el-card>
          <div class="e-title">文章分类统计</div>
          <div style="height:350px" id="articleCountData">
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import axios from "axios";

export default {
  name: "Main",
  data() {
    return {
      loading: true,
      viewsCount: 0,
      messageCount: 0,
      userCount: 0,
      articleCount: 0,
      viewsData: {
        xAxis: {
          type: 'category',
          data: [],
          axisLine: {
            lineStyle: {
              // 设置x轴颜色
              color: '#666'
            }
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: ['访问量']
        },
        yAxis: {
          type: 'value',
          axisLine: {
            lineStyle: {
              // 设置y轴颜色
              color: '#048CCE'
            }
          }
        },
        color: ['#3888fa'],
        grid: {
          left: '0%',
          right: '0%',
          bottom: '0%',
          top: '10%',
          containLabel: true
        },
        series: [
          {
            name: '访问量',
            type: 'line',
            data: [],
            smooth: true
          }
        ]
      },
      articleViewData: {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            data: [],
            axisTick: {
              alignWithLabel: true
            }
          }
        ],
        yAxis: [
          {
            type: 'value'
          }
        ],
        series: [
          {
            name: '浏览量',
            type: 'bar',
            barWidth: '60%',
            data: []
          }
        ]
      },
      articleCountData: {
        color: ['#7EC0EE', '#FF9F7F', '#FFD700', '#C9C9C9', '#E066FF', '#C0FF3E'],
        legend: {
          bottom: 'bottom'
        },
        tooltip: {
          trigger: 'item'
        },
        toolbox: {
          show: true,
        },
        series: [
          {
            name: '文章分类',
            type: 'pie',
            roseType: 'radius',
            data: []
          }
        ]
      }
    }
  },
  created() {
    this.getBlogInfo()
  },
  methods: {
    getBlogInfo() {
      axios.get("/api/blogInfo/adminBlogInfo").then(res => {
        this.viewsCount = res.data.viewsCount
        this.userCount = res.data.userCount
        this.articleCount = res.data.articleCount
        this.messageCount = res.data.messageCount
        if (res.data.uniqueViewList !== null) {
          res.data.uniqueViewList.forEach(item => {
            this.viewsData.xAxis.data.push(item.createTime);
            this.viewsData.series[0].data.push(item.viewsCount);
          });
        }
        if (res.data.classList !== null) {
          res.data.classList.forEach(item => {
            this.articleCountData.series[0].data.push({
              value: item.articleTotal,
              name: item.classificationName
            })
          })
        }
        if (res.data.articleRankList !== null) {
          res.data.articleRankList.forEach(item => {
            this.articleViewData.series[0].data.push(item.viewsCount)
            this.articleViewData.xAxis[0].data.push(item.articleTitle)
          })
        }
        document.getElementById('chartLineBox').removeAttribute('_echarts_instance_')
        document.getElementById('articleViewData').removeAttribute('_echarts_instance_')
        document.getElementById('articleCountData').removeAttribute('_echarts_instance_')
        // 指定图表的配置项和数据 , setOption()方法使刚指定的配置项和数据显示图表。
        echarts.init(document.getElementById('chartLineBox')).setOption(this.viewsData, true); //一周访问量
        echarts.init(document.getElementById('articleViewData')).setOption(this.articleViewData, true) //文章浏览量排行
        echarts.init(document.getElementById('articleCountData')).setOption(this.articleCountData, true) //文章分类统计
        this.loading = false
      })
    }
  }
}
</script>

<style scoped>
.card-count {
  text-align: center;
  font-size: 20px;
}

.card-title {
  text-align: center;
  font-size: 23px;
}

.iconfont {
  font-size: 25px;
}

#iconfont1 {
  color: #23afe7;
}

#iconfont2 {
  color: #23afe7;
}

#iconfont3 {
  color: #de0d25;
}

#iconfont4 {
  color: #23afe7;
}

.e-title {
  font-size: 13px;
  color: #202a34;
  font-weight: 700;
}
</style>