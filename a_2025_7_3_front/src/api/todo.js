import axios from 'axios'

const API_URL = 'http://localhost:8080/api/todos'

const getAllTodos = async (username) => {
    const response = await axios.get(API_URL, {
        params: { username }  // 作为查询参数传递
    });
    return response.data;
}

const createTodo = async (todoData) => {
    const response = await axios.post(API_URL, todoData)
    return response.data
}

const updateTodo = async (id, todoData) => {
    const response = await axios.put(`${API_URL}/${id}`, todoData)
    return response.data
}

const deleteTodo = async (id) => {
    await axios.delete(`${API_URL}/${id}`)
}

export default {
    getAllTodos,
    createTodo,
    updateTodo,
    deleteTodo
}
