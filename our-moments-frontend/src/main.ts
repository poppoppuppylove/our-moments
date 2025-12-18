import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import pinia from './store'
import { vLazy } from './composables/useLazyImage'

// 导入全局样式
import './assets/styles/main.scss'

const app = createApp(App)

// 注册全局指令
app.directive('lazy', vLazy)

// 使用插件
app.use(pinia)
app.use(router)

app.mount('#app')
