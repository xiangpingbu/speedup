export default {
  url_prefix: '/es/resource/model_xyb_monitor_',
  personal_live_join: {
    '1,': '父母',
    '2,': '配偶及子女',
    '1,2,': '父母、配偶及子女',
    '4,': '其他',
    '3,': '朋友',
    '2,4,': '配偶及子女、其他',
    '1,4,': '父母、其他',
    '1,2,4,': '父母、配偶子女、其他',
    'others': '不属于以上情况'
  },
  personal_education: {
    '1': '硕士及以上',
    '2': '本科',
    '3': '专科',
    '4': '其他',
    'others': '不属于以上情况'
  },
  client_gender: {
    '1': '男',
    '2': '女',
    'others': '其他'
  },
  personal_live_case: {
    '1': '自有商业按揭房',
    '2': '自有无按揭购房',
    '3': '自有公积金按揭购房',
    '4': '自建房',
    '5': '租房',
    '6': '亲戚住房',
    '7': '宿舍',
    '8': '其他',
    'others': '不属于以上情况'
  },
  // 色值未设定的会自动生成  TODO:多些色值，注意顺序
  data_colors: ['#2970a1', '#84594E', '#BCDACB', '#47A0FB', '#9F347D', '#28BBCC', '#8A72BC', '#B95839', '#7eb26d', '#849396', '#7EC1CF']
  // data_colors: []
}
