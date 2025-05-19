import React from 'react';
import { Dashboard } from '../components/Dashboard/Dashboard.tsx';
import { Sidebar } from '../components/Sidebar/Sidebar.tsx';

export default function Home() {
  return (
    <main className="grid gap-4 p-4 grid-cols-[220px,_1fr]">
      <Sidebar />
      <Dashboard />
    </main>
  );
} 