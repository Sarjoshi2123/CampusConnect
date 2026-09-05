import { useState } from 'react'
import { processNoShows } from '../api/api.js'
import { useExamSlots } from '../hooks/useExamSlots.js'
import ExamSlotSelect from './ExamSlotSelect.jsx'
import ResultBox from './ResultBox.jsx'

export default function NoShowScreen() {
  const examSlots = useExamSlots()
  const { slotId } = examSlots

  const [submitting, setSubmitting] = useState(false)
  const [result, setResult] = useState(null)

  async function handleTrigger() {
    setResult(null)
    setSubmitting(true)
    try {
      const response = await processNoShows(slotId)
      setResult({
        kind: 'success',
        message: `Marked ${response.markedNoShow} registration(s) as NO_SHOW for this slot.`,
      })
    } catch (err) {
      setResult({ kind: 'error', message: err.message })
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section>
      <h1>Mark No-Show</h1>
      <p>
        This is a no-op if the slot's check-in window has not closed yet (i.e. before the slot's end
        time) — the backend also runs this sweep automatically on a schedule.
      </p>
      <fieldset>
        <legend>Slot</legend>
        <ExamSlotSelect examSlots={examSlots} />
      </fieldset>

      <button type="button" onClick={handleTrigger} disabled={!slotId || submitting}>
        {submitting ? 'Processing...' : 'Mark no-shows for this slot'}
      </button>

      {result && <ResultBox kind={result.kind} message={result.message} />}
    </section>
  )
}
