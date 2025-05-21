import React from 'react';
import { Sidebar } from '../components/Sidebar/Sidebar.tsx';
import logo from '../assets/welcome_page.gif';

export default function Home() {
  return (
    <main className="grid gap-4 p-4 grid-cols-[220px,_1fr]">
      <Sidebar />
      <div className="space-y-6">
        <div className="bg-white rounded-lg shadow p-6 min-h-[calc(100vh-2rem)] flex items-center justify-center">
          <div className="text-center max-w-2xl">
            <h1 className="text-3xl font-semibold text-stone-900 mb-2">SalableCloudV2</h1>
            <h3 className="text-stone-500 mb-3">Metrics Dashboard</h3>
            <img src={logo}></img>
          </div>
        </div>
      </div>
    </main>
  );
} 