'use client'

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { Card } from '@/components/ui/card'
import { DriverNav } from '@/components/driver-nav'
import { RideOfferForm } from '@/components/ride-offer-form'
import { NotificationCenter } from '@/components/notification-center'
import { apiRequest } from '@/lib/api-config'

export default function DriverPage() {
  const router = useRouter()
  const [authenticated, setAuthenticated] = useState(false)
  const [showNotifications, setShowNotifications] = useState(false)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    const token = localStorage.getItem('authToken')
    const role = localStorage.getItem('role')
    if (!token || role !== 'driver') {
      router.push('/auth?role=driver')
    } else {
      setAuthenticated(true)
    }
  }, [router])

  const handleLogout = () => {
    localStorage.removeItem('authToken')
    localStorage.removeItem('role')
    localStorage.removeItem('userId')
    router.push('/')
  }

  const handleSubmitOffer = async (offerData: any) => {
    setLoading(true)
    try {
      const driverId = localStorage.getItem('userId')
      
      const payload = {
        driverId: driverId ? parseInt(driverId) : 1,
        routeStations: offerData.routeStations,
        finalDestination: offerData.finalDestination,
        availableSeats: offerData.availableSeats
      }

      console.log('Submitting payload:', payload)

      const response = await apiRequest('/api/driver/driver-info', {
        method: 'POST',
        body: JSON.stringify(payload),
      })
      console.log(response)
      if (response && response.status) {
        alert('🎉 Ride offer posted successfully!')
      } else {
        alert('❌ Failed to post ride offer. Please try again.')
      }
    } catch (error: any) {
      console.error('Error submitting ride offer:', error)
      alert(`❌ Error: ${error.message || 'Failed to post ride offer'}`)
    } finally {
      setLoading(false)
    }
  }

  if (!authenticated) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin text-4xl mb-4">⚙️</div>
          <p className="text-muted-foreground">Loading...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-background">
      <DriverNav onLogout={handleLogout} />

      <main className="max-w-6xl mx-auto px-4 py-8">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold text-primary">Driver Dashboard</h1>
          <button
            onClick={() => setShowNotifications(!showNotifications)}
            className="relative p-2 rounded-lg hover:bg-muted transition-colors text-xl"
          >
            🔔
            <span className="absolute top-1 right-1 w-3 h-3 bg-destructive rounded-full"></span>
          </button>
        </div>

        {showNotifications && <NotificationCenter role="driver" />}

        {/* Content */}
        <div className="max-w-3xl mx-auto">
          <Card className="p-6">
            <h2 className="text-xl font-semibold mb-6">Post a Ride Offer</h2>
            {loading ? (
              <div className="text-center py-12">
                <div className="animate-spin text-4xl mb-4">🚗</div>
                <p className="text-muted-foreground">Posting your ride offer...</p>
              </div>
            ) : (
              <RideOfferForm onSubmit={handleSubmitOffer} />
            )}
          </Card>
        </div>
      </main>
    </div>
  )
}
