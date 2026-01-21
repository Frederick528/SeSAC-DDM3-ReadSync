import { Routes, Route } from "react-router-dom";
import MainLayout from "./layout/MainLayout";

import MainPage from "./pages/main/MainPage";
import CommunityList from "./pages/community/CommunityList";
import CommunityDetail from "./pages/community/CommunityDetail";

function App() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        <Route path="/" element={<MainPage />} />
        <Route path="/community" element={<CommunityList />} />
        <Route path="/community/:id" element={<CommunityDetail />} />
      </Route>
    </Routes>
  );
}

export default App;
