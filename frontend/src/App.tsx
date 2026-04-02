import { BrowserRouter, Routes, Route } from "react-router-dom";
import { useTranslation } from "react-i18next";
import ProtectedRoute from "./components/ProtectedRoute";
import AppLayout from "./pages/AppLayout";
import Login from "./pages/Login";
import SearchPage from "./pages/SearchPage";
import ProfileSettingsLayout from "./components/profile/settings/ProfileSettingsLayout";
import PasswordSettingsPage from "./pages/profile/settings/PasswordSettingsPage";
import ProfileSettingsPage from "./pages/profile/settings/ProfileSettingsPage";
import UsersPage from "./pages/UsersPage";

function App() {
    const { t } = useTranslation();

    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<Login />} />

                <Route element={<ProtectedRoute />}>
                    <Route element={<AppLayout />}>
                        <Route path="/" element={<div className="p-8 text-center text-gray-400">{t('search.welcome')}</div>} />

                        <Route path="/search" element={<SearchPage />} />
                        <Route path="/users" element={<UsersPage />} />
                        <Route element={<ProfileSettingsLayout />}>
                            <Route path="/profile/settings" element={<ProfileSettingsPage />} />
                            <Route path="/profile/settings/password" element={<PasswordSettingsPage />} />
                        </Route>
                    </Route>
                </Route>
            </Routes>
        </BrowserRouter>
    );
}

export default App;