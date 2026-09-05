// Purpose: Shared "pick an exam, then pick one of its slots" state and data
//          loading, reused by every screen that needs an exam/slot selector
//          (Create Rooms, Register, Room Utilization, No-Show).
// Role: Custom hook. Loads the exam list once on mount, then reloads the slot
//       list whenever the selected exam changes (resetting the slot selection).
//       Keeps this fetch-on-change logic in exactly one place instead of
//       duplicating it across every screen component.
import { useEffect, useState } from 'react'
import { listExams, listSlotsForExam } from '../api/api.js'

export function useExamSlots() {
  const [exams, setExams] = useState([])
  const [examsError, setExamsError] = useState('')
  const [examId, setExamId] = useState('')

  const [slots, setSlots] = useState([])
  const [slotsError, setSlotsError] = useState('')
  const [slotId, setSlotId] = useState('')

  useEffect(() => {
    listExams()
      .then(setExams)
      .catch((err) => setExamsError(err.message))
  }, [])

  useEffect(() => {
    setSlotId('')
    setSlotsError('')
    if (!examId) {
      setSlots([])
      return
    }
    listSlotsForExam(examId)
      .then(setSlots)
      .catch((err) => setSlotsError(err.message))
  }, [examId])

  return {
    exams,
    examsError,
    examId,
    setExamId,
    slots,
    slotsError,
    slotId,
    setSlotId,
  }
}
