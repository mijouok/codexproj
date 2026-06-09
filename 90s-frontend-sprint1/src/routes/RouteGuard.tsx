import { Navigate } from "react-router-dom";
import { useAuth } from "../store/authStore";

export function RequireAuth({ children }: { children: JSX.Element }) {
  const { isAuthed, loading } = useAuth();
  if (loading) return <div className="x-loading">Loading...</div>;
  if (!isAuthed) return <Navigate to="/auth" replace />;
  return children;
}
