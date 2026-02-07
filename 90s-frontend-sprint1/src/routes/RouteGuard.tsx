import { Navigate } from "react-router-dom";
import { useAuth } from "../store/authStore";

export function RequireAuth({ children }: { children: JSX.Element }) {
  const { isAuthed, loading } = useAuth();
  if (loading) return <div style={{ padding: 24 }}>Loading...</div>;
  if (!isAuthed) return <Navigate to="/auth" replace />;
  return children;
}

export function RequireHasSpace({ children }: { children: JSX.Element }) {
  const { me, loading } = useAuth();
  if (loading) return <div style={{ padding: 24 }}>Loading...</div>;

  const hasSpace = (me?.spaces?.length ?? 0) > 0;
  if (!hasSpace) return <Navigate to="/join" replace />;
  return children;
}
