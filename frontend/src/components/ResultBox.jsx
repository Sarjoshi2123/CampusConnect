// Purpose: Renders the outcome of an API call — success or error — as plain
//          text taken directly from the backend response.
// Role: Reusable presentational component. Every screen that calls the API
//       feeds its result/error here instead of inventing its own message
//       display, so backend error text is never swallowed or replaced with a
//       generic message anywhere in the app.
export default function ResultBox({ kind, message }) {
  if (!message) {
    return null
  }
  return <div className={`result-box ${kind}`}>{message}</div>
}
