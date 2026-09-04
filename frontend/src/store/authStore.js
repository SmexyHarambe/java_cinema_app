import { create } from 'zustand'
import { persist } from 'zustand/middleware'

export const useAuthStore = create(
  persist(
    (set) => ({
      user: null,
      token: null,
      balance: 0,
      customerId: null,

      login: (userData) => set({
        user: {
          userId: userData.userId,
          username: userData.username,
          role: userData.role,
        },
        token: userData.token,
        balance: userData.balance || 0,
        customerId: userData.customerId,
      }),

      logout: () => set({
        user: null,
        token: null,
        balance: 0,
        customerId: null,
      }),

      updateBalance: (newBalance) => set({ balance: newBalance }),
    }),
    {
      name: 'auth-storage',
    }
  )
)
