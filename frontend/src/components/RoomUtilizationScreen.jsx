import { useEffect, useState } from 'react'
import { getRoomUtilization } from '../api/api.js'
import { useExamSlots } from '../hooks/useExamSlots.js'
import ExamSlotSelect from './ExamSlotSelect.jsx'
import ResultBox from './ResultBox.jsx'

export default function RoomUtilizationScreen() {
  const examSlots = useExamSlots()
  const { slotId } = examSlots

  const [rows, setRows] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  function load() {
    if (!slotId) {
      setRows([])
      return
    }
    setError('')
    setLoading(true)
    getRoomUtilization(slotId)
      .then(setRows)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }

  useEffect(load, [slotId])

  return (
    <section>
      <h1>Room Utilization</h1>
      <fieldset>
        <legend>Slot</legend>
        <ExamSlotSelect examSlots={examSlots} />
      </fieldset>

      <button type="button" onClick={load} disabled={!slotId || loading}>
        {loading ? 'Refreshing...' : 'Refresh'}
      </button>

      {error && <ResultBox kind="error" message={error} />}

      {slotId && (
        <table>
          <thead>
            <tr>
              <th>Room ID</th>
              <th>Capacity</th>
              <th>Filled</th>
              <th>Available</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((row) => (
              <tr key={row.proctoringRoomId}>
                <td>{row.proctoringRoomId}</td>
                <td>{row.capacity}</td>
                <td>{row.filled}</td>
                <td>{row.available}</td>
              </tr>
            ))}
            {rows.length === 0 && (
              <tr>
                <td colSpan="4">No rooms in this slot.</td>
              </tr>
            )}
          </tbody>
        </table>
      )}
    </section>
  )
}
