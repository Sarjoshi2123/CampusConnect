export class ApiError extends Error {
  constructor(message, status) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}

async function request(path, options = {}) {
  const response = await fetch(path, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
  })

  if (response.status === 204) {
    return null
  }

  const text = await response.text()
  const body = text ? JSON.parse(text) : null

  if (!response.ok) {
    const message =
      (body && (body.message || body.detail || body.title)) ||
      `Request failed with status ${response.status}`
    throw new ApiError(message, response.status)
  }

  return body
}

function postJson(path, payload) {
  return request(path, { method: 'POST', body: JSON.stringify(payload) })
}

function getJson(path) {
  return request(path, { method: 'GET' })
}

export function createStudent(name) {
  return postJson('/api/students', { name })
}

export function getStudent(studentId) {
  return getJson(`/api/students/${encodeURIComponent(studentId)}`)
}

export function createExam(name, description) {
  return postJson('/api/exams', { name, description })
}

export function listExams() {
  return getJson('/api/exams')
}

export function getExam(examId) {
  return getJson(`/api/exams/${encodeURIComponent(examId)}`)
}

export function createExamSlot(examId, { date, startTime, durationMinutes }) {
  return postJson(`/api/exams/${encodeURIComponent(examId)}/slots`, {
    date,
    startTime,
    durationMinutes,
  })
}

export function listSlotsForExam(examId) {
  return getJson(`/api/exams/${encodeURIComponent(examId)}/slots`)
}

export function getSlot(slotId) {
  return getJson(`/api/slots/${encodeURIComponent(slotId)}`)
}

export function createRoom(slotId, capacity) {
  return postJson(`/api/slots/${encodeURIComponent(slotId)}/rooms`, { capacity })
}

export function listRoomsForSlot(slotId) {
  return getJson(`/api/slots/${encodeURIComponent(slotId)}/rooms`)
}

export function getRoomUtilization(slotId) {
  return getJson(`/api/slots/${encodeURIComponent(slotId)}/utilization`)
}

export function processNoShows(slotId) {
  return postJson(`/api/slots/${encodeURIComponent(slotId)}/no-shows/process`, {})
}

export function registerStudent(studentId, examSlotId) {
  return postJson('/api/registrations', { studentId, examSlotId })
}

export function rescheduleRegistration(registrationId, newExamSlotId) {
  return postJson(`/api/registrations/${encodeURIComponent(registrationId)}/reschedule`, {
    newExamSlotId,
  })
}

export function getRegistration(registrationId) {
  return getJson(`/api/registrations/${encodeURIComponent(registrationId)}`)
}

export function getAdmitTicket(ticketId) {
  return getJson(`/api/admit-tickets/${encodeURIComponent(ticketId)}`)
}

export function checkInTicket(ticketId) {
  return postJson(`/api/admit-tickets/${encodeURIComponent(ticketId)}/check-in`, {})
}
