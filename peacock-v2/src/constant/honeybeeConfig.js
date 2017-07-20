import '@/constant/global'
import axios from 'axios'
export default {
  instance: axios.create({
    baseURL: global.host,
    timeout: 1000
  })
}
