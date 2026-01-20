import axios from "axios";

const NOTICE = "/api/notices";
export const getNotices = () => axios.get(NOTICE);
export const getNotice = (id) => axios.get(`${NOTICE}/${id}`)