import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./store/authStore";
import { RequireAuth, RequireHasSpace } from "./routes/RouteGuard";
import AuthPage from "./pages/AuthPage";
import JoinSpacePage from "./pages/JoinSpacePage";
import MePage from "./pages/MePage";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/auth" element={<AuthPage />} />

          <Route
            path="/join"
            element={
              <RequireAuth>
                <JoinSpacePage />
              </RequireAuth>
            }
          />

          <Route
            path="/me"
            element={
              <RequireAuth>
                <RequireHasSpace>
                  <MePage />
                </RequireHasSpace>
              </RequireAuth>
            }
          />

          <Route path="/" element={<Navigate to="/me" replace />} />
          <Route path="*" element={<Navigate to="/me" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
