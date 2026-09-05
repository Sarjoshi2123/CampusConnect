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
