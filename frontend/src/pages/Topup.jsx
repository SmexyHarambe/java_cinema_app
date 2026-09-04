import { useState } from 'react'
import { customerAPI } from '../services/api'
import { useAuthStore } from '../store/authStore'
import { CreditCard, ArrowLeft } from 'lucide-react'

export default function Topup() {
  const updateBalance = useAuthStore((state) => state.updateBalance)
  const balance = useAuthStore((state) => state.balance)
  
  const [amount, setAmount] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSuccess(false)
    
    const topupAmount = parseFloat(amount)
    if (isNaN(topupAmount) || topupAmount <= 0) {
      setError('Please enter a valid amount')
      return
    }

    setLoading(true)

    try {
      const response = await customerAPI.topup(topupAmount)
      updateBalance(response.data)
      setSuccess(true)
      setAmount('')
    } catch (err) {
      setError(err.response?.data?.message || 'Topup failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-md mx-auto px-4">
        <button
          onClick={() => window.history.back()}
          className="flex items-center text-gray-600 hover:text-primary-600 mb-6"
        >
          <ArrowLeft className="h-5 w-5 mr-2" />
          Back to Dashboard
        </button>

        <div className="card">
          <h1 className="text-2xl font-bold text-gray-900 mb-6">Topup Saldo</h1>
          
          <div className="bg-primary-50 border border-primary-200 rounded-lg p-4 mb-6">
            <div className="flex items-center">
              <CreditCard className="h-6 w-6 text-primary-600 mr-3" />
              <div>
                <p className="text-sm text-primary-600">Current Balance</p>
                <p className="text-2xl font-bold text-primary-700">
                  Rp {balance.toLocaleString('id-ID')}
                </p>
              </div>
            </div>
          </div>

          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-6">
              {error}
            </div>
          )}

          {success && (
            <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-6">
              Topup successful! Your balance has been updated.
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Amount (Rp)
              </label>
              <input
                type="number"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="input-field"
                placeholder="Enter amount"
                min="1"
                required
              />
            </div>

            <div className="grid grid-cols-2 gap-3 mb-6">
              {[50000, 100000, 200000, 500000].map((amount) => (
                <button
                  key={amount}
                  type="button"
                  onClick={() => setAmount(amount.toString())}
                  className="px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg text-sm transition-colors"
                >
                  Rp {amount.toLocaleString('id-ID')}
                </button>
              ))}
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full btn-primary py-3 text-lg"
            >
              {loading ? 'Processing...' : 'Topup Now'}
            </button>
          </form>
        </div>
      </div>
    </div>
  )
}
