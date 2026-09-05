// Purpose: "Check In" screen — submits an admit ticket id for check-in and
//          shows success or the exact backend error (invalid / already-used).
// Role: Calls POST /api/admit-tickets/{ticketId}/check-in.
import { useState } from 'react'
import { checkInTicket } from '../api/api.js'
import ResultBox from './ResultBox.jsx'

export default function CheckInScreen() {
  const [ticketId, setTicketId] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [result, setResult] = useState(null)

  async function handleSubmit(e) {
    e.preventDefault()
    setResult(null)
    setSubmitting(true)
    try {
      const ticket = await checkInTicket(ticketId)
      setResult({
        kind: 'success',
        message:
          `Checked in.\n` +
          `Ticket id: ${ticket.id}\n` +
          `Student id: ${ticket.studentId}\n` +
          `Exam id: ${ticket.examId}\n` +
          `Exam slot id: ${ticket.examSlotId}\n` +
          `Room id: ${ticket.proctoringRoomId}\n` +
          `Registration id: ${ticket.registrationId}\n` +
          `Used: ${ticket.used}`,
      })
    } catch (err) {
      setResult({ kind: 'error', message: err.message })
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section>
      <h1>Check In</h1>
      <form onSubmit={handleSubmit}>
        <fieldset>
          <legend>Admit ticket</legend>
          <label>
            <span>Ticket ID</span>
            <input value={ticketId} onChange={(e) => setTicketId(e.target.value)} required />
          </label>
        </fieldset>
        <button type="submit" disabled={submitting}>
          {submitting ? 'Checking in...' : 'Check in'}
        </button>
      </form>

      {result && <ResultBox kind={result.kind} message={result.message} />}
    </section>
  )
}
