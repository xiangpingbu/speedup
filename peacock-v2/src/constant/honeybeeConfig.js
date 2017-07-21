import '@/constant/global'
import axios from 'axios'
const instance = axios.create({
  baseURL: 'http://localhost:8091',
  timeout: 1000
})
export default instance

