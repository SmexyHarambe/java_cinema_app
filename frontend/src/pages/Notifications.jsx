import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { notificationAPI, customerAPI } from '../services/api'
import { useAuthStore } from '../store/authStore'
import { ArrowLeft, Trash2 } from 'lucide-react'

export default function Notifications() {
  const navigate = useNavigate()
  const logout = useAuthStore((state) => state.logout)
  const user = useAuthStore((state) => state.user)
  
  const [notifications, setNotifications] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    fetchNotifications()
  }, [])

  const fetchNotifications = async () => {
    try {
      const response = await notificationAPI.getUnread()
      setNotifications(response.data)
    } catch (err) {
      console.error('Error fetching notifications:', err)
      setError('Failed to load notifications')
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (notificationId) => {
    try {
      await notificationAPI.delete(notificationId)
      setNotifications(prev => prev.filter(n => n.id !== notificationId))
    } catch (err) {
      console.error('Error deleting notification', err)
      setError('Failed to delete notification')
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
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4">
        <button
          onClick={() => window.history.back()}
          className="flex items-center text-gray-600 hover:text-primary-600 mb-6"
        >
          <ArrowLeft className="h-5 w-5 mr-2" />
          Back to Dashboard
        </button>

        <h1 className="text-2xl font-bold text-gray-900 mb-6">Notifications</h1>

        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-6">
            {error}
          </div>
        )}

        <div className="space-y-4">
          {notifications.length === 0 ? (
            <div className="text-center py-12 bg-white rounded-lg">
              <p className="text-gray-500">No notifications</p>
            </div>
          ) : (
            notifications.map(notification => (
              <div key={notification.id} className="card flex items-center justify-between">
                <div>
                  <p className="text-gray-900">{notification.message}</p>
                  <p className="text-sm text-gray-500 mt-1">
                    {new Date(notification.createdAt).toLocaleString()}
                  </p>
                </div>
                
                <button
                  onClick={() => handleDelete(notification.id)}
                  className="p-2 text-red-600 hover:text-red-800 hover:bg-red-50 rounded-lg"
                >
                  <Trash2 className="h-5 w-5" />
                </button>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  )
}
