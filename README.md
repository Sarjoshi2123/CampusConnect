# CampusConnect

An exam scheduling and check-in system: exams with slots, proctoring rooms with
capacity, student registration with conflict/capacity checks, reschedule,
admit-ticket check-in, room utilization reporting, and no-show marking.

- `backend/` — Spring Boot 3.x (Java 17), REST API, fully in-memory storage (no database).
- `frontend/` — React + Vite (JavaScript), one plain screen per feature, no design system.

## Running backend + frontend together

You need **Java 17+**, **Maven 3.6+**, and **Node 18+ (with npm)** on your `PATH`.

**1. Start the backend** (from `backend/`):

```bash
cd backend
mvn spring-boot:run
```

This starts the API on `http://localhost:8080`. (Alternatively: `mvn package` then
`java -jar target/campusconnect-backend-0.0.1-SNAPSHOT.jar`.)

**2. Start the frontend** (from `frontend/`, in a second terminal):

```bash
cd frontend
npm install
npm run dev
```

This starts the dev server on `http://localhost:5173`. Open that URL in a
browser — the frontend calls `/api/...` and Vite's dev server proxies those
requests to the backend on port 8080 (see `frontend/vite.config.js`), so **no
CORS configuration is needed on the backend**.

**Running the tests:**

```bash
cd backend && mvn test
```

## Using the app

Storage is entirely in-memory on the backend, so all data (exams, slots,
rooms, students, registrations) is lost on backend restart — start fresh each
time you restart it. The screens must generally be used in this order, since
each one produces IDs the next one needs:

1. **Create Exam** — creates an exam with one or more slots; note the exam
   and slot IDs it prints.
2. **Create Rooms** — add a room (with capacity) to a slot.
3. **Register Student** — has an inline "create a new student" mini-form,
   since there's no separate student-creation screen or student-listing
   endpoint. Registering shows the returned admit ticket and, importantly,
   the **registration id** — copy it if you plan to reschedule later.
4. **Reschedule** — needs a registration id (from step 3) and shows a "new
   slot" dropdown restricted to slots of the *same* exam.
5. **Check In** — needs an admit ticket id (from step 3 or 4).
6. **Room Utilization** — pick a slot, see filled/available capacity per
   room; "Refresh" re-fetches on demand.
7. **Mark No-Show** — manually triggers the no-show sweep for a slot (a
   no-op if the slot's check-in window hasn't closed yet). The backend also
   runs this automatically every 60 seconds in the background.

Every error shown in the app (conflicts, room-full, invalid/used tickets,
not-found, etc.) is the backend's own message, verbatim — nothing is replaced
with a generic message.

## Assumptions made about ambiguous parts of the spec

- **CORS / integration**: the backend has no CORS configuration, and none was
  added. Instead, the frontend dev server proxies `/api/*` to
  `http://localhost:8080` (Vite's `server.proxy`), which needs no backend
  changes at all. This does mean the frontend only works via `npm run dev`
  (or a production build served/proxied the same way) — there's no
  standalone static-file deployment story here, since none was asked for.
- **Backend port**: assumed `8080`, since the backend has no
  `application.properties`/`.yml` setting a different port (Spring Boot's
  default).
- **Student creation**: not one of the seven listed screens, but registration
  requires an existing student id and there's no student-listing endpoint.
  Added as a small inline mini-form inside the Register Student screen
  (not a separate nav tab), per explicit confirmation.
- **Reschedule "same exam" rule**: originally read as a frontend-only
  constraint, but confirmed and escalated to a real backend rule — the
  backend now rejects a cross-exam reschedule with a new
  `DifferentExamRescheduleException` (400), with a unit test covering it.
  The frontend's "new slot" dropdown still also restricts itself to the same
  exam, but that's now a UX nicety on top of a real server-side guarantee,
  not the only thing preventing it.
- **No list-all endpoints for students or registrations**: the backend has
  no `GET /api/students` or `GET /api/registrations` (list-all). This means
  the UI cannot offer dropdowns for "pick an existing student" or "pick a
  registration to reschedule" — the user must carry IDs forward from a
  previous screen's output (registration id, student id, ticket id). This
  was not treated as something to fix by inventing new backend endpoints,
  per "don't guess or invent new ones."
- **"Refreshable on demand"** (Room Utilization) was read as a manual
  "Refresh" button, not automatic polling.
- **No-show marking UI**: the backend exposes this as *both* an automatic
  scheduled sweep (every 60 seconds) *and* a manual per-slot trigger
  (`POST /api/slots/{slotId}/no-shows/process`). Since a manual trigger
  exists, a screen was built for it rather than treating it as
  automatic-only.
- **Time input format**: the "Create Exam" slot form uses a native HTML
  `<input type="time">`, which yields `"HH:MM"` (no seconds). Verified this
  parses correctly against the backend's `LocalTime` field (Jackson's
  ISO-8601 time parsing accepts the seconds-omitted form).
- **No production deployment**: only local dev-mode running
  (`mvn spring-boot:run` + `npm run dev`) is documented, since the spec only
  asked for the two to run together, not to be deployed anywhere.
