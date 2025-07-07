import axios from 'axios'

const API_URL = 'http://localhost:8080/api/notifications'

const getAllNotifications = async (username) => {
    const response = await axios.get(API_URL, {
        params: { username }  // 作为查询参数传递
    });
    return response.data
}

export const createNotification = async (notificationData) => {
    const response = await axios.post(API_URL, notificationData)
    return response.data
}

const markAsRead = async (id) => {
    const response = await axios.patch(`${API_URL}/${id}/read`)
    return response.data
}


export const getNotificationsBySender = async (id) => {
    const response = await axios.get(`${API_URL}/${id}`)
    return response.data
}

export const deleteNotification = async (id) => {
    await axios.delete(`${API_URL}/${id}`)
}

export default {
    getAllNotifications,
    createNotification,
    markAsRead,
    deleteNotification,
    getNotificationsBySender
}
