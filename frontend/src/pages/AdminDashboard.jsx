import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'
import { Film, LogOut } from 'lucide-react'

export default function AdminDashboard() {
  const navigate = useNavigate()
  const logout = useAuthStore((state) => state.logout)

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navbar */}
      <nav className="bg-white shadow-md">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <Film className="h-8 w-8 text-primary-600" />
              <span className="ml-2 text-xl font-bold text-gray-900">UAS Disprog - Admin</span>
            </div>
            
            <div className="flex items-center space-x-4">
              <button 
                onClick={handleLogout}
                className="flex items-center px-4 py-2 text-red-600 hover:text-red-800 hover:bg-red-50 rounded-lg transition-colors"
              >
                <LogOut className="h-5 w-5 mr-2" />
                <span className="text-sm font-medium">Logout</span>
              </button>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-8">Admin Dashboard</h1>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="card">
            <h3 className="text-xl font-semibold text-gray-900 mb-4">Manage Tickets</h3>
            <p className="text-gray-600 mb-4">Create, update, or delete movie tickets</p>
            <button className="btn-primary">Create Ticket</button>
          </div>
          
          <div className="card">
            <h3 className="text-xl font-semibold text-gray-900 mb-4">View Transactions</h3>
            <p className="text-gray-600 mb-4">See all customer transactions</p>
            <button className="btn-primary">View All Transactions</button>
          </div>
        </div>
      </div>
    </div>
  )
}
