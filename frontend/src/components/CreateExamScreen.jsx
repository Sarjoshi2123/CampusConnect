import { useState } from 'react'
import { createExam, createExamSlot } from '../api/api.js'
import ResultBox from './ResultBox.jsx'

function emptyRow() {
  return { date: '', startTime: '', durationMinutes: '' }
}

export default function CreateExamScreen() {
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [slotRows, setSlotRows] = useState([emptyRow()])
  const [submitting, setSubmitting] = useState(false)
  const [result, setResult] = useState(null)

  function updateRow(index, field, value) {
    setSlotRows((rows) => rows.map((row, i) => (i === index ? { ...row, [field]: value } : row)))
  }

  function addRow() {
    setSlotRows((rows) => [...rows, emptyRow()])
  }

  function removeRow(index) {
    setSlotRows((rows) => rows.filter((_, i) => i !== index))
  }

  function completeRows() {
    return slotRows.filter((row) => row.date && row.startTime && row.durationMinutes)
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setResult(null)

    const rowsToCreate = completeRows()
    if (rowsToCreate.length === 0) {
      setResult({ kind: 'error', message: 'At least one complete slot (date, start time, duration) is required.' })
      return
    }

    setSubmitting(true)
    const createdSlots = []
    try {
      const exam = await createExam(name, description)
      for (const row of rowsToCreate) {
        const slot = await createExamSlot(exam.id, {
          date: row.date,
          startTime: row.startTime,
          durationMinutes: Number(row.durationMinutes),
        })
        createdSlots.push(slot)
      }

      const slotLines = createdSlots
        .map((s) => `  - ${s.date} ${s.startTime}-${s.endTime} (slot id: ${s.id})`)
        .join('\n')
      setResult({
        kind: 'success',
        message: `Exam created: "${exam.name}" (exam id: ${exam.id})\nSlots created:\n${slotLines}`,
      })
      setName('')
      setDescription('')
      setSlotRows([emptyRow()])
    } catch (err) {
      const createdSoFar = createdSlots.length
        ? `\n(${createdSlots.length} slot(s) were already created before this error: ${createdSlots
            .map((s) => s.id)
            .join(', ')})`
        : ''
      setResult({ kind: 'error', message: err.message + createdSoFar })
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section>
      <h1>Create Exam</h1>
      <form onSubmit={handleSubmit}>
        <fieldset>
          <legend>Exam details</legend>
          <label>
            <span>Name</span>
            <input value={name} onChange={(e) => setName(e.target.value)} required />
          </label>
          <label>
            <span>Description</span>
            <input value={description} onChange={(e) => setDescription(e.target.value)} />
          </label>
        </fieldset>

        <fieldset>
          <legend>Slots</legend>
          {slotRows.map((row, index) => (
            <div className="slot-row" key={index}>
              <label>
                <span>Date</span>
                <input
                  type="date"
                  value={row.date}
                  onChange={(e) => updateRow(index, 'date', e.target.value)}
                />
              </label>
              <label>
                <span>Start time</span>
                <input
                  type="time"
                  value={row.startTime}
                  onChange={(e) => updateRow(index, 'startTime', e.target.value)}
                />
              </label>
              <label>
                <span>Duration (minutes)</span>
                <input
                  type="number"
                  min="1"
                  value={row.durationMinutes}
                  onChange={(e) => updateRow(index, 'durationMinutes', e.target.value)}
                />
              </label>
              {slotRows.length > 1 && (
                <button type="button" onClick={() => removeRow(index)}>
                  Remove
                </button>
              )}
            </div>
          ))}
          <button type="button" onClick={addRow}>
            Add another slot
          </button>
        </fieldset>

        <button type="submit" disabled={submitting}>
          {submitting ? 'Creating...' : 'Create exam'}
        </button>
      </form>

      {result && <ResultBox kind={result.kind} message={result.message} />}
    </section>
  )
}
