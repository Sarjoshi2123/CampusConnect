import { useEffect, useState } from 'react'
import { createRoom, listRoomsForSlot } from '../api/api.js'
import { useExamSlots } from '../hooks/useExamSlots.js'
import ExamSlotSelect from './ExamSlotSelect.jsx'
import ResultBox from './ResultBox.jsx'

export default function CreateRoomsScreen() {
  const examSlots = useExamSlots()
  const { slotId } = examSlots

  const [capacity, setCapacity] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [result, setResult] = useState(null)

  const [rooms, setRooms] = useState([])
  const [roomsError, setRoomsError] = useState('')

  function loadRooms(forSlotId) {
    if (!forSlotId) {
      setRooms([])
      return
    }
    listRoomsForSlot(forSlotId)
      .then(setRooms)
      .catch((err) => setRoomsError(err.message))
  }

  useEffect(() => {
    setRoomsError('')
    loadRooms(slotId)
    
  }, [slotId])

  async function handleSubmit(e) {
    e.preventDefault()
    setResult(null)
    setSubmitting(true)
    try {
      const room = await createRoom(slotId, Number(capacity))
      setResult({ kind: 'success', message: `Room created: id ${room.id}, capacity ${room.capacity}` })
      setCapacity('')
      loadRooms(slotId)
    } catch (err) {
      setResult({ kind: 'error', message: err.message })
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section>
      <h1>Create Proctoring Rooms</h1>
      <form onSubmit={handleSubmit}>
        <fieldset>
          <legend>Target slot</legend>
          <ExamSlotSelect examSlots={examSlots} />
        </fieldset>

        <fieldset>
          <legend>Room</legend>
          <label>
            <span>Max capacity</span>
            <input
              type="number"
              min="1"
              value={capacity}
              onChange={(e) => setCapacity(e.target.value)}
              required
            />
          </label>
        </fieldset>

        <button type="submit" disabled={submitting || !slotId}>
          {submitting ? 'Creating...' : 'Create room'}
        </button>
      </form>

      {result && <ResultBox kind={result.kind} message={result.message} />}

      {slotId && (
        <>
          <h2>Rooms in this slot</h2>
          {roomsError && <ResultBox kind="error" message={roomsError} />}
          <table>
            <thead>
              <tr>
                <th>Room ID</th>
                <th>Capacity</th>
                <th>Occupied</th>
                <th>Available</th>
              </tr>
            </thead>
            <tbody>
              {rooms.map((room) => (
                <tr key={room.id}>
                  <td>{room.id}</td>
                  <td>{room.capacity}</td>
                  <td>{room.currentOccupancy}</td>
                  <td>{room.availableCapacity}</td>
                </tr>
              ))}
              {rooms.length === 0 && (
                <tr>
                  <td colSpan="4">No rooms yet.</td>
                </tr>
              )}
            </tbody>
          </table>
        </>
      )}
    </section>
  )
}
