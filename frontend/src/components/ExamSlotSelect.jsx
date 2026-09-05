export default function ExamSlotSelect({ examSlots }) {
  const { exams, examsError, examId, setExamId, slots, slotsError, slotId, setSlotId } = examSlots

  return (
    <div>
      <label>
        <span>Exam</span>
        <select value={examId} onChange={(e) => setExamId(e.target.value)}>
          <option value="">-- select an exam --</option>
          {exams.map((exam) => (
            <option key={exam.id} value={exam.id}>
              {exam.name} ({exam.id})
            </option>
          ))}
        </select>
      </label>
      {examsError && <ResultBoxInline message={examsError} />}

      <label>
        <span>Exam slot</span>
        <select value={slotId} onChange={(e) => setSlotId(e.target.value)} disabled={!examId}>
          <option value="">-- select a slot --</option>
          {slots.map((slot) => (
            <option key={slot.id} value={slot.id}>
              {slot.date} {slot.startTime}-{slot.endTime} ({slot.id})
            </option>
          ))}
        </select>
      </label>
      {slotsError && <ResultBoxInline message={slotsError} />}
    </div>
  )
}

function ResultBoxInline({ message }) {
  return <div className="result-box error">{message}</div>
}
