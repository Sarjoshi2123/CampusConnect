const SCREENS = [
  { key: 'create-exam', label: 'Create Exam' },
  { key: 'create-rooms', label: 'Create Rooms' },
  { key: 'register', label: 'Register Student' },
  { key: 'reschedule', label: 'Reschedule' },
  { key: 'check-in', label: 'Check In' },
  { key: 'utilization', label: 'Room Utilization' },
  { key: 'no-show', label: 'Mark No-Show' },
]

export default function Nav({ active, onSelect }) {
  return (
    <nav className="app-nav">
      {SCREENS.map((screen) => (
        <button
          key={screen.key}
          className={active === screen.key ? 'active' : ''}
          onClick={() => onSelect(screen.key)}
        >
          {screen.label}
        </button>
      ))}
    </nav>
  )
}
