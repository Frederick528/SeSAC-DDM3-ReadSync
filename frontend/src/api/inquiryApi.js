import axios from "axios";

const INQUIRY = "/api/inquiries";
export const getInquiries = () => axios.get(INQUIRY);
export const getInquiry = (id) => axios.get(`${INQUIRY}/${id}`);
export const createInquiry = (data) => axios.post(INQUIRY, data);