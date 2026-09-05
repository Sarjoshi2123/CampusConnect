// Purpose: "Register a Student" screen — registers a student into an exam slot
//          and shows the returned admit ticket details, or the exact backend
//          error (conflict, room full, student/slot not found, etc.).
// Role: Calls POST /api/registrations. Also includes a small inline
//       "create new student" mini-form (POST /api/students) so the screen is
//       usable end-to-end without needing a separate student-creation screen —
//       there is no student-listing endpoint, so a student id must either be
//       already known or created here first.
import { useState } from 'react'
import { createStudent, registerStudent } from '../api/api.js'
import { useExamSlots } from '../hooks/useExamSlots.js'
import ExamSlotSelect from './ExamSlotSelect.jsx'
import ResultBox from './ResultBox.jsx'

export default function RegisterStudentScreen() {
  const examSlots = useExamSlots()
  const { slotId, exams, slots } = examSlots

  const [studentId, setStudentId] = useState('')
  const [newStudentName, setNewStudentName] = useState('')
  const [creatingStudent, setCreatingStudent] = useState(false)
  const [createStudentResult, setCreateStudentResult] = useState(null)

  const [submitting, setSubmitting] = useState(false)
  const [result, setResult] = useState(null)

  async function handleCreateStudent(e) {
    e.preventDefault()
    setCreateStudentResult(null)
    setCreatingStudent(true)
    try {
      const student = await createStudent(newStudentName)
      setStudentId(student.id)
      setCreateStudentResult({
        kind: 'success',
        message: `Created student "${student.name}" (id: ${student.id}) — filled into Student ID below.`,
      })
      setNewStudentName('')
    } catch (err) {
      setCreateStudentResult({ kind: 'error', message: err.message })
    } finally {
      setCreatingStudent(false)
    }
  }

  async function handleRegister(e) {
    e.preventDefault()
    setResult(null)
    setSubmitting(true)
    try {
      const registration = await registerStudent(studentId, slotId)
      const examName = exams.find((x) => x.id === registration.examId)?.name || registration.examId
      const slot = slots.find((s) => s.id === registration.examSlotId)
      const slotLabel = slot ? `${slot.date} ${slot.startTime}-${slot.endTime}` : registration.examSlotId
      setResult({
        kind: 'success',
        message:
          `Registered.\n` +
          `Registration id: ${registration.id} (needed for Reschedule later)\n` +
          `Student id: ${registration.studentId}\n` +
          `Exam: ${examName}\n` +
          `Slot: ${slotLabel}\n` +
          `Room id: ${registration.proctoringRoomId}\n` +
          `Admit ticket id: ${registration.admitTicketId}\n` +
          `Status: ${registration.status}`,
      })
    } catch (err) {
      setResult({ kind: 'error', message: err.message })
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section>
      <h1>Register a Student</h1>

      <fieldset>
        <legend>New student? Create one</legend>
        <form onSubmit={handleCreateStudent}>
          <label>
            <span>Student name</span>
            <input value={newStudentName} onChange={(e) => setNewStudentName(e.target.value)} required />
          </label>
          <button type="submit" disabled={creatingStudent}>
            {creatingStudent ? 'Creating...' : 'Create student'}
          </button>
        </form>
        {createStudentResult && <ResultBox kind={createStudentResult.kind} message={createStudentResult.message} />}
      </fieldset>

      <form onSubmit={handleRegister}>
        <fieldset>
          <legend>Target slot</legend>
          <ExamSlotSelect examSlots={examSlots} />
        </fieldset>

        <fieldset>
          <legend>Student</legend>
          <label>
            <span>Student ID</span>
            <input value={studentId} onChange={(e) => setStudentId(e.target.value)} required />
          </label>
        </fieldset>

        <button type="submit" disabled={submitting || !slotId}>
          {submitting ? 'Registering...' : 'Register'}
        </button>
      </form>

      {result && <ResultBox kind={result.kind} message={result.message} />}
    </section>
  )
}
