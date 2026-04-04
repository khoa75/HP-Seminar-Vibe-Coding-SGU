# FinAlly Frontend

A stunning AI-powered trading workstation interface built with Next.js, TypeScript, and Tailwind CSS.

## Features

- **Real-time price updates** via SSE (Server-Sent Events)
- **Live watchlist** with flash animations (green for up, red for down)
- **Portfolio dashboard** showing positions, cash, and P&L
- **Quick trade panel** for buying and selling market orders
- **Connection status indicator** (green = connected, red = disconnected)
- **Dark Bloomberg-inspired theme** for professional trading feel

## Development

```bash
# Install dependencies
npm install

# Local development (requires backend on localhost:8000)
npm run dev

# Build for production (static export)
npm run build
```

## Color Scheme

- Background: `#0d1117`
- Border: `#30363d`
- Text: `#c9d1d9`
- Accent Yellow: `#ecad0a`
- Blue Primary: `#209dd7`
- Green (up): `#34a853`
- Red (down): `#f85149`
- Purple (submit): `#753991`

## Static Export

This project uses Next.js static export (`output: 'export'`), which means:
- No API routes in the frontend
- All backend calls go to `/api/*` endpoints (proxied by FastAPI)
- The built frontend is served as static files by the backend

Build output goes to `./out/` directory.
