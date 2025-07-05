import { createStore } from 'vuex'

export default createStore({
    state: {
        user: null,
        token: localStorage.getItem('token') || null,
        notifications: []
    },
    mutations: {
        setUser(state, user) {
            state.user = user
        },
        setToken(state, token) {
            state.token = token
            localStorage.setItem('token', token)
        },
        clearAuthData(state) {
            state.user = null
            state.token = null
            localStorage.removeItem('token')
        },
        setNotifications(state, notifications) {
            state.notifications = notifications
        },
        markNotificationAsRead(state, notificationId) {
            const notification = state.notifications.find(n => n.id === notificationId)
            if (notification) {
                notification.isRead = true
                notification.readAt = new Date().toISOString()
            }
        },
        removeNotification(state, notificationId) {
            state.notifications = state.notifications.filter(n => n.id !== notificationId)
        }
    },
    actions: {
        async fetchNotifications({ commit }) {
            try {
                const response = await NotificationAPI.getAllNotifications()
                commit('setNotifications', response.data)
            } catch (error) {
                console.error('获取通知失败:', error)
            }
        }
    },
    getters: {
        isAuthenticated: state => !!state.token,
        unreadNotifications: state => state.notifications.filter(n => !n.isRead),
        user: state => state.user
    }
})
