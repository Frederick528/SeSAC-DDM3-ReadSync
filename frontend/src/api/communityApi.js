import axios from "axios";
const API = "/api/community";


export const getPosts = () => axios.get(`${API}/posts`);
export const getPost = (id) => axios.get(`${API}/posts/${id}`);
export const createPost = (data) => axios.post(`${API}/posts`, data);
export const getComments = (postId) => axios.get(`${API}/comments/${postId}`);
export const createComment = (postId, data) => axios.post(`${API}/comments/${postId}`, data);

