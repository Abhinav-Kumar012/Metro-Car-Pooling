'use client'

import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'

interface RideCompletionModalProps {
  isOpen: boolean
  message: string
  onClose: () => void
}

export function RideCompletionModal({ isOpen, message, onClose }: RideCompletionModalProps) {
  return (
    <Dialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="text-2xl flex items-center gap-2 text-green-600">
            ✅ Ride Completed!
          </DialogTitle>
          <DialogDescription>
            You have arrived at your destination.
          </DialogDescription>
        </DialogHeader>

        <Card className="p-6 bg-gradient-to-br from-green-50 to-blue-50 border-green-200">
          <div className="text-center space-y-4">
            <div className="text-6xl animate-bounce">🏁</div>
            <p className="text-lg font-medium text-gray-800">
              {message}
            </p>
            <p className="text-sm text-muted-foreground">
              Thank you for riding with us!
            </p>
          </div>
        </Card>

        <DialogFooter className="sm:justify-center">
          <Button
            onClick={onClose}
            className="w-full sm:w-auto bg-green-600 hover:bg-green-700 text-white"
            size="lg"
          >
            Book Another Ride
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}