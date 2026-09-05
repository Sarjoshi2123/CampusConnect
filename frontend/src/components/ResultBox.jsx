export default function ResultBox({ kind, message }) {
  if (!message) {
    return null
  }
  return <div className={`result-box ${kind}`}>{message}</div>
}
