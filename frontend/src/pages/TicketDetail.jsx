import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ticketAPI, customerAPI } from '../services/api'
import { useAuthStore } from '../store/authStore'
import { ArrowLeft, Clock, Calendar, User as UserIcon } from 'lucide-react'

export default function TicketDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const balance = useAuthStore((state) => state.balance)
  const updateBalance = useAuthStore((state) => state.updateBalance)
  
  const [ticket, setTicket] = useState(null)
  const [selectedSeats, setSelectedSeats] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const seats = Array.from({ length: 30 }, (_, i) => `A${i + 1}`)

  useEffect(() => {
    fetchTicket()
  }, [id])

  const fetchTicket = async () => {
    try {
      const response = await ticketAPI.getById(id)
      setTicket(response.data)
    } catch (err) {
      setError('Failed to load ticket')
    } finally {
      setLoading(false)
    }
  }

  const handleSeatClick = (seat) => {
    setSelectedSeats(prev => 
      prev.includes(seat) 
        ? prev.filter(s => s !== seat)
        : [...prev, seat]
    )
  }

  const handleBooking = async () => {
    if (selectedSeats.length === 0) {
      alert('Please select at least one seat')
      return
    }

    const total = ticket.price * selectedSeats.length

    if (balance < total) {
      alert('Insufficient balance. Please topup first.')
      return
    }

    setSubmitting(true)
    setError('')

    try {
      await customerAPI.createTransaction({
        ticketId: ticket.id,
        seats: selectedSeats,
        total: total
      })

      const balanceResponse = await customerAPI.getBalance()
      updateBalance(balanceResponse.data)

      alert('Booking successful!')
      navigate('/customer/dashboard')
    } catch (err) {
      setError(err.response?.data?.message || 'Booking failed')
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    )
  }

  if (!ticket) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <p className="text-gray-600">Ticket not found</p>
      </div>
    )
  }

  const total = ticket.price * selectedSeats.length

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-6xl mx-auto px-4">
        <button
          onClick={() => navigate('/customer/dashboard')}
          className="flex items-center text-gray-600 hover:text-primary-600 mb-6"
        >
          <ArrowLeft className="h-5 w-5 mr-2" />
          Back to Dashboard
        </button>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Ticket Info */}
          <div className="card">
            <img
              src={ticket.imagePath ? `/api/public/images/${ticket.imagePath}` : '/placeholder-movie.jpg'}
              alt={ticket.judul}
              className="w-full h-64 object-cover rounded-lg mb-6"
            />
            
            <h1 className="text-3xl font-bold text-gray-900 mb-4">{ticket.judul}</h1>
            
            <div className="space-y-3 mb-6">
              <div className="flex items-center text-gray-600">
                <span className="bg-primary-100 text-primary-800 px-3 py-1 rounded-full text-sm mr-3">
                  {ticket.genre}
                </span>
                <Clock className="h-5 w-5 mr-2" />
                <span>{ticket.durasi} minutes</span>
              </div>
              
              <div className="flex items-center text-gray-600">
                <UserIcon className="h-5 w-5 mr-2" />
                <span>Creator: {ticket.creator}</span>
              </div>
              
              <div className="flex items-center text-gray-600">
                <Calendar className="h-5 w-5 mr-2" />
                <span>{new Date(ticket.tanggalTayang).toLocaleString()}</span>
              </div>
            </div>

            <p className="text-gray-600 mb-6">{ticket.deskripsi}</p>

            <div className="border-t pt-4">
              <div className="flex justify-between items-center">
                <span className="text-lg text-gray-700">Price per ticket:</span>
                <span className="text-2xl font-bold text-primary-600">
                  Rp {ticket.price.toLocaleString('id-ID')}
                </span>
              </div>
            </div>
          </div>

          {/* Seat Selection */}
          <div className="card">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">Select Seats</h2>

            {error && (
              <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-6">
                {error}
              </div>
            )}

            <div className="mb-6">
              <div className="bg-gray-800 text-white text-center py-2 rounded-t-lg mb-4">
                SCREEN
              </div>
              
              <div className="grid grid-cols-6 gap-2 mb-6">
                {seats.map(seat => (
                  <button
                    key={seat}
                    onClick={() => handleSeatClick(seat)}
                    className={`
                      py-3 rounded-lg text-sm font-medium transition-colors
                      ${selectedSeats.includes(seat)
                        ? 'bg-primary-600 text-white'
                        : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                      }
                    `}
                  >
                    {seat}
                  </button>
                ))}
              </div>

              <div className="flex items-center justify-between text-sm mb-6">
                <div className="flex items-center">
                  <div className="w-6 h-6 bg-gray-200 rounded mr-2"></div>
                  <span>Available</span>
                </div>
                <div className="flex items-center">
                  <div className="w-6 h-6 bg-primary-600 rounded mr-2"></div>
                  <span>Selected</span>
                </div>
              </div>
            </div>

            <div className="border-t pt-6">
              <div className="space-y-3 mb-6">
                <div className="flex justify-between text-gray-700">
                  <span>Selected Seats:</span>
                  <span className="font-semibold">
                    {selectedSeats.length > 0 ? selectedSeats.join(', ') : '-'}
                  </span>
                </div>
                
                <div className="flex justify-between text-gray-700">
                  <span>Total Tickets:</span>
                  <span className="font-semibold">{selectedSeats.length}</span>
                </div>
                
                <div className="flex justify-between text-xl font-bold text-gray-900">
                  <span>Total:</span>
                  <span className="text-primary-600">
                    Rp {total.toLocaleString('id-ID')}
                  </span>
                </div>

                <div className="flex justify-between text-sm text-gray-600">
                  <span>Your Balance:</span>
                  <span className={balance >= total ? 'text-green-600' : 'text-red-600'}>
                    Rp {balance.toLocaleString('id-ID')}
                  </span>
                </div>
              </div>

              <button
                onClick={handleBooking}
                disabled={selectedSeats.length === 0 || submitting || balance < total}
                className="w-full btn-primary py-3 text-lg disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {submitting ? 'Processing...' : 'Confirm Booking'}
              </button>

              {balance < total && selectedSeats.length > 0 && (
                <p className="text-red-600 text-sm text-center mt-3">
                  Insufficient balance. Please topup first.
                </p>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
