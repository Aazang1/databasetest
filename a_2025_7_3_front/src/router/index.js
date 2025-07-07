import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Todo from '../views/Todo.vue'
import Notification from '../views/Notification.vue'
import store from '../store'
import Test from "@/views/test.vue";
import SendNotification from "@/views/SendNotification.vue";
import SetTime from "@/views/SetTime.vue";

const routes = [
    {
        path: '/',
        name: 'Login',
        component: Login,
        meta: { requiresGuest: true }
    },
    {
        path: '/register',
        name: 'Register',
        component: Register,
        meta: { requiresGuest: true }
    },
    {


        path: '/index',
        name: 'Home',
        component: Home,
        meta: { requiresAuth: true },
        children: [

            {
                path: '/todos',
                name: 'Todo',
                component: Todo,
                meta: { requiresAuth: true }
            },
            {
                path: '/notifications/receive',
                name: 'NotificationReceive',
                component: Notification,
                meta: { requiresAuth: true }
            },
            {
                path: '/notifications/send',
                name: 'NotificationSend',
                component: SendNotification,
                meta: { requiresAuth: true }
            },
            {
                path: '/test',
                name: 'test',
                component: Test,
                meta: { requiresAuth: true }
            },
            {
                path: '/todos/settime',
                name: 'settime',
                component: SetTime,
                meta: { requiresAuth: true }
            },
        ]
    }

]

const router = createRouter({
    history: createWebHistory(),
    routes
})
export default router
