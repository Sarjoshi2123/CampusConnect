import { useState } from 'react'
import { getExam, getRegistration, listSlotsForExam, rescheduleRegistration } from '../api/api.js'
import ResultBox from './ResultBox.jsx'

export default function RescheduleScreen() {
  const [registrationId, setRegistrationId] = useState('')
  const [lookingUp, setLookingUp] = useState(false)
  const [lookupError, setLookupError] = useState('')

  const [registration, setRegistration] = useState(null)
  const [exam, setExam] = useState(null)
  const [slots, setSlots] = useState([])
  const [newSlotId, setNewSlotId] = useState('')

  const [submitting, setSubmitting] = useState(false)
  const [result, setResult] = useState(null)

  async function handleLookup(e) {
    e.preventDefault()
    setLookupError('')
    setRegistration(null)
    setExam(null)
    setSlots([])
    setNewSlotId('')
    setResult(null)
    setLookingUp(true)
    try {
      const reg = await getRegistration(registrationId)
      const examData = await getExam(reg.examId)
      const slotData = await listSlotsForExam(reg.examId)
      setRegistration(reg)
      setExam(examData)
      setSlots(slotData.filter((s) => s.id !== reg.examSlotId))
    } catch (err) {
      setLookupError(err.message)
    } finally {
      setLookingUp(false)
    }
  }

  async function handleReschedule(e) {
    e.preventDefault()
    setResult(null)
    setSubmitting(true)
    try {
      const updated = await rescheduleRegistration(registration.id, newSlotId)
      const slot = slots.find((s) => s.id === updated.examSlotId)
      const slotLabel = slot ? `${slot.date} ${slot.startTime}-${slot.endTime}` : updated.examSlotId
      setResult({
        kind: 'success',
        message:
          `Rescheduled.\n` +
          `Student id: ${updated.studentId}\n` +
          `Exam: ${exam?.name ?? updated.examId}\n` +
          `New slot: ${slotLabel}\n` +
          `Room id: ${updated.proctoringRoomId}\n` +
          `New admit ticket id: ${updated.admitTicketId}\n` +
          `Status: ${updated.status}`,
      })
    } catch (err) {
      setResult({ kind: 'error', message: err.message })
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section>
      <h1>Reschedule</h1>

      <form onSubmit={handleLookup}>
        <fieldset>
          <legend>Registration to reschedule</legend>
          <label>
            <span>Registration ID</span>
            <input value={registrationId} onChange={(e) => setRegistrationId(e.target.value)} required />
          </label>
          <button type="submit" disabled={lookingUp}>
            {lookingUp ? 'Looking up...' : 'Look up'}
          </button>
        </fieldset>
      </form>
      {lookupError && <ResultBox kind="error" message={lookupError} />}

      {registration && (
        <form onSubmit={handleReschedule}>
          <fieldset>
            <legend>Current registration</legend>
            <p>
              Student: {registration.studentId}
              <br />
              Exam: {exam?.name ?? registration.examId}
              <br />
              Current slot id: {registration.examSlotId}
              <br />
              Status: {registration.status}
            </p>
          </fieldset>

          <fieldset>
            <legend>New slot (same exam)</legend>
            <label>
              <span>Slot</span>
              <select value={newSlotId} onChange={(e) => setNewSlotId(e.target.value)} required>
                <option value="">-- select a slot --</option>
                {slots.map((slot) => (
                  <option key={slot.id} value={slot.id}>
                    {slot.date} {slot.startTime}-{slot.endTime} ({slot.id})
                  </option>
                ))}
              </select>
            </label>
            {slots.length === 0 && <p>No other slots exist for this exam yet.</p>}
          </fieldset>

          <button type="submit" disabled={submitting || !newSlotId}>
            {submitting ? 'Rescheduling...' : 'Reschedule'}
          </button>
        </form>
      )}

      {result && <ResultBox kind={result.kind} message={result.message} />}
    </section>
  )
}
