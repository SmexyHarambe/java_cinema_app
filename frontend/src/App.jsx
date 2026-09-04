import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from './store/authStore'

// Pages
import Login from './pages/Login'
import Register from './pages/Register'
import CustomerDashboard from './pages/CustomerDashboard'
import AdminDashboard from './pages/AdminDashboard'
import TicketList from './pages/TicketList'
import TicketDetail from './pages/TicketDetail'
import Notifications from './pages/Notifications'
import Topup from './pages/Topup'

function ProtectedRoute({ children, allowedRoles }) {
  const { token, user } = useAuthStore()
  
  if (!token) {
    return <Navigate to="/login" replace />
  }
  
  if (allowedRoles && !allowedRoles.includes(user?.role)) {
    return <Navigate to="/" replace />
  }
  
  return children
}

function App() {
  const { user } = useAuthStore()

  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        
        <Route
          path="/"
          element={
            user?.role === 'ADMIN' ? (
              <Navigate to="/admin/dashboard" replace />
            ) : (
              <Navigate to="/customer/dashboard" replace />
            )
          }
        />

        <Route
          path="/customer/dashboard"
          element={
            <ProtectedRoute allowedRoles={['CUSTOMER']}>
              <CustomerDashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path="/customer/tickets"
          element={
            <ProtectedRoute allowedRoles={['CUSTOMER']}>
              <TicketList />
            </ProtectedRoute>
          }
        />

        <Route
          path="/customer/tickets/:id"
          element={
            <ProtectedRoute allowedRoles={['CUSTOMER']}>
              <TicketDetail />
            </ProtectedRoute>
          }
        />

        <Route
          path="/customer/notifications"
          element={
            <ProtectedRoute allowedRoles={['CUSTOMER']}>
              <Notifications />
            </ProtectedRoute>
          }
        />

        <Route
          path="/customer/topup"
          element={
            <ProtectedRoute allowedRoles={['CUSTOMER']}>
              <Topup />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin/dashboard"
          element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <AdminDashboard />
            </ProtectedRoute>
          }
        />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  )
}

export default App
