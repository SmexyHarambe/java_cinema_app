import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ticketAPI, customerAPI } from '../services/api'
import { useAuthStore } from '../store/authStore'
import { Calendar, Film, CreditCard, Bell, User, LogOut } from 'lucide-react'

export default function CustomerDashboard() {
  const navigate = useNavigate()
  const logout = useAuthStore((state) => state.logout)
  const user = useAuthStore((state) => state.user)
  const balance = useAuthStore((state) => state.balance)
  const updateBalance = useAuthStore((state) => state.updateBalance)

  const handleLogout = () => {
    logout()
    navigate('/login')
  }
  
  const [tickets, setTickets] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    fetchTickets()
    fetchBalance()
  }, [])

  const fetchTickets = async () => {
    try {
      const response = await ticketAPI.getAll()
      setTickets(response.data)
    } catch (err) {
      setError('Failed to load tickets')
    } finally {
      setLoading(false)
    }
  }

  const fetchBalance = async () => {
    try {
      const response = await customerAPI.getBalance()
      updateBalance(response.data)
    } catch (err) {
      console.error('Failed to fetch balance', err)
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navbar */}
      <nav className="bg-white shadow-md">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <Film className="h-8 w-8 text-primary-600" />
              <span className="ml-2 text-xl font-bold text-gray-900">UAS Disprog</span>
            </div>
            
            <div className="flex items-center space-x-4">
              <button 
                onClick={handleLogout}
                className="flex items-center px-4 py-2 text-red-600 hover:text-red-800 hover:bg-red-50 rounded-lg transition-colors"
              >
                <LogOut className="h-5 w-5 mr-2" />
                <span className="text-sm font-medium">Logout</span>
              </button>
              
              <div className="flex items-center px-4 py-2 bg-primary-50 rounded-lg">
                <span className="text-primary-700 font-semibold mr-2">Saldo:</span>
                <span className="text-primary-600 font-bold">Rp {balance.toLocaleString('id-ID')}</span>
              </div>
              
              <button 
                onClick={() => navigate('/customer/notifications')}
                className="p-2 relative text-gray-600 hover:text-primary-600"
              >
                <Bell className="h-6 w-6" />
                <span className="absolute top-0 right-0 block h-2 w-2 rounded-full bg-red-500 ring-2 ring-white"></span>
              </button>
              
              <button 
                onClick={() => navigate('/customer/topup')}
                className="p-2 text-gray-600 hover:text-primary-600"
              >
                <CreditCard className="h-6 w-6" />
              </button>
              
              <div className="flex items-center px-4 py-2 bg-gray-100 rounded-lg">
                <User className="h-5 w-5 text-gray-600 mr-2" />
                <span className="text-sm font-medium text-gray-700">{user?.username}</span>
              </div>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-8">Available Tickets</h1>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-6">
            {error}
          </div>
        )}

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {tickets.map(ticket => (
            <div key={ticket.id} className="card hover:shadow-lg transition-shadow">
              <div className="relative h-48 mb-4">
                <img
                  src={ticket.imagePath ? `/api/public/images/${ticket.imagePath}` : '/placeholder-movie.jpg'}
                  alt={ticket.judul}
                  className="w-full h-full object-cover rounded-lg"
                />
              </div>
              
              <h3 className="text-xl font-bold text-gray-900 mb-2">{ticket.judul}</h3>
              <div className="flex items-center text-sm text-gray-600 mb-2">
                <span className="bg-primary-100 text-primary-800 px-2 py-1 rounded text-xs mr-2">
                  {ticket.genre}
                </span>
                <span>{ticket.durasi} menit</span>
              </div>
              
              <p className="text-gray-600 mb-4 line-clamp-2">{ticket.deskripsi}</p>
              
              <div className="flex justify-between items-center mb-4">
                <div>
                  <span className="text-2xl font-bold text-primary-600">
                    Rp {ticket.price.toLocaleString('id-ID')}
                  </span>
                </div>
                
                {ticket.isFlashSale && (
                  <span className="bg-red-100 text-red-800 px-2 py-1 rounded text-xs">
                    Flash Sale
                  </span>
                )}
              </div>
              
              <button
                onClick={() => navigate(`/customer/tickets/${ticket.id}`)}
                className="w-full btn-primary"
              >
                Book Now
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
