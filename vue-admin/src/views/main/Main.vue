<template>
  <div>
    <!-- 统计数据 -->
    <el-row :gutter="30">
      <el-col :span="6">
        <el-card>
          <div class="card-desc">
            <div class="card-title"><i class="iconfont" id="iconfont1">&#xe648;</i> &nbsp;访问量</div>
            <div class="card-count">100</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="card-desc">
            <div class="card-title"><i class="iconfont" id="iconfont2">&#xe626;</i> &nbsp;用户量</div>
            <div class="card-count">200</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="card-desc">
            <div class="card-title"><i class="iconfont" id="iconfont3">&#xe606;</i> &nbsp;文章量</div>
            <div class="card-count">400</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="card-desc">
            <div class="card-title"><i class="iconfont" id="iconfont4">&#xe616;</i> &nbsp;留言量</div>
            <div class="card-count">300</div>
          </div>
        </el-card>
      </el-col>
      <!-- 一周访问量展示 -->
      <el-card style="margin-top:1.25rem;margin-left: 12px;margin-right: 12px;width: 100%">
        <div class="e-title">一周访问量</div>
        <div id="chartLineBox" style="height:350px;"></div>
      </el-card>
    </el-row>
    <el-row :gutter="30" style="margin-top:1.25rem">
      <!-- 文章浏览量排行 -->
      <el-col :span="16">
        <el-card>
          <div class="e-title">文章浏览量排行</div>
          <div style="height:350px" id="articleViewData">
          </div>
        </el-card>
      </el-col>
      <!-- 分类数据统计 -->
      <el-col :span="8">
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

export default {
  name: "Main",
  data() {
    return {
      viewsData: {
        xAxis: {
          type: 'category',
          data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
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
            data: [820, 932, 901, 934, 1290, 1330, 320],
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
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
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
            name: 'Direct',
            type: 'bar',
            barWidth: '60%',
            data: [10, 52, 200, 334, 390, 330, 220]
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
            data: [
              {value: 40, name: '多线程'},
              {value: 38, name: 'Java'},
              {value: 32, name: 'vue'},
            ]
          }
        ]
      }
    }
  }
  ,
  mounted() {
    // 指定图表的配置项和数据 , setOption()方法使刚指定的配置项和数据显示图表。
    echarts.init(document.getElementById('chartLineBox')).setOption(this.viewsData); //一周访问量
    echarts.init(document.getElementById('articleViewData')).setOption(this.articleViewData) //文章浏览量排行
    echarts.init(document.getElementById('articleCountData')).setOption(this.articleCountData) //文章分类统计
  }
  ,
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