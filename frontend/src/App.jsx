import { Routes, Route } from "react-router-dom";
import MainLayout from "./layout/MainLayout";

import MainPage from "./pages/main/MainPage";

// community
import CommunityList from "./pages/community/CommunityList";
import CommunityDetail from "./pages/community/CommunityDetail";

// inquiry
import InquiryList from "./pages/inquiry/InquiryList";
import InquiryDetail from "./pages/inquiry/InquiryDetail";

// notice
import NoticeList from "./pages/notice/NoticeList";
import NoticeDetail from "./pages/notice/NoticeDetail";

function App() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        <Route path="/" element={<MainPage />} />

        {/* Community */}
        <Route path="/community" element={<CommunityList />} />
        <Route path="/community/:id" element={<CommunityDetail />} />

        {/* Inquiry */}
        <Route path="/inquiry" element={<InquiryList />} />
        <Route path="/inquiry/:id" element={<InquiryDetail />} />

        {/* Notice */}
        <Route path="/notice" element={<NoticeList />} />
        <Route path="/notice/:id" element={<NoticeDetail />} />
      </Route>
    </Routes>
  );
}

export default App;
