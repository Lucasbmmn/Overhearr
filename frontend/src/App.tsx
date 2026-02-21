import { BrowserRouter, Routes, Route } from "react-router-dom";
import ProtectedRoute from "./components/ProtectedRoute";
import AppLayout from "./pages/AppLayout";
import Login from "./pages/Login";
import ProfileSettingsLayout from "./components/profile/settings/ProfileSettingsLayout";
import PasswordSettingsPage from "./pages/profile/settings/PasswordSettingsPage";
import ProfileSettingsPage from "./pages/profile/settings/ProfileSettingsPage";
import UsersPage from "./pages/UsersPage";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<Login />} />

                <Route element={<ProtectedRoute />}>
                    <Route element={<AppLayout />}>
                        <Route path="/" element="" />
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